package com.atul.musicplayerlite.listener;

import com.atul.musicplayerlite.model.Album;
import com.atul.musicplayerlite.model.Music;

import java.util.List;

public interface MusicSelectListener {
    void playAlbum(Album album);
    void playQueue(List<Music> musicList);
}
