package com.atul.musicplayerlite.activities;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.helper.MusicLibraryHelper;
import com.atul.musicplayerlite.model.Music;
import com.atul.musicplayerlite.player.PlayerListener;
import com.atul.musicplayerlite.player.PlayerManager;
import com.atul.musicplayerlite.player.PlayerQueue;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;

public class PlayerDialog extends BottomSheetDialog implements SeekBar.OnSeekBarChangeListener, PlayerListener, View.OnClickListener {

    private final PlayerManager playerManager;
    private final PlayerQueue playerQueue;

    private final ImageView albumArt;
    private final ImageView repeatControl;
    private final ImageView shuffleControl;
    private final ImageView prevControl;
    private final ImageView nextControl;
    private final ImageView playPauseControl;
    private final TextView songName;
    private final TextView songAlbum;
    private final TextView currentDuration;
    private final TextView totalDuration;
    private final SeekBar songProgress;

    public PlayerDialog(@NonNull Context context, PlayerManager playerManager) {
        super(context);
        setContentView(R.layout.dialog_player);

        this.playerManager = playerManager;
        this.playerManager.attachListener(this);
        playerQueue = playerManager.getPlayerQueue();

        albumArt = findViewById(R.id.album_art);
        repeatControl = findViewById(R.id.control_repeat);
        shuffleControl = findViewById(R.id.control_shuffle);
        prevControl = findViewById(R.id.control_prev);
        nextControl = findViewById(R.id.control_next);
        playPauseControl = findViewById(R.id.control_play_pause);
        songName = findViewById(R.id.song_name);
        songAlbum = findViewById(R.id.song_album);
        currentDuration = findViewById(R.id.current_duration);
        totalDuration = findViewById(R.id.total_duration);
        songProgress = findViewById(R.id.song_progress);

        setUpUi();
        setUpListeners();
    }

    private void setUpListeners() {
        songProgress.setOnSeekBarChangeListener(this);
        repeatControl.setOnClickListener(this);
        prevControl.setOnClickListener(this);
        playPauseControl.setOnClickListener(this);
        nextControl.setOnClickListener(this);
        shuffleControl.setOnClickListener(this);
    }

    private void setUpUi() {
        Music music = playerManager.getCurrentMusic();

        songName.setText(music.displayName);
        songAlbum.setText(String.format(Locale.getDefault(), "%s • %s",
                music.artist, music.album));

        Glide.with(getContext())
                .load(music.albumArt)
                .placeholder(R.drawable.ic_album_art)
                .into(albumArt);

        int icon = playerManager.isPlaying() ? R.drawable.ic_controls_pause : R.drawable.ic_controls_play;
        playPauseControl.setImageResource(icon);

        if (playerQueue.isShuffle()) shuffleControl.setAlpha(1f);
        else shuffleControl.setAlpha(0.3f);

        int repeat = playerQueue.isRepeat() ? R.drawable.ic_controls_repeat_one : R.drawable.ic_controls_repeat;
        repeatControl.setImageResource(repeat);

        totalDuration.setText(MusicLibraryHelper.formatDuration(music.duration));
    }

    private int percentToPosition(int percent) {
        return (playerManager.getDuration() * percent) / 100;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        currentDuration.setText(MusicLibraryHelper.formatDuration(percentToPosition(progress)));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        playerManager.seekTo(percentToPosition(seekBar.getProgress()));
    }

    @Override
    public void onPrepared() {

    }

    @Override
    public void onStateChanged(int state) {
        if (state == State.PLAYING)
            playPauseControl.setImageResource(R.drawable.ic_controls_pause);
        else
            playPauseControl.setImageResource(R.drawable.ic_controls_play);
    }

    @Override
    public void onPositionChanged(int position) {
        songProgress.setProgress(position);
    }

    @Override
    public void onMusicSet(Music music) {
        setUpUi();
    }

    @Override
    public void onPlaybackCompleted() {
    }

    @Override
    public void onRelease() {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.control_repeat) setRepeat();
        else if (id == R.id.control_shuffle) setShuffle();
        else if (id == R.id.control_prev) playerManager.playPrev();
        else if(id == R.id.control_next) playerManager.playNext();
        else if (id == R.id.control_play_pause) playerManager.playPause();

        setUpUi();
    }

    private void setRepeat() {
        playerQueue.setRepeat((! playerQueue.isRepeat()));
    }

    private void setShuffle(){
        playerQueue.setShuffle((! playerQueue.isShuffle()));
    }
}
