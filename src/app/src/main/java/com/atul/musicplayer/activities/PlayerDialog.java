package com.atul.musicplayer.activities;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.atul.musicplayer.R;
import com.atul.musicplayer.helper.MusicLibraryHelper;
import com.atul.musicplayer.listener.PlayerDialogListener;
import com.atul.musicplayer.model.Music;
import com.atul.musicplayer.player.PlayerListener;
import com.atul.musicplayer.player.PlayerManager;
import com.atul.musicplayer.player.PlayerQueue;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;

public class PlayerDialog extends BottomSheetDialog implements SeekBar.OnSeekBarChangeListener, PlayerListener, View.OnClickListener {

    private final PlayerManager playerManager;
    private final PlayerDialogListener playerDialogListener;
    private final PlayerQueue playerQueue;

    private final ImageView albumArt;
    private final ImageButton repeatControl;
    private final ImageButton shuffleControl;
    private final ImageButton prevControl;
    private final ImageButton nextControl;
    private final ImageButton playPauseControl;
    private final ImageButton musicQueue;
    private final ImageButton sleepTimer;
    private final TextView songName;
    private final TextView songAlbum;
    private final TextView currentDuration;
    private final TextView totalDuration;
    private final TextView songDetails;
    private final SeekBar songProgress;

    private Boolean dragging = false;

    public PlayerDialog(@NonNull Context context, PlayerManager playerManager, PlayerDialogListener listener) {
        super(context);
        setContentView(R.layout.dialog_player);

        this.playerDialogListener = listener;
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
        songDetails = findViewById(R.id.audio_details);
        musicQueue = findViewById(R.id.music_queue);
        sleepTimer = findViewById(R.id.sleep_timer);

        setUpUi();
        setUpListeners();

        this.setOnCancelListener(dialogInterface -> detachListener());
        this.setOnDismissListener(dialogInterface -> detachListener());
    }

    private void detachListener() {
        playerManager.detachListener(this);
    }

    private void setUpAudioDetails() {
        int[] rates = MusicLibraryHelper.getBitSampleRates(playerManager.getCurrentMusic());
        if (rates[0] > 0 && rates[1] > 0) {
            songDetails.setText(
                    String.format(Locale.getDefault(),
                            "%s kHz • %s kbps", rates[0], rates[1]));
        }
    }

    private void setUpListeners() {
        songProgress.setOnSeekBarChangeListener(this);
        repeatControl.setOnClickListener(this);
        prevControl.setOnClickListener(this);
        playPauseControl.setOnClickListener(this);
        nextControl.setOnClickListener(this);
        shuffleControl.setOnClickListener(this);
        musicQueue.setOnClickListener(this);
        sleepTimer.setOnClickListener(this);

        currentDuration.setText(getContext().getString(R.string.zero_time));
    }

    private void setUpUi() {
        Music music = playerManager.getCurrentMusic();

        songName.setText(music.title);
        songAlbum.setText(String.format(Locale.getDefault(), "%s • %s",
                music.artist, music.album));

        Glide.with(getContext().getApplicationContext())
                .load(music.albumArt)
                .placeholder(R.drawable.ic_album_art)
                .into(albumArt);

        int icon = playerManager.isPlaying() ? R.drawable.ic_controls_pause : R.drawable.ic_controls_play;
        playPauseControl.setImageResource(icon);

        if (playerQueue.isShuffle()) shuffleControl.setAlpha(1f);
        else shuffleControl.setAlpha(0.3f);

        int repeat = playerQueue.isRepeat() ? R.drawable.ic_controls_repeat_one : R.drawable.ic_controls_repeat;
        repeatControl.setImageResource(repeat);

        totalDuration.setText(MusicLibraryHelper.formatDurationTimeStyle(playerManager.getDuration()));

        if (playerManager.getCurrentPosition() < 100)
            currentDuration.setText(MusicLibraryHelper
                    .formatDurationTimeStyle(percentToPosition(playerManager.getCurrentPosition())));

        setUpAudioDetails();
    }

    private int percentToPosition(int percent) {
        return (playerManager.getDuration() * percent) / 100;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        currentDuration.setText(MusicLibraryHelper.formatDurationTimeStyle(percentToPosition(progress)));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        dragging = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        playerManager.seekTo(percentToPosition(seekBar.getProgress()));
        dragging = false;
    }

    @Override
    public void onPrepared() {
        // Unused
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
        if (Boolean.FALSE.equals(dragging))
            songProgress.setProgress(position);
    }

    @Override
    public void onMusicSet(Music music) {
        setUpUi();
    }

    @Override
    public void onPlaybackCompleted() {
        // Unused
    }

    @Override
    public void onRelease() {
        // Unused
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.control_repeat) setRepeat();
        else if (id == R.id.control_shuffle) setShuffle();
        else if (id == R.id.control_prev) playerManager.playPrev();
        else if (id == R.id.control_next) playerManager.playNext();
        else if (id == R.id.control_play_pause) playerManager.playPause();
        else if (id == R.id.music_queue) this.playerDialogListener.queueOptionSelect();
        else if (id == R.id.sleep_timer) this.playerDialogListener.sleepTimerOptionSelect();

    }

    private void setRepeat() {
        boolean repeatState = !playerQueue.isRepeat();
        playerQueue.setRepeat(repeatState);
        int repeat = repeatState ? R.drawable.ic_controls_repeat_one : R.drawable.ic_controls_repeat;
        repeatControl.setImageResource(repeat);
    }

    private void setShuffle() {
        boolean shuffleState = !playerQueue.isShuffle();
        playerQueue.setShuffle(shuffleState);
        if (shuffleState) shuffleControl.setAlpha(1f);
        else shuffleControl.setAlpha(0.3f);
    }
}
