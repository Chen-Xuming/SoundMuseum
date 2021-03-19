package com.example.soundmuseum;

import android.support.design.widget.NavigationView;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

public class DrawerActivity extends AppCompatActivity {

    private int AnimateNumber = 1 ;
    DrawerLayout mDrawer;
    NavigationView navigationView;
    RecyclerView recyclerView;
    RelativeLayout WaveContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

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
            }
        });
        recyclerView.setAdapter(adapter_menu);

        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {


            }
            @Override
            public void onDrawerOpened(View drawerView) {
                StartAnimation();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }




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
}
