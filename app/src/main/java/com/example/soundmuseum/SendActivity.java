package com.example.soundmuseum;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.soundmuseum.location.LocationActivity;
import com.example.soundmuseum.util.PermissionUtil;
import com.example.soundmuseum.util.UserManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kaopiz.kprogresshud.KProgressHUD;


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;
import nl.changer.audiowife.AudioWife;
import okhttp3.Address;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.os.Environment.DIRECTORY_MUSIC;

public class SendActivity extends AppCompatActivity {

    private static final String TAG = SendActivity.class.getSimpleName();

    private PermissionUtil permissionUtil;


    /* 播放器 */
    private LinearLayout player;
    private Context mContext;
    private View mPlayMedia;
    private View mPauseMedia;
    private ImageView mDelete;
    private SeekBar mMediaSeekBar;
    private TextView mRunTime;
    private TextView mTotalTime;
    private TextView mPlaybackTime;
    private Uri mUri = null;
    private static final int INTENT_PICK_AUDIO = 1;

    private String address = null;
    private Double Lng;
    private Double Lat;

    TextView textView_location;
    ImageButton imgbtn_location_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_send);

        permissionUtil = new PermissionUtil(this);
        permissionUtil.requestPermission();

        Toolbar toolbar = (Toolbar) findViewById(R.id.send_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 点击返回箭头退出界面
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // 选择音频文件
        ImageButton imgbtn_select = findViewById(R.id.send_select_audio);
        imgbtn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickAudio();
            }
        });

        // 现场录音
        ImageButton imgbtn_create = findViewById(R.id.send_create_audio);
        imgbtn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordAudio();
            }
        });


        // 选择定位
        ImageButton imgbtn_location = findViewById(R.id.send_select_location);
        imgbtn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SendActivity.this, LocationActivity.class),
                        101);
            }
        });
        textView_location = findViewById(R.id.send_address);
        textView_location.setVisibility(View.GONE);
        imgbtn_location_delete = findViewById(R.id.send_location_delete);
        imgbtn_location_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgbtn_location_delete.setVisibility(View.GONE);
                textView_location.setVisibility(View.GONE);
                address = null;
            }
        });
        imgbtn_location_delete.setVisibility(View.GONE);

        initPlayer();
    }

    /*
            初始化播放器
     */
    private void initPlayer(){
        mContext = this;

        // initialize the player controls
        player = findViewById(R.id.send_player_layout);
        mPlayMedia = findViewById(R.id.send_player_play);
        mPauseMedia = findViewById(R.id.send_player_pause);
        mMediaSeekBar = (SeekBar) findViewById(R.id.send_player_seekbar);
        mRunTime = (TextView) findViewById(R.id.send_player_runtime);
        mTotalTime = (TextView) findViewById(R.id.send_palyer_totaltime);
        mDelete = findViewById(R.id.send_audio_delete);

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioWife.getInstance().pause();
                AudioWife.getInstance().release();
                mUri = null;
                player.setVisibility(View.GONE);
            }
        });

        player.setVisibility(View.GONE);
    }


    /*
            选择音频文件
     */
    private void pickAudio() {
        AudioWife.getInstance().pause();
        //AudioWife.getInstance().release();

        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Music");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        startActivityForResult(intent, 100);
    }


    /*
            现场录音
     */
    private void recordAudio(){

        AudioWife.getInstance().pause();

        String filePath = Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC)
                .getAbsolutePath();
        int requestCode = 0;
        AndroidAudioRecorder.with(SendActivity.this)
                // Required
                .setFilePath(filePath)
                .setColor(Color.parseColor("#0d1220"))
                .setRequestCode(requestCode)

                // Optional
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.STEREO)
                .setSampleRate(AudioSampleRate.HZ_48000)
                .setAutoStart(false)
                .setKeepDisplayOn(true)

                // Start recording
                .record();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            String s = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
            s = uri.getPath().replace("/document/primary:", s);
            mUri = Uri.parse(s);

            Log.d("send_pick", s);

            setPlayer();
        }



        /*
        *       录音完毕后，配置播放器
        * */
        if(requestCode == 0){
            if (resultCode == RESULT_OK) {
                String mp3 = data.getStringExtra("mp3_file_path");
                mUri = Uri.parse(mp3);
                Toast.makeText(this, "录音已保存至:\n" + mp3,
                        Toast.LENGTH_SHORT).show();

                setPlayer();

            } else if (resultCode == RESULT_CANCELED) {
                //Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }else if(resultCode == 2){
                Toast.makeText(this, "抱歉，录音保存失败。", Toast.LENGTH_SHORT).show();
            }
        }


        /*
                选取地址成功
         */
        if(requestCode == 101 && resultCode == RESULT_OK){
            address = data.getStringExtra("address");
            Lat = data.getDoubleExtra("Lat", 0);
            Lng = data.getDoubleExtra("Lng", 0);
            textView_location.setVisibility(View.VISIBLE);
            imgbtn_location_delete.setVisibility(View.VISIBLE);
            textView_location.setText(address);
        }
    }

    /*
            得到文件后，装载音频
     */
    private void setPlayer(){

        player.setVisibility(View.VISIBLE);

        AudioWife.getInstance().pause();
        AudioWife.getInstance().release();

        AudioWife.getInstance()
                .init(mContext, mUri)
                .setPlayView(mPlayMedia)
                .setPauseView(mPauseMedia)
                .setSeekBar(mMediaSeekBar)
                .setRuntimeView(mRunTime)
                .setTotalTimeView(mTotalTime);

        AudioWife.getInstance().addOnPlayClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(getBaseContext(), "Play", Toast.LENGTH_SHORT).show();
                // get-set-go. Lets dance.
                AudioWife.getInstance().play();
            }
        });

        AudioWife.getInstance().addOnPauseClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(getBaseContext(), "Pause", Toast.LENGTH_SHORT).show();
                // Your on audio pause stuff.
                AudioWife.getInstance().pause();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.send_send) {
            sendAudio();
        }

        return super.onOptionsItemSelected(item);
    }

    /*
            发布音频:
            1. 检查内容是否填好、是否有音频
            2. 发送并回馈结果
     */
    private void sendAudio(){

        EditText edit_title = findViewById(R.id.send_title);
        EditText edit_description = findViewById(R.id.send_description);

        String str1 = edit_title.getText().toString().trim();
        String str2 = edit_description.getText().toString().trim();

        if(str1.equals("") || str2.equals("")){
            Toast.makeText(SendActivity.this, "请将内容填写完整。", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mUri == null){
            Toast.makeText(SendActivity.this, "请选择音频。", Toast.LENGTH_SHORT).show();
            return;
        }

        upLoadFile(str1, str2);
    }

    /*
             先上传音频后发布动态
     */
    private void upLoadFile(final String title, final String description){

        String filepath = mUri.getPath();
        File file = new File(filepath);
        String filename = file.getName();

        Log.d("create_record", "filePath:" + filepath + "\nfilename:" + filename);


        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("sound", filename,
                        RequestBody.create(MediaType.parse("multipart/form-data"),
                                new File(filepath)))
                .build();

        Request request = new Request.Builder()
                .url("http://1.15.157.176/soundmuseum/web/index.php?r=sound/uploadmusic")
                .post(requestBody)
                .build();

        Call call = client.newCall(request);

        final KProgressHUD hud =
                KProgressHUD.create(SendActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("请稍等")
                        .setCancellable(false)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                hud.dismiss();
                Looper.prepare();
                Toast.makeText(SendActivity.this, "发布失败，请重试。", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result = response.body().string();

                final JsonObject jsonObject  = JsonParser.parseString(result).getAsJsonObject();

                if(jsonObject.get("code").getAsInt() == 1){
                    String url = jsonObject.get("data").getAsString();
                    sendRecord(title, description, url);
                    hud.dismiss();
                }else{
                    hud.dismiss();
                    Looper.prepare();
                    Toast.makeText(SendActivity.this, "发布失败，请重试。", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        });
    }

    private void sendRecord(final String title, final String description, String soundFileUrl){
        String api = "http://1.15.157.176/soundmuseum/web/index.php?r=sound/publish";

        final String soundFileUrl_ = "http://1.15.157.176/" + soundFileUrl;

        final String username = UserManager.getCurrentUser().getUsername();

        int duration = 0;

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(mUri.toString());
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();
            mediaPlayer.release();
        }catch (IOException e){
            e.printStackTrace();
        }

        final int duration_ = duration;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate =  new Date(System.currentTimeMillis());
        final String create_time = formatter.format(curDate);


        Log.d("create_record", "\nusername:" + username + "\ntitle:" + title
                    + "\ndescription:" + description + "\nsoundFileUrl:" + soundFileUrl_
                    + "\nduration:" + duration + "\ncreate_time:" + create_time);

        OkHttpClient client = new OkHttpClient();

        RequestBody body;

        if(address == null){

            body = new FormBody.Builder()
                    .add("username", username)
                    .add("title", title)
                    .add("description", description)
                    .add("sound_url", soundFileUrl_)
                    .add("duration", String.valueOf(duration))
                    .add("create_time", create_time)
                    .build();

        }else {
            body = new FormBody.Builder()
                    .add("username", username)
                    .add("title", title)
                    .add("description", description)
                    .add("sound_url", soundFileUrl_)
                    .add("duration", String.valueOf(duration))
                    .add("create_time", create_time)
                    .add("address", address)
                    .add("lat", Lat + "")
                    .add("lng", Lng + "")
                    .build();
        }
        Request request = new Request.Builder()
                .url(api).post(body)
                .build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(SendActivity.this, "网络不佳，请重试。", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result = response.body().string();

                final JsonObject jsonObject  = JsonParser.parseString(result).getAsJsonObject();

                if(jsonObject.get("code").getAsInt() == 1){
                    final int id = jsonObject.get("data").getAsInt();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SendActivity.this, "发布成功", Toast.LENGTH_SHORT).show();

                            AudioWife.getInstance().pause();
                            AudioWife.getInstance().release();

                            Intent intent = new Intent(SendActivity.this, MainActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("title", title);
                            intent.putExtra("description", description);
                            intent.putExtra("soundFileUrl", soundFileUrl_);
                            intent.putExtra("duration", duration_);
                            intent.putExtra("create_time", create_time);
                            intent.putExtra("id", id);
                            intent.putExtra("headpic", UserManager.getCurrentUser().getHeadPic_url());

                            intent.putExtra("create_a_record", "yes");


                            Log.d("intent_value", "\nusername:" + username + "\ntitle:" + title
                                    + "\ndescription:" + description + "\nsoundFileUrl:" + soundFileUrl_
                                    + "\nduration:" + duration_ + "\ncreate_time:" + create_time);

                            setResult(1024, intent);

                            finish();
                        }
                    });

                }else{
                    Looper.prepare();
                    Toast.makeText(SendActivity.this, "抱歉，请重试。", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        });

    }
}
