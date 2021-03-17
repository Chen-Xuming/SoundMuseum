package com.example.soundmuseum.dbMeter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;

import com.example.soundmuseum.R;
import com.freedom.lauzy.playpauseviewlib.PlayPauseView;
import com.jaeger.library.StatusBarUtil;

public class DBMeterActivity extends AppCompatActivity {

    private TextView textView_current_db;
    private TextView textView_description;
    private TextView textView_max;
    private TextView textView_average;
    private TextView textView_min;
    private PlayPauseView playPauseView;

    private float volume = 10000;
    private static MyMediaRecorder mRecorder;
    private static final int msgWhat = 0x1001;
    private static final int refreshTime = 100;

    private float max = -1;
    private float min = 1000000;
    private int average = 0;
    private float count = 0;
    private float sum = 0;

    private Handler handler;

    private String [] descriptions = {
            "树叶掉落",     // [0,10)
            "树叶掉落",     // [10,20)
            "耳语",         //  30
            "安静的图书馆",  //  40
            "中雨",         //  50
            "正常交谈",      //  60
            "嘈杂马路",      //  70
            "闹钟",          //  80
            "电动工具",       //  90
            "雪地摩托",       //  100
            "汽车喇叭",       //  110
            "喷气式飞机起飞",  //  120
            "喷气式飞机起飞",
            "喷气式飞机起飞",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbmeter);

        StatusBarUtil.setTranslucent(this);

        mRecorder = new MyMediaRecorder();

        handler = new Handler();

        textView_current_db = findViewById(R.id.dbmeter_current_value);
        textView_description = findViewById(R.id.dbmeter_description);
        textView_max = findViewById(R.id.dbmeter_max);
        textView_min = findViewById(R.id.dbmeter_min);
        textView_average = findViewById(R.id.dbmeter_average);
        playPauseView = findViewById(R.id.dbmeter_playpauseview);

        playPauseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playPauseView.isPlaying()){
                    playPauseView.pause();
                    handler.removeCallbacksAndMessages(null);
                }else{
                    playPauseView.play();
                    handler.postDelayed(runnable, 300);
                }
            }
        });
    }


    private void startListenAudio() {
        //handler.sendEmptyMessageDelayed(msgWhat, refreshTime);
        handler.postDelayed(runnable, 300);
    }

    /**
     * 开始记录
     * @param fFile
     */
    public void startRecord(File fFile){
        try{
            mRecorder.setMyRecAudioFile(fFile);
            if (mRecorder.startRecorder()) {
                startListenAudio();
            }else{
                Toast.makeText(this, "启动录音失败", Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){
            Toast.makeText(this, "录音机已被占用或录音权限被禁止", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            volume = mRecorder.getMaxAmplitude();  //获取声压值
            if(volume > 0 && volume < 1000000){

                float db_value = 20 * (float)(Math.log10(volume));
                int value = (int)db_value;
                count++;
                sum += db_value;
                if(value < min){
                    min = value;
                    textView_min.setText((int)min + "");
                }
                if(value > max){
                    max = value;
                    textView_max.setText((int)max + "");
                }
                average = (int)(sum / count);
                textView_average.setText(average + "");
                textView_current_db.setText(value + "");

                textView_description.setText(descriptions[(value % 140) / 10]);
            }
            handler.postDelayed(runnable, 300);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        File file = FileUtil.createFile("temp.amr");
        if (file != null) {
            Log.v("file", "file =" + file.getAbsolutePath());
            startRecord(file);
            playPauseView.play();
        } else {
            Toast.makeText(getApplicationContext(), "创建文件失败", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 停止记录
     */
    @Override
    protected void onPause() {
        super.onPause();
        mRecorder.delete(); //停止记录并删除录音文件
        //handler.removeMessages(msgWhat);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        //handler.removeMessages(msgWhat);
        mRecorder.delete();
        super.onDestroy();
    }
}
