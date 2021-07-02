package com.atul.musicplayerlite.model;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.atul.musicplayerlite.MPConstants;

import java.util.List;

@Entity(tableName = MPConstants.MUSIC_TABLE)
public class PlayList {

    @PrimaryKey(autoGenerate = true)
    public Integer id;

    public String title;

    public List<Music> musics;

    public PlayList(){}
}
