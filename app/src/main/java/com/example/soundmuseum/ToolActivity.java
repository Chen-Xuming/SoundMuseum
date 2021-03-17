package com.example.soundmuseum;

import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.soundmuseum.dbMeter.DBMeterActivity;
import com.example.soundmuseum.fm.AudioFmActivity;
import com.jaeger.library.StatusBarUtil;
import com.ringtone.maker.Activities.Activity_Editor;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.AudioPickActivity;
import com.vincent.filepicker.filter.entity.AudioFile;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ToolActivity extends AppCompatActivity {

    private CardView cardView_analysis;
    private CardView cardView_convert;
    private CardView cardView_cutter;
    private CardView cardView_db;

    private String filePath = null;

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
                pickFile();
            }
        });

        cardView_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ToolActivity.this, DBMeterActivity.class));
            }
        });
    }

    void pickFile(){
        Intent intent = new Intent(this, AudioPickActivity.class);
        intent.putExtra(Constant.MAX_NUMBER, 1);
        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_AUDIO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constant.REQUEST_CODE_PICK_AUDIO && resultCode == RESULT_OK){
            ArrayList<AudioFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_AUDIO);
            if(!list.isEmpty()){
                filePath = list.get(0).getPath();
                toCut();
            }
        }
    }

    void toCut() {
        if(filePath != null){

            String temp = filePath;
            String title = filePath.substring(
                    filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
            filePath = null;

            //Toast.makeText(this, title, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(ToolActivity.this, Activity_Editor.class);
            intent.putExtra("path", temp);
            intent.putExtra("title", title);
            startActivity(intent);
        }
    }
}
