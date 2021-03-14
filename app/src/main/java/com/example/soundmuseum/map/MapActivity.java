package com.example.soundmuseum.map;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
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
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapsdkexample.util.clusterutil.clustering.Cluster;
import com.baidu.mapsdkexample.util.clusterutil.clustering.ClusterManager;
import com.example.soundmuseum.R;
import com.freedom.lauzy.playpauseviewlib.PlayPauseView;
import com.jaeger.library.StatusBarUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private ClusterManager<MapItem> mClusterManager;

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
        mLayout.setAnchorPoint(0.6f);
        //mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);

        /*
                地图初始化
         */
        mMapView = (MapView) findViewById(R.id.map_mapview);
        mBaiduMap = mMapView.getMap();

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // 初始化点聚合管理类
                initCluster();
                // 加载点
                loadPoints();
            }
        });
    }

    // 大批量加载标记
    private void loadPoints(){
        List<MapItem> items = new ArrayList<MapItem>();
        for(int i = 0; i < Points.points.length; i++){
            LatLng point = new LatLng(Points.points[i].getLat(), Points.points[i].getLng());
            items.add(new MapItem(point));
        }

        Log.d("loadPoints", "" + Points.points.length);
        mClusterManager.addItems(items);
        Log.d("loadPoints", "done");
    }

    private void initCluster() {
        // 定义点聚合管理类ClusterManager
        mClusterManager = new ClusterManager<MapItem>(this, mBaiduMap);
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        mBaiduMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager
                .setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MapItem>() {

                    @Override
                    public boolean onClusterClick(Cluster<MapItem> cluster) {
                        List<MapItem> items = (List<MapItem>) cluster.getItems();
                        LatLngBounds.Builder builder2 = new LatLngBounds.Builder();
                        int i=0;
                        for(MapItem myItem : items){
                            builder2 = builder2.include(myItem.getPosition());
                        }
                        LatLngBounds latlngBounds = builder2.build();
                        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(latlngBounds,mMapView.getWidth(),mMapView.getHeight());
                        mBaiduMap.animateMapStatus(u);
                        return false;
                    }
                });
        mClusterManager.setOnClusterItemClickListener(
                new ClusterManager.OnClusterItemClickListener<MapItem>() {
                    @Override
                    public boolean onClusterItemClick(MapItem item) {
                        Toast.makeText(MapActivity.this, "点击单个Item", Toast.LENGTH_SHORT)
                                .show();
                        return false;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}
