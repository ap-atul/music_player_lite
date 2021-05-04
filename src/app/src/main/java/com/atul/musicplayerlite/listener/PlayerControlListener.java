package com.atul.musicplayerlite.listener;

import com.atul.musicplayerlite.model.Music;

public interface PlayerControlListener {
    void play(Music music);
    void pause();
    void next();
    void prev();
}
