package com.atul.musicplayerlite;

import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.atul.musicplayerlite.activities.PlayerDialog;
import com.atul.musicplayerlite.adapter.MainPagerAdapter;
import com.atul.musicplayerlite.helper.PermissionHelper;
import com.atul.musicplayerlite.helper.ThemeHelper;
import com.atul.musicplayerlite.listener.MusicSelectListener;
import com.atul.musicplayerlite.model.Music;
import com.atul.musicplayerlite.player.PlayerBuilder;
import com.atul.musicplayerlite.player.PlayerListener;
import com.atul.musicplayerlite.player.PlayerManager;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements MusicSelectListener, PlayerListener, View.OnClickListener {


    private RelativeLayout playerView;
    private ImageView albumArt;
    private TextView songName;
    private TextView songDetails;
    private ImageButton play_pause;
    private LinearProgressIndicator progressIndicator;

    private PlayerBuilder playerBuilder;
    private PlayerManager playerManager;
    private TabLayout tabs;
    private PlayerDialog playerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeHelper.getTheme(MPPreferences.getTheme(getApplicationContext())));
        setContentView(R.layout.activity_main);

        if (PermissionHelper.manageStoragePermission(MainActivity.this))
            setUpUiElements();

        if (savedInstanceState != null && savedInstanceState.getString(MPConstants.SAVE_INSTANCE_KEY_PLAYER) != null)
            setPlayerView();

        playerBuilder = new PlayerBuilder(this, this);
        MPConstants.musicSelectListener = this;

        MaterialCardView playerLayout = findViewById(R.id.player_layout);
        albumArt = findViewById(R.id.albumArt);
        progressIndicator = findViewById(R.id.song_progress);
        playerView = findViewById(R.id.player_view);
        songName = findViewById(R.id.song_title);
        songDetails = findViewById(R.id.song_details);
        ImageButton next = findViewById(R.id.control_next);
        ImageButton prev = findViewById(R.id.control_prev);
        play_pause = findViewById(R.id.control_play_pause);

        songName.setOnClickListener(this);
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        play_pause.setOnClickListener(this);
        playerLayout.setOnClickListener(this);
    }

    private void setPlayerView() {
        if (playerManager != null && playerManager.isPlaying()) {
            playerView.setVisibility(View.VISIBLE);
            onMusicSet(playerManager.getCurrentMusic());
        }
    }

    public void setUpUiElements() {
        MainPagerAdapter sectionsPagerAdapter = new MainPagerAdapter(
                this, getSupportFragmentManager(), this);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        setUpTabIcons();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MPConstants.SAVE_INSTANCE_KEY_PLAYER, MPConstants.SAVE_INSTANCE_VAL_PLAYER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setUpUiElements();
        }
    }

    private void setUpTabIcons() {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            tabs.getTabAt(i).setIcon(MPConstants.TAB_ICONS[i]);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerManager.detachService();

        if (playerDialog != null)
            playerDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPlayerView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setPlayerView();
    }

    @Override
    public void playQueue(List<Music> musicList) {
        playerManager.setMusicList(musicList);
    }

    @Override
    public void setShuffleMode(boolean mode) {
        playerManager.getPlayerQueue().setShuffle(mode);
    }

    @Override
    public void onPrepared() {
        playerManager = playerBuilder.getPlayerManager();
        setPlayerView();
    }

    @Override
    public void onStateChanged(int state) {
        if (state == State.PLAYING)
            play_pause.setImageResource(R.drawable.ic_controls_pause);
        else
            play_pause.setImageResource(R.drawable.ic_controls_play);
    }

    @Override
    public void onPositionChanged(int position) {
        progressIndicator.setProgress(position);
    }

    @Override
    public void onMusicSet(Music music) {
        songName.setText(music.title);
        songDetails.setText(
                String.format(Locale.getDefault(), "%s • %s",  music.artist,  music.album));
        playerView.setVisibility(View.VISIBLE);

        Glide.with(getApplicationContext())
                .load(music.albumArt)
                .centerCrop()
                .into(albumArt);

        if (playerManager != null && playerManager.isPlaying())
            play_pause.setImageResource(R.drawable.ic_controls_pause);
        else
            play_pause.setImageResource(R.drawable.ic_controls_play);
    }

    @Override
    public void onPlaybackCompleted() {
        playerView.setVisibility(View.GONE);
    }

    @Override
    public void onRelease() {
        playerView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.control_prev)
            playerManager.playPrev();

        else if (id == R.id.control_next)
            playerManager.playNext();

        else if (id == R.id.control_play_pause)
            playerManager.playPause();

        else if (id == R.id.player_layout)
            setUpPlayerDialog();
    }

    private void setUpPlayerDialog() {
        playerDialog = new PlayerDialog(this, playerManager);
        playerDialog.show();
    }
}