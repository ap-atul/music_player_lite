package com.atul.musicplayer.player;

import com.atul.musicplayer.model.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PlayerQueue {
    private static PlayerQueue instance = null;
    private final Random random = new Random();
    private List<Music> currentQueue;
    private List<Integer> played;
    private boolean shuffle = false;
    private boolean repeat = false;
    private int currentPosition = 0;

    public static PlayerQueue getInstance() {
        if (instance == null) {
            instance = new PlayerQueue();
        }
        return instance;
    }

    private boolean isCurrentPositionOutOfBound(int pos) {
        return pos >= currentQueue.size() || pos < 0;
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

    public void setCurrentQueue(List<Music> currentQueue) {
        this.played = new ArrayList<>();
        this.currentQueue = currentQueue;
        this.currentPosition = 0;
        if (this.shuffle) {
            Collections.shuffle(currentQueue);
        }
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public Music getCurrentMusic() {
        return currentQueue.get(currentPosition);
    }

    public void addMusicListToQueue(List<Music> music) {
        currentQueue.addAll(music);
        this.currentPosition = (shuffle) ? random.nextInt(currentQueue.size()) : 0;
    }

    public void next() {
        played.add(currentPosition);
        if (!repeat) {
            currentPosition = (shuffle)
                    ? random.nextInt(currentQueue.size())
                    : isCurrentPositionOutOfBound(currentPosition + 1)
                    ? 0
                    : ++currentPosition;
        }
    }

    public void prev() {
        if (played.size() == 0) {
            currentPosition = 0;
        } else {
            int lastPosition = played.size() - 1;
            currentPosition = played.get(lastPosition);
            played.remove(lastPosition);
        }
    }

    public void removeMusicFromQueue(int position) {
        if (!isCurrentPositionOutOfBound(position)) {
            currentQueue.remove(position);
            if(currentPosition > position)
                currentPosition -= 1;
        }
    }

    public void swap(int one, int two) {
        if (!isCurrentPositionOutOfBound(one) && !isCurrentPositionOutOfBound(two)) {
            if(one == currentPosition) {
                currentPosition = two;
            }
            else if(two == currentPosition) {
                currentPosition = one;
            }
            Collections.swap(currentQueue, one, two);
        }
    }
}
