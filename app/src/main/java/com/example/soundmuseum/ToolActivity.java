package com.example.soundmuseum;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.soundmuseum.fm.AudioFmActivity;
import com.jaeger.library.StatusBarUtil;

public class ToolActivity extends AppCompatActivity {

    private CardView cardView_analysis;
    private CardView cardView_convert;
    private CardView cardView_cutter;
    private CardView cardView_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);

        StatusBarUtil.setColor(this, Color.parseColor("#1A91FF"), 0);

        //////////// toolbar 初始化
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbox_toolbar);
        toolbar.setTitle("工具箱");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cardView_analysis = findViewById(R.id.toolbox_file_analysis);
        cardView_convert = findViewById(R.id.toolbox_format_convert);
        cardView_cutter = findViewById(R.id.toolbox_audio_cutter);
        cardView_db = findViewById(R.id.toolbox_db_measure);

        cardView_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cardView_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cardView_cutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cardView_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
