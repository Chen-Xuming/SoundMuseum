package com.example.soundmuseum.fm;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.soundmuseum.R;
import com.freedom.lauzy.playpauseviewlib.PlayPauseView;
import com.jaeger.library.StatusBarUtil;

import java.io.IOException;
import java.lang.reflect.Method;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class AudioFmActivity extends AppCompatActivity {


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
                    }
                }
            }
        });

        fm_next_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSong();
                if(mediaPlayer!= null && mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                if(fm_playPauseView.isPlaying()) fm_playPauseView.pause();
            }
        });

        loadSong();
    }


    private void loadSong(){

        AudioFmModel temp = AudioFmModel.test_getData();
        if(current_song != null){
            while(current_song.getId() == temp.getId()){
                temp = AudioFmModel.test_getData();
            }
        }
        current_song = temp;

        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            if(fm_playPauseView.isPlaying()) fm_playPauseView.pause();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlayerPrepared = false;
        }

        fm_title_text.setText(current_song.getSong_title());

        fm_time_right.setText(current_song.getDuration());

        // 圆形头像, 并加白边框
        //RequestOptions options = new RequestOptions()
        //        .transform(new CropCircleWithBorderTransformation(4, Color.WHITE));
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
                isPlayerPrepared = true;
                mediaPlayer.start();
                fm_playPauseView.play();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                fm_playPauseView.pause();
                loadSong();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        //load the file of menu that you created
        getMenuInflater().inflate(R.menu.fm_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.fm_menu_study) {
            //TODO do everything what you want
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // 让菜单同时显示图标和文字
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            mediaPlayer.release();
        }
    }
}
