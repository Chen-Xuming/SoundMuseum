package com.example.soundmuseum;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.soundmuseum.fm.AudioFmActivity;
import com.example.soundmuseum.map.MapActivity;
import com.vincent.filepicker.activity.AudioPickActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button btn_map;
    private Button btn_format;
    private Button btn_kotlin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_map = (Button)findViewById(R.id.btn_map_act);
        btn_format = (Button)findViewById(R.id.btn_format_convert);
        btn_kotlin = findViewById(R.id.btn_kotlin_act);

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        btn_format.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                audioFormatConvert();
            }
        });

        btn_kotlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ToolActivity.class));
            }
        });
    }

    void audioFormatConvert(){
        File flacFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Recordings/Standard Recordings/FormatTest.flac");


        Log.d("file-name", flacFile.getAbsolutePath());

    }
}
