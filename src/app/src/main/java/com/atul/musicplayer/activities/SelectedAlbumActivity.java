package com.atul.musicplayer.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayer.MPConstants;
import com.atul.musicplayer.MPPreferences;
import com.atul.musicplayer.R;
import com.atul.musicplayer.adapter.SongsAdapter;
import com.atul.musicplayer.helper.ThemeHelper;
import com.atul.musicplayer.listener.MusicSelectListener;
import com.atul.musicplayer.model.Album;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Locale;

public class SelectedAlbumActivity extends AppCompatActivity {

    private final
    MusicSelectListener musicSelectListener = MPConstants.musicSelectListener;

    private ImageView albumArt;
    private TextView albumName;
    private TextView albumDetails;
    private MaterialToolbar toolbar;
    private Album album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeHelper.getTheme(MPPreferences.getTheme(getApplicationContext())));
        AppCompatDelegate.setDefaultNightMode(MPPreferences.getThemeMode(getApplicationContext()));
        setContentView(R.layout.activity_selected_album);

        album = getIntent().getParcelableExtra("album");

        ExtendedFloatingActionButton shuffleControl = findViewById(R.id.shuffle_button);
        albumArt = findViewById(R.id.album_art);
        albumName = findViewById(R.id.album_name);
        albumDetails = findViewById(R.id.album_details);
        toolbar = findViewById(R.id.search_toolbar);
        toolbar.setTitle(album.title);
        toolbar.setSubtitle(String.format(Locale.getDefault(), "%d songs",
                album.music.size()));

        RecyclerView recyclerView = findViewById(R.id.songs_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SongsAdapter(musicSelectListener, album.music));

        shuffleControl.setOnClickListener(v -> musicSelectListener.playQueue(album.music, true));

        setAlbumDataToUi();
        setUpOptions();
    }

    private void setUpOptions() {
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_add_to_queue) {
                musicSelectListener.addToQueue(album.music);
                return true;
            }

            return false;
        });
        toolbar.setNavigationOnClickListener(v ->
                finish()
        );
    }

    private void setAlbumDataToUi() {
        albumName.setText(album.title);
        albumDetails.setText(String.format(Locale.getDefault(), "%s . %s . %d songs",
                album.music.get(0).artist,
                album.year,
                album.music.size()));

        boolean state = MPPreferences.getAlbumRequest(this);
        if (state)
            Glide.with(this)
                    .load(album.music.get(0).albumArt)
                    .placeholder(R.drawable.ic_album_art)
                    .into(albumArt);
    }
}