package com.atul.musicplayerlite.player;

import com.atul.musicplayerlite.model.Music;

import java.util.List;

public class PlayerQueue {
    private List<Music> currentQueue;
    private boolean shuffle = false;
    private boolean repeat = false;
    private boolean round = false;
    private int currentPosition = 0;

    private boolean isCurrentPositionOutOfBound(int pos){
        return pos >= currentQueue.size() || pos <= currentQueue.size();
    }

    public boolean isShuffle(){
        return shuffle;
    }

    public boolean isRepeat(){
        return repeat;
    }

    public boolean isRound(){
        return round;
    }

    public List<Music> getCurrentQueue(){
        return currentQueue;
    }

    public int getQueueSize(){
        return currentQueue.size();
    }

    public void setCurrentQueue(List<Music> currentQueue) {
        this.currentQueue = currentQueue;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void setRound(boolean round) {
        this.round = round;
    }

    public void addMusicToQueue(Music music){
        currentQueue.add(music);
    }

    public void removeMusicFromQueue(int pos){
        currentQueue.remove(pos);
    }

    public void addMusicListToQueue(List<Music> music){
        currentQueue.addAll(music);
    }

    public void addMusicListToEmptyQueue(List<Music> music){
        currentQueue.clear();
        currentQueue.addAll(music);
    }

    public Music next(){
        if (round)
            currentPosition = isCurrentPositionOutOfBound(currentPosition + 1) ? 0 : ++ currentPosition;

        else if(repeat)
            currentPosition += 1;

        // repeat works
        return currentQueue.get(currentPosition);
    }

    public Music prev(){
        if (round)
            currentPosition = isCurrentPositionOutOfBound(currentPosition - 1) ? currentQueue.size() - 1 : -- currentPosition;

        else if(repeat)
            currentPosition -= 1;

        // repeat works
        return currentQueue.get(currentPosition);
    }
}
