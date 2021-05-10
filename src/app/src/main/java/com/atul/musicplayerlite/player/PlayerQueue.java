package com.atul.musicplayerlite.player;

import android.util.Log;

import com.atul.musicplayerlite.MPConstants;
import com.atul.musicplayerlite.model.Music;

import java.util.Collections;
import java.util.List;

public class PlayerQueue {
    private List<Music> currentQueue;
    private boolean shuffle = false;
    private boolean repeat = false;
    private int currentPosition = 0;

    private boolean isCurrentPositionOutOfBound(int pos) {
        return pos >= currentQueue.size() || pos <= 0;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public List<Music> getCurrentQueue() {
        return currentQueue;
    }

    public Music getCurrentMusic() {
        return currentQueue.get(currentPosition);
    }
    public void setCurrentQueue(List<Music> currentQueue) {
        this.currentQueue = currentQueue;

        if (shuffle)
            Collections.shuffle(currentQueue);
        else
            this.currentPosition = 0;
    }

    public void addMusicListToQueue(List<Music> music) {
        currentQueue.addAll(music);

        if (shuffle)
            Collections.shuffle(currentQueue);
        else
            this.currentPosition = 0;
    }

    public void next() {
        if (!repeat)
            currentPosition = isCurrentPositionOutOfBound(currentPosition + 1) ? 0 : ++currentPosition;
        else if (shuffle) {
            Collections.shuffle(currentQueue);
        }
    }

    public void prev() {
        if (!repeat)
            currentPosition = isCurrentPositionOutOfBound(currentPosition - 1) ? currentQueue.size() - 1 : --currentPosition;

        else if (shuffle)
            Collections.shuffle(currentQueue);
    }

    public void removeMusicFromQueue(int position) {
        if (!isCurrentPositionOutOfBound(position)) {
            Log.d(MPConstants.DEBUG_TAG, "song removed at pos :: " + position);
            currentQueue.remove(position);
        }
    }

    public void swap(int one, int two) {
        if (!isCurrentPositionOutOfBound(one) && !isCurrentPositionOutOfBound(two)) {
            Collections.swap(currentQueue, one, two);
            Log.d(MPConstants.DEBUG_TAG, "songs swapped at pos :: " + one + " " + two);
        }
    }
}
