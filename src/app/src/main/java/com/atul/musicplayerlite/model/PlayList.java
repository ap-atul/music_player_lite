package com.atul.musicplayerlite.model;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.atul.musicplayerlite.MPConstants;

import java.util.List;

@Entity(tableName = MPConstants.MUSIC_TABLE)
public class PlayList {
    @NonNull
    @PrimaryKey
    public String title = "";

    public List<Music> musics;

    public PlayList(){}
}
