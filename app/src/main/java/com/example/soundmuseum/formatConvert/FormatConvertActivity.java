package com.example.soundmuseum.formatConvert;

import android.graphics.Color;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soundmuseum.R;
import com.jaeger.library.StatusBarUtil;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.ringtone.maker.Views.CustomButton;

import java.io.File;
import java.text.Normalizer;

import androidx.annotation.ColorRes;
import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;
import cafe.adriel.androidaudioconverter.model.BitRate;
import de.mateware.snacky.Snacky;


public class FormatConvertActivity extends AppCompatActivity {

    private String filePath;
    private String fileName;

    private MaterialSpinner materialSpinner_format;
    private MaterialSpinner materialSpinner_bitrate;
    private MaterialSpinner materialSpinner_samplerate;
    private MaterialSpinner materialSpinner_channel;
    private TextView textView_file;
    private CustomButton customButton_convert;

    private AudioFormat audioFormat;
    private int sample_rate;
    private BitRate bitRate;
    private boolean channel;        // true = single;

    private KProgressHUD kProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_format_convert);

        StatusBarUtil.setColor(this, Color.parseColor("#1267B8"), 100);

        //////////// toolbar 初始化
        Toolbar toolbar = (Toolbar) findViewById(R.id.convert_toolbar);
        toolbar.setTitle("格式转换");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        filePath = getIntent().getStringExtra("path");

        materialSpinner_format = findViewById(R.id.convert_spinner_format);
        materialSpinner_bitrate = findViewById(R.id.convert_spinner_bitrate);
        materialSpinner_samplerate = findViewById(R.id.convert_spinner_samplerate);
        materialSpinner_channel = findViewById(R.id.convert_spinner_channel);
        textView_file = findViewById(R.id.convert_filepath);
        customButton_convert = findViewById(R.id.convert_done);

        textView_file.setText(filePath);

        audioFormat = AudioFormat.MP3;
        sample_rate = 44100;
        bitRate = BitRate.s16;
        channel = true;

        materialSpinner_format.setItems("MP3", "WAV", "M4A", "WMA", "AAC", "FLAC");
        materialSpinner_format.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                switch (item){
                    case "MP3":
                        audioFormat = AudioFormat.MP3;
                        break;
                    case "WAV":
                        audioFormat = AudioFormat.WAV;
                        break;
                    case "M4A":
                        audioFormat = AudioFormat.M4A;
                        break;
                    case "WMA":
                        audioFormat = AudioFormat.WMA;
                        break;
                    case "AAC":
                        audioFormat = AudioFormat.AAC;
                        break;
                    case "FLAC":
                        audioFormat = AudioFormat.FLAC;
                        break;

                        default: audioFormat  = AudioFormat.MP3;
                }
            }
        });

        materialSpinner_bitrate.setItems("192kbps", "320kbps", "256kbps",
                "128kbps", "96kbps", "64kbps");
        materialSpinner_bitrate.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

            }
        });

        materialSpinner_samplerate.setItems("44100Hz", "22050Hz", "32000Hz", "48000Hz");
        materialSpinner_samplerate.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                switch (item){
                    case "44100Hz":
                        sample_rate = 44100;
                        break;
                    case "22050Hz":
                        sample_rate = 22050;
                        break;
                    case "32000Hz":
                        sample_rate = 32000;
                        break;
                    case "48000Hz":
                        sample_rate = 48000;
                        break;

                        default:
                            sample_rate = 44100;
                }
            }
        });

        materialSpinner_channel.setItems("单声道", "立体声");
        materialSpinner_channel.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                switch (item){
                    case "单声道":
                        channel = true;
                        break;
                    case "立体声":
                        channel = false;
                        break;
                        default:
                            channel = true;
                }
            }
        });

        customButton_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kProgressHUD = KProgressHUD.create(FormatConvertActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("正在转换，请稍等...")
                        .setCancellable(true)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();
                convert();
            }
        });
    }

    private void convert(){
        File file = new File(filePath);

        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {

                Log.d("converted_file", convertedFile.getAbsolutePath());
                //Toast.makeText(FormatConvertActivity.this, "Success", Toast.LENGTH_SHORT).show();

                kProgressHUD.dismiss();

                Snacky.builder()
                        .setActivty(FormatConvertActivity.this)
                        .setBackgroundColor(ContextCompat.getColor(FormatConvertActivity.this ,R.color.editor_toast_color))
                        .setText("已保存至" + convertedFile.getAbsolutePath())
                .setDuration(Snacky.LENGTH_LONG)
                .success().show();

            }
            @Override
            public void onFailure(Exception error) {
                // Oops! Something went wrong

                kProgressHUD.dismiss();
                Toast.makeText(FormatConvertActivity.this, "抱歉，不支持改格式或文件错误", Toast.LENGTH_SHORT).show();

                Log.d("converted_file", "error");
            }
        };
        AndroidAudioConverter.with(this)
                // Your current audio file
                .setFile(file)

                // Your desired audio format
                .setFormat(audioFormat)

                // An optional method for your desired sample rate
                .setSampleRate(sample_rate)
                // .setSampleRate(16000) // when not used original sample rate is kept.

                // An optional method for your desired bitrate
                .setBitRate(BitRate.s16)
                // .setBitRate(BitRate.s16) // when not used original bitrate is kept.

                //An optional method for if output is desired as mono channel or as original
                .setMono(channel)
                // .setMono(true) // when not used original channel configuration is kept.

                // An callback to know when conversion is finished
                .setCallback(callback)

                // Start conversion
                .convert();
    }
}
