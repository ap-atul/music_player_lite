package com.atul.musicplayer.listener;

import com.atul.musicplayer.model.Music;

import java.util.List;

public interface MusicSelectListener {
    void playQueue(List<Music> musicList, boolean shuffle);

    void addToQueue(List<Music> musicList);

    void refreshMediaLibrary();
}