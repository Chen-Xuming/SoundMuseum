package com.example.soundmuseum.map;

public class MapModel {
    private double lng;     // 经度
    private double lat;     // 纬度
    private String title;   // 标题
    private int duration;   // 音频时长
    private String id;      // 作者
    private String time;    // 发布日期
    private String address; // 音频发布地址
    private String content; // 内容

    public MapModel(double lng, double lat, String title, int duration, String id, String time, String address, String content) {
        this.lng = lng;
        this.lat = lat;
        this.title = title;
        this.duration = duration;
        this.id = id;
        this.time = time;
        this.address = address;
        this.content = content;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public String getTitle() {
        return title;
    }

    public int getDuration() {
        return duration;
    }

    public String getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getAddress() {
        return address;
    }

    public String getContent() {
        return content;
    }
}
