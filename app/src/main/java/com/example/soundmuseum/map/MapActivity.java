package com.example.soundmuseum.map;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapsdkexample.util.clusterutil.clustering.Cluster;
import com.baidu.mapsdkexample.util.clusterutil.clustering.ClusterManager;
import com.baidu.mapsdkexample.util.clusterutil.projection.Point;
import com.example.soundmuseum.R;
import com.freedom.lauzy.playpauseviewlib.PlayPauseView;
import com.jaeger.library.StatusBarUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MapActivity extends AppCompatActivity {

    private SlidingUpPanelLayout mLayout;
    private TextView textView_title;
    private AppCompatSeekBar seekbar;
    private TextView textView_time_left;
    private TextView textView_time_right;
    private PlayPauseView playPauseView;
    private TextView textView_author_date;
    private TextView textView_place;
    private TextView textView_content;
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private MediaPlayer mediaPlayer;
    private boolean isPlayerPrepared = false;
    private Handler mHandler;

    private ClusterManager<MapModel> mClusterManager;

    private MapModel current_model = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        textView_title = findViewById(R.id.map_title);
        seekbar = findViewById(R.id.map_seekbar);
        textView_time_left = findViewById(R.id.map_time_left);
        textView_time_right = findViewById(R.id.map_time_right);
        playPauseView = findViewById(R.id.map_playpauseview);
        textView_author_date = findViewById(R.id.map_author_and_date);
        textView_place = findViewById(R.id.map_place);
        textView_content = findViewById(R.id.map_content);


        mHandler = new Handler();

        /*
                SlidingUpPanel初始化
         */
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.map_sliding_layout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        mLayout.setAnchorPoint(0.5f);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);  // 未点击任意音频之前隐藏

        /*
                地图初始化
         */
        mMapView = (MapView) findViewById(R.id.map_mapview);
        mMapView.showZoomControls(false);                   // 不显示缩放按钮
        mBaiduMap = mMapView.getMap();

        UiSettings uiSettings = mBaiduMap.getUiSettings();
        uiSettings.setRotateGesturesEnabled(false);

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // 初始化点聚合管理类
                initCluster();
                // 加载点
                loadPoints();
                // 设置初始视图范围
                LatLng center = new LatLng(33.513286, 109.552645);
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(center, 5.5f);
                mBaiduMap.setMapStatus(mapStatusUpdate);
            }
        });


        /*
                播放按钮监听
         */
        playPauseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playPauseView.isPlaying()){
                    playPauseView.pause();
                    if(mediaPlayer!= null && mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    }
                }else{
                    if(isPlayerPrepared && !mediaPlayer.isPlaying()){
                        mediaPlayer.start();
                        playPauseView.play();
                        mHandler.postDelayed(mRunnable, 1000);
                    }
                }
            }
        });

        /*
            进度条监听
        */
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mediaPlayer.seekTo(progress);

                    if(!mediaPlayer.isPlaying()){
                        int run_time = mediaPlayer.getCurrentPosition();
                        String run_str = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) run_time),
                                TimeUnit.MILLISECONDS.toSeconds((long) run_time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) run_time)));
                        textView_time_left.setText(run_str);
                    }

                    mHandler.postDelayed(mRunnable, 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    // 大批量加载标记
    private void loadPoints(){
        List<MapModel> items = new ArrayList<MapModel>();
        for(int i = 0; i < Points.points.length; i++){
            MapModel mapModel = new MapModel(
                    Points.points[i].getLat(),
                    Points.points[i].getLng(),
                    Points.titles[i % 10],
                    "3:21",
                    "chen",
                    "2020.10.2",
                    "江苏省苏州市",
                    "荆州机场今年春节期间新建成通航，很多老人在家人陪伴下来看航班起降。我们也把它当成晚饭后的消遣。晚上九点左右，机场已经关闭了，没有航班，它旁边的田野里蛙声响成一片。",
                    Points.uris[i % 10]
            );
            items.add(mapModel);
        }

        Log.d("loadPoints", "" + Points.points.length);
        mClusterManager.addItems(items);
        Log.d("loadPoints", "done");
    }

    private void initCluster() {
        // 定义点聚合管理类ClusterManager
        mClusterManager = new ClusterManager<MapModel>(this, mBaiduMap);
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        mBaiduMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager
                .setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MapModel>() {

                    @Override
                    public boolean onClusterClick(Cluster<MapModel> cluster) {
                        List<MapModel> items = (List<MapModel>) cluster.getItems();
                        LatLngBounds.Builder builder2 = new LatLngBounds.Builder();
                        int i=0;
                        for(MapModel myItem : items){
                            builder2 = builder2.include(myItem.getPosition());
                        }
                        LatLngBounds latlngBounds = builder2.build();
                        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(latlngBounds,mMapView.getWidth(),mMapView.getHeight());
                        mBaiduMap.animateMapStatus(u);
                        return false;
                    }
                });
        mClusterManager.setOnClusterItemClickListener(
                new ClusterManager.OnClusterItemClickListener<MapModel>() {
                    @Override
                    public boolean onClusterItemClick(MapModel item) {
                        if(item == current_model) return false;
                        current_model = item;
                        loadSong(item);
                        return false;
                    }
                });
    }

    /*
            加载歌曲
     */
    private void loadSong(MapModel mapModel){
//        if(mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN){
//            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
//        }

        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);

        textView_title.setText(mapModel.getTitle());
        textView_time_right.setText(mapModel.getS_duration());
        textView_author_date.setText(mapModel.getAuthor_id() + " / " + mapModel.getS_time());
        textView_place.setText(mapModel.getAddress());
        textView_content.setText(mapModel.getContent());

        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playPauseView.pause();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            isPlayerPrepared = false;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                textView_time_left.setText("00:00");
                seekbar.setProgress(0);
                seekbar.setMax(mediaPlayer.getDuration());

                int total_time = mediaPlayer.getDuration();
                String total_str = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) total_time),
                        TimeUnit.MILLISECONDS.toSeconds((long) total_time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) total_time)));
                textView_time_right.setText(total_str);

                isPlayerPrepared = true;
                mediaPlayer.start();
                playPauseView.play();
                mHandler.postDelayed(mRunnable, 1000);
            }
        });
        try {
            if(mapModel.getAudio_url() != null){
                mediaPlayer.setDataSource(mapModel.getAudio_url());
                mediaPlayer.prepareAsync();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            if(mediaPlayer != null && mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                playPauseView.pause();
            }
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            playPauseView.pause();
            //mediaPlayer.release();
        }
    }

    /*
            播放进度（seekbar, 时间）更新
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mHandler.postDelayed(mRunnable, 1000);

                    seekbar.setProgress(mediaPlayer.getCurrentPosition());

                    int run_time = mediaPlayer.getCurrentPosition();
                    String run_str = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) run_time),
                            TimeUnit.MILLISECONDS.toSeconds((long) run_time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) run_time)));
                    textView_time_left.setText(run_str);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
