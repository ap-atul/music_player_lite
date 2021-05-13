package com.atul.musicplayerlite.listener;

import com.atul.musicplayerlite.model.Music;

import java.util.List;

public interface MusicSelectListener {
    void playQueue(List<Music> musicList);

    void addToQueue(List<Music> music);

    void setShuffleMode(boolean mode);
}