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

import java.io.File;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;
import cafe.adriel.androidaudioconverter.model.BitRate;

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
                startActivity(new Intent(MainActivity.this, DemoActivity.class));
            }
        });
    }

    void audioFormatConvert(){
        File flacFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Recordings/Standard Recordings/FormatTest.flac");
        Log.d("file-name", flacFile.getAbsolutePath());
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                Log.d("convert-file-info", convertedFile.getPath());
                // So fast? Love it!
            }
            @Override
            public void onFailure(Exception error) {
                error.printStackTrace();
                Log.d("convert-file-info", "error");
                // Oops! Something went wrong
            }
        };
        AndroidAudioConverter.with(this)
                // Your current audio file
                .setFile(flacFile)

                // Your desired audio format
                .setFormat(AudioFormat.MP3)

                // An optional method for your desired sample rate
                .setSampleRate(16000)
                // .setSampleRate(16000) // when not used original sample rate is kept.

                // An optional method for your desired bitrate
                .setBitRate(BitRate.s16)
                // .setBitRate(BitRate.s16) // when not used original bitrate is kept.

                //An optional method for if output is desired as mono channel or as original
                .setMono(true)
                // .setMono(true) // when not used original channel configuration is kept.

                // An callback to know when conversion is finished
                .setCallback(callback)

                // Start conversion
                .convert();
    }
}
