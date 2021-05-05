package com.atul.musicplayerlite;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.atul.musicplayerlite.adapter.MainPagerAdapter;
import com.atul.musicplayerlite.helper.MusicLibraryHelper;
import com.atul.musicplayerlite.helper.PermissionHelper;
import com.atul.musicplayerlite.listener.MusicSelectListener;
import com.atul.musicplayerlite.model.Album;
import com.atul.musicplayerlite.model.Music;
import com.atul.musicplayerlite.service.PlayerBuilder;
import com.atul.musicplayerlite.service.PlayerListener;
import com.atul.musicplayerlite.service.PlayerManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements MusicSelectListener, PlayerListener, View.OnClickListener {

    private RelativeLayout playerView;
    private ImageView albumArt;
    private TextView songName;
    private ImageView next;
    private ImageView prev;
    private ImageView play_pause;
    private LinearProgressIndicator progressIndicator;

    private PlayerBuilder playerBuilder;
    private PlayerManager playerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerBuilder = new PlayerBuilder(this, this);

        albumArt = findViewById(R.id.albumArt);
        progressIndicator = findViewById(R.id.song_progress);
        playerView = findViewById(R.id.player_view);
        songName = findViewById(R.id.song_title);
        next = findViewById(R.id.control_next);
        prev = findViewById(R.id.control_prev);
        play_pause = findViewById(R.id.control_play_pause);

        MainPagerAdapter sectionsPagerAdapter = new MainPagerAdapter(
                this, getSupportFragmentManager(), this);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        PermissionHelper.manageStoragePermission(MainActivity.this);

        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        play_pause.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playerManager != null && playerManager.isPlaying())
            playerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void playAlbum(Album album) {
        playerManager.setMusicList(album.music);
    }

    @Override
    public void playQueue(List<Music> musicList) {
        Log.d(MPConstants.DEBUG_TAG, "Trying to play the list");
        playerManager.setMusicList(musicList);
    }

    @Override
    public void onPrepared() {
        playerManager = playerBuilder.getPlayerManager();
        if(playerManager != null)
            Log.d(MPConstants.DEBUG_TAG, "Successfully got the player manager");
    }

    @Override
    public void onContentUpdate() {

    }

    @Override
    public void onStateChanged(int state) {
        Log.i(MPConstants.DEBUG_TAG, String.valueOf(state));

        if(state == State.PLAYING)
            play_pause.setImageResource(R.drawable.ic_controls_pause);
        else
            play_pause.setImageResource(R.drawable.ic_controls_play);
    }

    @Override
    public void onPositionChanged(int position) {
        progressIndicator.setProgress(position);

        try {
            ScheduledExecutorService progressService = Executors.newScheduledThreadPool(1);
            Runnable runnable = () -> {
                int percentage = playerManager.getCurrentPosition() * 100 / playerManager.getDuration();
                progressIndicator.setProgress(percentage);
                Log.d(MPConstants.DEBUG_TAG, "Progress " + String.valueOf(percentage));
            };

            progressService.scheduleAtFixedRate(
                    runnable,
                    0,
                    1000,
                    TimeUnit.MILLISECONDS
            );
        }catch (Exception e){
            e.printStackTrace();

            Log.d(MPConstants.DEBUG_TAG, "Progress  dead" );
        }
    }

    @Override
    public void onMusicSet(Music music) {
        songName.setText(music.displayName);
        playerView.setVisibility(View.VISIBLE);

        Bitmap art = MusicLibraryHelper.getThumbnail(getApplicationContext(), music.albumArt);
        Glide.with(playerView)
                .load(art)
                .centerCrop()
                .into(albumArt);
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

        if(id == R.id.control_prev)
            playerManager.playPrev();

        else if (id == R.id.control_next)
            playerManager.playNext();

        else if (id == R.id.control_play_pause)
            playerManager.playPause();
    }
}