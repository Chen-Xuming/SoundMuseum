package com.example.soundmuseum.fm;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.soundmuseum.R;
import com.example.soundmuseum.record.Record;
import com.freedom.lauzy.playpauseviewlib.PlayPauseView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jaeger.library.StatusBarUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AudioFmActivity extends AppCompatActivity {
    private Context context;

    private ImageView fm_album_img;
    private TextView fm_title_text;
    private TextView fm_time_left;
    private TextView fm_time_right;
    private AppCompatSeekBar fm_seekbar;
    private PlayPauseView fm_playPauseView;
    private ImageView fm_next_song;

    private AudioFmModel current_song = null;

    private MediaPlayer mediaPlayer;
    private boolean isPlayerPrepared = false;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_fm);

        StatusBarUtil.setTranslucent(AudioFmActivity.this);

        fm_album_img = findViewById(R.id.fm_album_img);
        fm_title_text = findViewById(R.id.fm_title_text);
        fm_time_left = findViewById(R.id.fm_time_left);
        fm_time_right = findViewById(R.id.fm_time_right);
        fm_seekbar = findViewById(R.id.fm_seekbar);
        fm_playPauseView = findViewById(R.id.fm_play_pause_btn);
        fm_next_song = findViewById(R.id.fm_next_img);

        mHandler = new Handler();

        //////////// toolbar 初始化
        Toolbar toolbar = (Toolbar) findViewById(R.id.fm_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 点击返回箭头退出界面
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null && mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    //mediaPlayer.release();
                }
                finish();
            }
        });
        /////////////////////

        fm_playPauseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fm_playPauseView.isPlaying()){
                    fm_playPauseView.pause();
                    if(mediaPlayer!= null && mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    }
                }else{
                    if(isPlayerPrepared && !mediaPlayer.isPlaying()){
                        mediaPlayer.start();
                        fm_playPauseView.play();
                        mHandler.postDelayed(mRunnable, 1000);
                    }
                }
            }
        });

        /*
            进度条监听
        */
        fm_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mediaPlayer.seekTo(progress);

                    if(!mediaPlayer.isPlaying()){
                        int run_time = mediaPlayer.getCurrentPosition();
                        String run_str = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) run_time),
                                TimeUnit.MILLISECONDS.toSeconds((long) run_time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) run_time)));
                        fm_time_left.setText(run_str);
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

        fm_next_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSong();
                if(mediaPlayer!= null && mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                if(fm_playPauseView.isPlaying()) fm_playPauseView.pause();
            }
        });

        requestSong();
    }


    private void requestSong(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://1.15.157.176/soundmuseum/web/index.php?r=sound/randfm")
                .build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(context, "网络不佳，请重试。", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result = response.body().string();

                final JsonObject jsonObject  = JsonParser.parseString(result).getAsJsonObject();

                if(jsonObject.get("code").getAsInt() == 1){

                    JsonObject fm = jsonObject.get("data").getAsJsonObject();
                    if(current_song != null && current_song.getId() == fm.get("id").getAsInt()){
                        requestSong();
                        return;
                    }

                    AudioFmModel audioFmModel = new AudioFmModel(
                            fm.get("id").getAsInt(),
                            fm.get("song_name").getAsString(),
                            fm.get("song_url").getAsString(),
                            fm.get("album_url").getAsString(),
                            fm.get("duration").getAsInt(),
                            0
                    );

                    current_song = audioFmModel;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadSong();
                        }
                    });
                }
            }
        });
    }


    private void loadSong(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            if(fm_playPauseView.isPlaying()) fm_playPauseView.pause();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlayerPrepared = false;
        }

        fm_title_text.setText(current_song.getSong_title());

        String right_time = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) current_song.getDuration()),
                TimeUnit.MILLISECONDS.toSeconds((long) current_song.getDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) current_song.getDuration())));
        fm_time_right.setText(right_time);

        // 圆角图片
        RequestOptions options = new RequestOptions()
                .transform(new RoundedCornersTransformation(20, 0));
        Glide.with(this)
                .load(current_song.getSong_album_image())
                .apply(options)
                .into(fm_album_img);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(false);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                fm_time_left.setText("00:00");
                fm_seekbar.setProgress(0);
                fm_seekbar.setMax(mediaPlayer.getDuration());

                int total_time = mediaPlayer.getDuration();
                String total_str = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) total_time),
                        TimeUnit.MILLISECONDS.toSeconds((long) total_time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) total_time)));
                fm_time_right.setText(total_str);

                isPlayerPrepared = true;
                mediaPlayer.start();
                fm_playPauseView.play();

                mHandler.postDelayed(mRunnable, 1000);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                fm_playPauseView.pause();
                requestSong();
            }
        });

        try {
            if(current_song.getSong_uri() != null){
                mediaPlayer.setDataSource(current_song.getSong_uri());
                mediaPlayer.prepareAsync();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }


    @Override
    protected void onPause(){
        super.onPause();
//        if(mediaPlayer != null && mediaPlayer.isPlaying()){
//            mediaPlayer.pause();
//            fm_playPauseView.pause();
//            //mediaPlayer.release();
//        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
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
                    fm_seekbar.setProgress(mediaPlayer.getCurrentPosition());
                    int run_time = mediaPlayer.getCurrentPosition();
                    String run_str = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) run_time),
                            TimeUnit.MILLISECONDS.toSeconds((long) run_time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) run_time)));
                    fm_time_left.setText(run_str);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
