package com.example.soundmuseum.fm;

import java.util.Random;

public class AudioFmModel {

    int id;
    private String song_title;
    private String song_album_image;        // img_uri
    private String song_uri;                // source_uri
    private String duration;
    private int type;                       // 1: study mode; 2: sleep mode

    public AudioFmModel(int i, String title, String s_uri, String img_uri, String d, int t){
        song_title = title;
        song_album_image = img_uri;
        duration = d;
        type = t;
        song_uri = s_uri;
        id = i;
    }

    public int getId() {
        return id;
    }

    public String getSong_title() {
        return song_title;
    }

    public String getSong_album_image() {
        return song_album_image;
    }

    public String getDuration() {
        return duration;
    }

    public int getType() {
        return type;
    }

    public String getSong_uri() {
        return song_uri;
    }

    public static AudioFmModel testData[] ={
            new AudioFmModel(1, "你走以后",  "http://music.163.com/song/media/outer/url?id=1363205817",
                    "http://p3.music.126.net/n6_HOUCgBfk46D-9OCwyeQ==/109951164049623941.jpg", "3:54", 0),

            new AudioFmModel(2, "狐狸的童话", "http://music.163.com/song/media/outer/url?id=1815105886",
                    "http://p3.music.126.net/jnSajZlbE9ed8QGVlbwK0A==/109951165666607256.jpg", "2:36", 1),

            new AudioFmModel(3, "DEAR JOHN",  "http://music.163.com/song/media/outer/url?id=207497",
                    "http://p4.music.126.net/VtuBBh6zd7npHLL8yT6Liw==/46179488382243.jpg", "5:11", 1),

            new AudioFmModel(4, "归去来兮", "http://music.163.com/song/media/outer/url?id=1357999894",
                    "http://p3.music.126.net/H6dt7IgvXNWhRM_w7XbcqQ==/109951163990575387.jpg", "3:26", 0),

            new AudioFmModel(5, "如一", "http://music.163.com/song/media/outer/url?id=1498076242",
                                     "http://p4.music.126.net/3MJXi4zhAWNnwKuOA4f3EA==/109951165495881117.jpg", "4:11", 0)
    };

    public static AudioFmModel test_getData(){
        Random r = new Random();
        int i = r.nextInt(5);
        return testData[i];
    }
}
