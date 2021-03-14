package com.example.soundmuseum.map;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapsdkexample.util.clusterutil.clustering.ClusterItem;
import com.example.soundmuseum.R;

// 耳聆网外链 https://down.ear0.com:3321/preview?soundid=20881&type=mp3

public class MapModel implements ClusterItem {

    private LatLng mPosition;

    private String title;               // 标题
    private String s_duration;          // 音频时长(string)
    private String author_id;           // 作者id
    private String s_time;                // 发布时间
    private String address;             // 发布地点
    private String content;             // 内容

    private String audio_url;           // 音频链接

    public MapModel(double lat, double lng, String title, String duration, String author_id, String time, String address, String content, String audio_url) {
        this.title = title;
        this.s_duration = duration;
        this.author_id = author_id;
        this.s_time = time;
        this.address = address;
        this.content = content;
        this.audio_url = audio_url;

        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public BitmapDescriptor getBitmapDescriptor() {
        return BitmapDescriptorFactory
                .fromResource(R.drawable.baidumap_marker);
    }

    public String getTitle() {
        return title;
    }

    public String getS_duration() {
        return s_duration;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public String getS_time() {
        return s_time;
    }

    public String getAddress() {
        return address;
    }

    public String getContent() {
        return content;
    }

    public String getAudio_url() {
        return audio_url;
    }
}
