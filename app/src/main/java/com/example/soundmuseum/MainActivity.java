package com.example.soundmuseum;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.view.GravityCompat;
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
import com.jaeger.library.StatusBarUtil;

import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarUtil.setColor(this, Color.parseColor("#222941"), 100);

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
        imageView_avata = findViewById(R.id.menu_Avata_img);
        textView_username = findViewById(R.id.menu_username_text);
        textView_username.setText("chen_1127");
        // 圆形头像, 并加白边框
        RequestOptions options = new RequestOptions()
                .transform(new CropCircleWithBorderTransformation(5, Color.WHITE))
                .error(R.drawable.pic_head);
        Glide.with(this)
                .load(R.drawable.pic_head)
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

    private void gotoMyCenter(){

    }

    private void gotoSoundMap(){

    }

    private void gotoFm(){

    }

    private void gotoConvert(){

    }

    private void gotoCut(){

    }

    private void gotoDbMeter(){

    }

    private void gotoSearch(){

    }
}
