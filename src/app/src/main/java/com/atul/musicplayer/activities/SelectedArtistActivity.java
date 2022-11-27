package com.atul.musicplayer.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayer.MPConstants;
import com.atul.musicplayer.MPPreferences;
import com.atul.musicplayer.R;
import com.atul.musicplayer.adapter.HorizontalAlbumsAdapter;
import com.atul.musicplayer.adapter.SongsAdapter;
import com.atul.musicplayer.helper.ThemeHelper;
import com.atul.musicplayer.listener.AlbumSelectListener;
import com.atul.musicplayer.listener.MusicSelectListener;
import com.atul.musicplayer.model.Album;
import com.atul.musicplayer.model.Artist;
import com.atul.musicplayer.model.Music;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectedArtistActivity extends AppCompatActivity implements AlbumSelectListener {

    private final
    MusicSelectListener musicSelectListener = MPConstants.musicSelectListener;
    private final List<Music> musicList = new ArrayList<>();
    private TextView albumTitle;
    private TextView albumSongsCount;
    private MaterialToolbar toolbar;
    private SongsAdapter songsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeHelper.getTheme(MPPreferences.getTheme(getApplicationContext())));
        AppCompatDelegate.setDefaultNightMode(MPPreferences.getThemeMode(getApplicationContext()));
        setContentView(R.layout.activity_selected_artist);

        Artist artist = getIntent().getParcelableExtra("artist");

        RecyclerView songsRecyclerView = findViewById(R.id.songs_layout);
        RecyclerView albumsRecyclerView = findViewById(R.id.albums_layout);
        albumTitle = findViewById(R.id.album_title);
        albumSongsCount = findViewById(R.id.album_song_count);
        toolbar = findViewById(R.id.search_toolbar);
        toolbar.setTitle(artist.name);
        toolbar.setSubtitle(String.format(Locale.getDefault(), "%d albums • %d songs",
                artist.albumCount, artist.songCount));

        Album defAlbum = artist.albums.get(0);
        albumTitle.setText(defAlbum.title);
        albumSongsCount.setText(String.format(Locale.getDefault(), "%d songs",
                defAlbum.music.size()));

        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        musicList.addAll(defAlbum.music);
        songsAdapter = new SongsAdapter(musicSelectListener,  musicList);
        songsRecyclerView.setAdapter(songsAdapter);

        albumsRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        List<Album> albumList = artist.albums;
        HorizontalAlbumsAdapter albumsAdapter = new HorizontalAlbumsAdapter(albumList, this);
        albumsRecyclerView.setAdapter(albumsAdapter);

        ExtendedFloatingActionButton shuffleControl = findViewById(R.id.shuffle_button);
        shuffleControl.setOnClickListener(v -> musicSelectListener.playQueue(musicList, true));

        setUpOptions();
    }

    private void setUpOptions() {
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_add_to_queue) {
                musicSelectListener.addToQueue(musicList);
                return true;
            }

            return false;
        });
        toolbar.setNavigationOnClickListener(v ->
                finish()
        );
    }

    @Override
    public void selectedAlbum(Album album) {
        musicList.clear();
        musicList.addAll(album.music);
        songsAdapter.notifyDataSetChanged();

        albumTitle.setText(album.title);
        albumSongsCount.setText(String.format(Locale.getDefault(), "%d Songs",
                album.music.size()));
    }
}