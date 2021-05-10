package com.atul.musicplayerlite;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.atul.musicplayerlite.activities.PlayerDialog;
import com.atul.musicplayerlite.activities.QueueDialog;
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

import java.util.ArrayList;
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
    private PlayerDialog playerDialog;
    private QueueDialog queueDialog;

    private PlayerBuilder playerBuilder;
    private PlayerManager playerManager;
    private boolean albumState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeHelper.getTheme(MPPreferences.getTheme(getApplicationContext())));
        setContentView(R.layout.activity_main);

        if (PermissionHelper.manageStoragePermission(MainActivity.this))
            setUpUiElements();

        albumState = MPPreferences.getAlbumRequest(this);
        playerBuilder = new PlayerBuilder(this, this);
        MPConstants.musicSelectListener = this;

        MaterialCardView playerLayout = findViewById(R.id.player_layout);
        albumArt = findViewById(R.id.albumArt);
        progressIndicator = findViewById(R.id.song_progress);
        playerView = findViewById(R.id.player_view);
        songName = findViewById(R.id.song_title);
        songDetails = findViewById(R.id.song_details);
        play_pause = findViewById(R.id.control_play_pause);
        ImageButton queue = findViewById(R.id.control_queue);

        songName.setOnClickListener(this);
        play_pause.setOnClickListener(this);
        playerLayout.setOnClickListener(this);
        queue.setOnClickListener(this);
    }

    private void setPlayerView() {
        if (playerManager != null && playerManager.isPlaying()) {
            playerView.setVisibility(View.VISIBLE);
            onMusicSet(playerManager.getCurrentMusic());
        }
    }

    public void setUpUiElements() {
        MainPagerAdapter sectionsPagerAdapter = new MainPagerAdapter(
                getSupportFragmentManager(), this);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(4);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        for (int i = 0; i < tabs.getTabCount(); i++) {
            tabs.getTabAt(i).setIcon(MPConstants.TAB_ICONS[i]);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ThemeHelper.applySettings(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // TODO:: Fix this, the ideal behaviour is that when the app closes
        //      the service is stopped from the foreground, but still lives
        //      and on coming back to app, it works.

        // TODO:: Current error is that the service unbinds, when we start the app
        //       from notification or by launcher it works ok, but after selecting the
        //      theme(changing the theme) or clicking the navigation button on toolbar
        //      the service dies too, sometimes it dies also by clicking on the
        //      notification.
//        playerBuilder.unBindService();

        if(playerDialog != null)
            playerDialog.dismiss();

        if(queueDialog != null)
            queueDialog.dismiss();
    }

    @Override
    public void playQueue(List<Music> musicList) {
        playerManager.setMusicList(musicList);
        setPlayerView();
    }

    @Override
    public void playCurrent(Music music) {
        List<Music> list = new ArrayList<>();
        list.add(music);
        playerManager.setMusicList(list);

        setPlayerView();
    }

    @Override
    public void addToQueue(List<Music> music) {
        if (playerManager != null && playerManager.isPlaying())
            playerManager.addMusicQueue(music);
        else if (playerManager != null)
            playerManager.setMusicList(music);

        setPlayerView();
    }

    @Override
    public void setShuffleMode() {
        playerManager.getPlayerQueue().setShuffle(true);
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
                String.format(Locale.getDefault(), "%s • %s",
                        music.artist,  music.album));
        playerView.setVisibility(View.VISIBLE);

        if(albumState)
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
    public void onPlaybackCompleted() {  }

    @Override
    public void onRelease() {
        playerView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.control_play_pause)
            playerManager.playPause();

        else if (id == R.id.control_queue)
            setUpQueueDialog();

        else if (id == R.id.player_layout)
            setUpPlayerDialog();
    }

    private void setUpPlayerDialog() {
        playerDialog = new PlayerDialog(this, playerManager);
        playerDialog.show();
    }

    private void setUpQueueDialog() {
        Log.d(MPConstants.DEBUG_TAG, "queue clicked");
        queueDialog = new QueueDialog(this, playerManager.getPlayerQueue());
        queueDialog.show();
    }
}