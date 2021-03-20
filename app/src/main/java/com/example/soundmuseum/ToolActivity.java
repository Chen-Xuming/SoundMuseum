package com.example.soundmuseum;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.soundmuseum.dbMeter.DBMeterActivity;
import com.example.soundmuseum.formatConvert.FormatConvertActivity;
import com.jaeger.library.StatusBarUtil;
import com.ringtone.maker.Activities.Activity_Editor;



public class ToolActivity extends AppCompatActivity {

    private int request_type = -1;  // 0: info; 1: convert; 2: cut

    private CardView cardView_analysis;
    private CardView cardView_convert;
    private CardView cardView_cutter;
    private CardView cardView_db;

    private String filePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);

//        StatusBarUtil.setColor(this, Color.parseColor("#1A91FF"), 100);
//
//        //////////// toolbar 初始化
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbox_toolbar);
//        toolbar.setTitle("工具箱");
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        cardView_analysis = findViewById(R.id.toolbox_file_analysis);
//        cardView_convert = findViewById(R.id.toolbox_format_convert);
//        cardView_cutter = findViewById(R.id.toolbox_audio_cutter);
//        cardView_db = findViewById(R.id.toolbox_db_measure);
//
//        cardView_analysis.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                request_type = 0;
//                pickFile();
//            }
//        });
//
//        cardView_convert.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                request_type = 1;
//                //pickFile();
//
//                Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Music");
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
////                intent.setType("audio/*");
////                intent.setData(Uri.parse(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC)
////                        .getAbsolutePath()));
//                intent.setDataAndType(uri, "audio/*");
//
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                startActivityForResult(intent,100);
//
////                Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Music");
////                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
////                intent.addCategory(Intent.CATEGORY_OPENABLE);
////                intent.setType("audio/*");
////                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
////                startActivityForResult(intent, 100);
//            }
//        });
//
//        cardView_cutter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                request_type = 2;
//                pickFile();
//            }
//        });
//
//        cardView_db.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ToolActivity.this, DBMeterActivity.class));
//            }
//        });
    }

//    void pickFile(){
//        Intent intent = new Intent(this, AudioPickActivity.class);
//        intent.putExtra(Constant.MAX_NUMBER, 1);
//        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_AUDIO);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == Constant.REQUEST_CODE_PICK_AUDIO && resultCode == RESULT_OK){
//            ArrayList<AudioFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_AUDIO);
//            if(!list.isEmpty()){
//                filePath = list.get(0).getPath();
//            }
//            switch (request_type){
//                case 0:
//                    //toShowInfo();
//                    break;
//                case 1:
//                    toConvert();
//                    break;
//                case 2:
//                    toCut();
//                    break;
//
//                    default: break;
//            }
//
//        }
//
//        if(requestCode == 100 && resultCode == RESULT_OK){
//            Uri uri = data.getData();
//            String s = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
//            s = uri.getPath().replace("/document/primary:", s);
//            Log.d("Uri_Path", s);
//            filePath = s;
//            toConvert();
//            //Toast.makeText(ToolActivity.this, "路径：" + uri.getPath(), Toast.LENGTH_LONG).show();
//        }
//    }
//
//    void toCut() {
//        if(filePath != null){
//            request_type = -1;
//            String temp = filePath;
//            String title = filePath.substring(
//                    filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
//            filePath = null;
//
//            Intent intent = new Intent(ToolActivity.this, Activity_Editor.class);
//            intent.putExtra("path", temp);
//            intent.putExtra("title", title);
//            startActivity(intent);
//        }
//    }
//
//    void toConvert(){
//        if(filePath != null){
//            request_type = -1;
//            String temp = filePath;
//            filePath = null;
//            Intent intent = new Intent(ToolActivity.this, FormatConvertActivity.class);
//            intent.putExtra("path", temp);
//            startActivity(intent);
//        }
//    }
//
////    void toShowInfo(){
////        if(filePath != null){
////            request_type = -1;
////            String temp = filePath;
////            filePath = null;
////
////            Intent intent = new Intent(ToolActivity.this, InfoActivity.class);
////            intent.putExtra("path", temp);
////
////            startActivity(intent);
////        }
////    }


}
