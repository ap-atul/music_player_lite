package com.atul.musicplayerlite.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.MediaController;

import com.atul.musicplayerlite.model.Music;

import java.util.List;

public class PlayerManager implements MediaController.MediaPlayerControl {

    private final List<Music> musicList;
    private final Context context;
    private PlayerService playerService;
    private PlayerController controller;
    private boolean musicBound = false;
    private boolean playbackPaused = false;

    public PlayerManager(Context context, List<Music> musicList) {
        this.musicList = musicList;
        this.context = context;

        Intent playIntent = new Intent(this.context, PlayerService.class);
        ServiceConnection musicConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PlayerService.MusicBinder binder = (PlayerService.MusicBinder) service;
                playerService = binder.getService();
                playerService.setList(PlayerManager.this.musicList);
                playerService.setSong(0);
                playerService.go();
                musicBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicBound = false;
            }
        };
        this.context.bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        this.context.startService(playIntent);

        setController();
    }

    private void setController() {
        controller = new PlayerController(context);
        controller.setPrevNextListeners(v -> playNext(), v -> playPrev());
        controller.setMediaPlayer(this);
        controller.setEnabled(true);
    }

    @Override
    public int getCurrentPosition() {
        if (playerService != null && musicBound && playerService.isPng())
            return playerService.getPosn();
        else return 0;
    }

    @Override
    public int getDuration() {
        if (playerService != null && musicBound && playerService.isPng())
            return playerService.getDur();
        else return 0;
    }

    @Override
    public boolean isPlaying() {
        if (playerService != null && musicBound)
            return playerService.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void pause() {
        playbackPaused = true;
        playerService.pausePlayer();
    }

    @Override
    public void seekTo(int pos) {
        playerService.seek(pos);
    }

    @Override
    public void start() {
        playerService.go();
    }

    private void playNext() {
        playerService.playNext();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    private void playPrev() {
        playerService.playPrev();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }
}
