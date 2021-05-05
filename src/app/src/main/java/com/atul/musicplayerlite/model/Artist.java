package com.atul.musicplayerlite.model;

import java.util.List;

public class Artist {
    public String name;
    public List<Album> albums;

    public int songCount;
    public int albumCount;

    public Artist(String name, List<Album> albums, int songCount, int albumCount) {
        this.name = name;
        this.albums = albums;
        this.songCount = songCount;
        this.albumCount = albumCount;
    }
}
