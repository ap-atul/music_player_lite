package com.atul.musicplayerlite;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.atul.musicplayerlite.helper.PermissionHelper;
import com.atul.musicplayerlite.adapter.MainPagerAdapter;
import com.atul.musicplayerlite.listener.PlayerControlListener;
import com.atul.musicplayerlite.model.Music;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements PlayerControlListener {

    private TextView songName;
    private ImageView next;
    private ImageView prev;
    private ImageView play_pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songName = findViewById(R.id.song_title);
        next = findViewById(R.id.control_next);
        prev = findViewById(R.id.control_prev);
        play_pause = findViewById(R.id.control_play_pause);

        MainPagerAdapter sectionsPagerAdapter = new MainPagerAdapter(this, getSupportFragmentManager(), this);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        PermissionHelper.manageStoragePermission(MainActivity.this);
    }

    @Override
    public void play(Music music) {
        songName.setText(music.displayName);
    }

    @Override
    public void pause() {
    }

    @Override
    public void next() {

    }

    @Override
    public void prev() {

    }
}