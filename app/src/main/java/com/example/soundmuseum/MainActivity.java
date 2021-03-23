package com.example.soundmuseum;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.soundmuseum.dbMeter.DBMeterActivity;
import com.example.soundmuseum.fm.AudioFmActivity;
import com.example.soundmuseum.formatConvert.FormatConvertActivity;
import com.example.soundmuseum.map.MapActivity;
import com.example.soundmuseum.record.Record;
import com.example.soundmuseum.record.RecordFragment;
import com.example.soundmuseum.search.MySearchActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;

import com.example.soundmuseum.util.UserManager;
import com.ringtone.maker.Activities.Activity_Editor;

public class MainActivity extends AppCompatActivity {

    private int AnimateNumber = 1 ;
    DrawerLayout mDrawer;
    NavigationView navigationView;
    RecyclerView recyclerView;
    RelativeLayout WaveContainer;
    ImageView imageView_avata;
    TextView textView_username;

    ImageView imageView_menu_open;
    ImageView imageView_search;


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<String> tab_title_list = new ArrayList<>();//存放标签页标题
    private ArrayList<Fragment> fragment_list = new ArrayList<>();//存放ViewPager下的Fragment
    private RecordFragment fragment1, fragment2;
    private MyFragmentPagerAdapter adapter;//适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarUtil.setColor(this, Color.parseColor("#222941"), 100);


        /************************************************

                        Navigation Part

         */
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        recyclerView = navigationView.findViewById(R.id.nav_drawer_recycler_view);
        WaveContainer = navigationView.findViewById(R.id.WaveContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Adapter_menu adapter_menu =   new Adapter_menu(this, new Adapter_menu.ListenerOnMenuItemClick() {
            @Override
            public void Item(int Position) {
                mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                mDrawer.closeDrawer(GravityCompat.START);

                switch (Position){
                    case 0:
                        gotoMyCenter();
                        break;
                    case 2:
                        gotoSoundMap();
                        break;
                    case 3:
                        gotoFm();
                        break;
                    case 5:
                        gotoConvert();
                        break;
                    case 6:
                        gotoCut();
                        break;
                    case 7:
                        gotoDbMeter();
                        break;
                        default:break;
                }
            }
        });
        recyclerView.setAdapter(adapter_menu);
        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) { }
            @Override
            public void onDrawerOpened(View drawerView) {
                StartAnimation();
            }
            @Override
            public void onDrawerClosed(View drawerView) { }
            @Override
            public void onDrawerStateChanged(int newState) { }
        });

        UserManager.init(this);
        UserManager userManager =UserManager.getCurrentUser();

        imageView_avata = findViewById(R.id.menu_Avata_img);
        textView_username = findViewById(R.id.menu_username_text);
        textView_username.setText(userManager == null ? "未登录" : userManager.getUsername());


        // 圆形头像, 并加白边框
        RequestOptions options = new RequestOptions()
                .transform(new CropCircleWithBorderTransformation(5, Color.WHITE))
                .error(R.drawable.fox);
        Glide.with(this)
                .load(userManager == null ? R.drawable.fox : userManager.getHeadPic_url())
                .apply(options)
                .into(imageView_avata);

        imageView_menu_open = findViewById(R.id.drawer_opener);
        imageView_search = findViewById(R.id.main_search_button);


        imageView_menu_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });

        imageView_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSearch();
            }
        });


        /*************************************************
         *
         *          Main Part
         *
         */
        tabLayout = findViewById(R.id.main_tablayout);
        viewPager = findViewById(R.id.main_viewpager);
        tab_title_list.add("推荐");
        tab_title_list.add("热门");
        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(1)));
        fragment1 = new RecordFragment();
        ((RecordFragment) fragment1).setContext(this);
        ((RecordFragment) fragment1).setPageType(1);
        fragment2 = new RecordFragment();
        ((RecordFragment) fragment2).setContext(this);
        ((RecordFragment) fragment2).setPageType(2);
        fragment_list.add(fragment1);
        fragment_list.add(fragment2);
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), tab_title_list, fragment_list);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        //tabLayout.setTabsFromPagerAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //选中了tab的逻辑
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //未选中tab的逻辑
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //再次选中tab的逻辑
            }

        });

        /*******************************************
         Floating Button
         *********************************************/
        final FloatingActionMenu floating_menu = findViewById(R.id.floating_menu);
        FloatingActionButton fab_create = findViewById(R.id.fab_create);

        // 发布动态
        fab_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floating_menu.close(true);

                /*
                        已登录则跳到发布页，未登录则跳到登录页面
                 */
                if (UserManager.getCurrentUser() != null) {
                    Intent intent = new Intent(MainActivity.this, SendActivity.class);
                    startActivityForResult(intent, 100);
                    //startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, login.class);
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {

        if(requestCode == 100) {
            if (resultCode == 1024) {
                String username = data.getStringExtra("username");
                String title = data.getStringExtra("title");
                String description = data.getStringExtra("description");
                String sound_url = data.getStringExtra("soundFileUrl");
                int duration = data.getIntExtra("duration", 0);
                String create_time = data.getStringExtra("create_time");
                int id = data.getIntExtra("id", -1);
                String headpic = data.getStringExtra("headpic");

                Record record = new Record(id, username, title, description, create_time, duration,
                        headpic, sound_url, 0, 0);

                Log.d("intent_get", "\nusername:" + username + "\ntitle:" + title
                        + "\ndescription:" + description + "\nsoundFileUrl:" + sound_url
                        + "\nduration:" + duration + "\ncreate_time:" + create_time);

                fragment1.recordArray.add(0, record);
                fragment1.recordRecyclerAdapter.notifyItemInserted(0);
                fragment1.recordRecyclerAdapter.notifyItemRangeChanged(0, fragment1.recordArray.size());
                fragment1.recyclerView.scrollToPosition(0);

                //Toast.makeText(MainActivity.this, "AAAAAA", Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == 101 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            String s = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
            s = uri.getPath().replace("/document/primary:", s);

            Intent intent = new Intent(MainActivity.this, FormatConvertActivity.class);
            intent.putExtra("path", s);
            startActivity(intent);
        }

        if(requestCode == 102 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            String s = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
            s = uri.getPath().replace("/document/primary:", s);
            String title = s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf("."));

            Intent intent = new Intent(MainActivity.this, Activity_Editor.class);
            intent.putExtra("path", s);
            intent.putExtra("title", title);
            startActivity(intent);
        }
    }


    // drawer's wave
    private void  StartAnimation(){

        AnimatedVectorDrawableCompat drawable = null;
        switch (AnimateNumber){
            case 1:
                drawable =  AnimatedVectorDrawableCompat.create(this,R.drawable.animate_wave_1);
                break;
            case 2:
                drawable =  AnimatedVectorDrawableCompat.create(this,R.drawable.animate_wave_2);
                break;
            case 3:
                drawable =  AnimatedVectorDrawableCompat.create(this,R.drawable.animate_wave_3);
                break;
            case 4:
                drawable =  AnimatedVectorDrawableCompat.create(this,R.drawable.animate_wave_4);
                break;
            case 5:
                drawable =  AnimatedVectorDrawableCompat.create(this,R.drawable.animate_wave_5);
                AnimateNumber = 0;
                break;
            default:
                drawable =  AnimatedVectorDrawableCompat.create(this,R.drawable.animate_wave_1);
        }


        AnimateNumber ++ ;
        WaveContainer.setBackground(drawable);
        assert drawable != null;
        drawable.start();
    }

    /*
            个人中心
     */
    private void gotoMyCenter(){
        if (UserManager.getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, MyCenterActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
        }
    }

    /*
            音乐地图
     */
    private void gotoSoundMap(){
        startActivity(new Intent(MainActivity.this, MapActivity.class));
    }

    /*
            白噪音电台
     */
    private void gotoFm(){
        startActivity(new Intent(MainActivity.this, AudioFmActivity.class));
    }

    /*
            格式转换
     */
    private void gotoConvert(){
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Music");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        startActivityForResult(intent, 101);
    }

    /*
            音频剪切
     */
    private void gotoCut(){
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Music");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        startActivityForResult(intent, 102);
    }

    /*
            噪音检测
     */
    private void gotoDbMeter(){
        startActivity(new Intent(MainActivity.this, DBMeterActivity.class));
    }

    /*
            搜索
     */
    private void gotoSearch(){
        startActivity(new Intent(MainActivity.this, MySearchActivity.class));
    }
}
