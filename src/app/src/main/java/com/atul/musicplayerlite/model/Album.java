package com.atul.musicplayerlite.model;

import java.util.List;

public class Album {
    public String title;
    public String year;

    public Long duration;
    public List<Music> music;

    public Album(String title, String year, Long duration, List<Music> music) {
        this.title = title;
        this.year = year;
        this.duration = duration;
        this.music = music;
    }
}
