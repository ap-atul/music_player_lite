package com.atul.musicplayeronline.listener;

import com.atul.musicplayeronline.model.Music;

import java.util.List;

public interface MusicSelectListener {
    void playQueue(List<Music> musicList);

    void addToQueue(List<Music> music);

    void setShuffleMode(boolean mode);
}