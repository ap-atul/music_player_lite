package com.atul.musicplayerlite.player;

import com.atul.musicplayerlite.model.Music;

import java.util.List;
import java.util.Random;

public class PlayerQueue {
    private final Random random = new Random();
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

    public void setCurrentQueue(List<Music> currentQueue) {
        this.currentQueue = currentQueue;
        if (shuffle)
            this.currentPosition = random.nextInt(currentQueue.size());
    }

    public int getQueueSize() {
        return currentQueue.size();
    }

    public Music getCurrentMusic() {
        return currentQueue.get(currentPosition);
    }

    public void addMusicToQueue(Music music) {
        currentQueue.add(music);
    }

    public void removeMusicFromQueue(int pos) {
        currentQueue.remove(pos);
    }

    public void addMusicListToQueue(List<Music> music) {
        currentQueue.addAll(music);
    }

    public void addMusicListToEmptyQueue(List<Music> music) {
        currentQueue.clear();
        currentQueue.addAll(music);
    }

    public void next() {
        if (shuffle) {
            currentPosition = random.nextInt(currentQueue.size());
        } else if (!repeat)
            currentPosition = isCurrentPositionOutOfBound(currentPosition + 1) ? 0 : ++currentPosition;
    }

    public void prev() {
        if (shuffle)
            currentPosition = random.nextInt(currentQueue.size());

        else if (!repeat)
            currentPosition = isCurrentPositionOutOfBound(currentPosition - 1) ? currentQueue.size() - 1 : --currentPosition;
    }
}
