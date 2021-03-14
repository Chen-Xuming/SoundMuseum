package com.example.soundmuseum.map;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.example.soundmuseum.R;
import com.freedom.lauzy.playpauseviewlib.PlayPauseView;
import com.jaeger.library.StatusBarUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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
