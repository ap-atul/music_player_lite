package com.atul.musicplayerlite.online.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayerlite.MPConstants;
import com.atul.musicplayerlite.MPPreferences;
import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.helper.ThemeHelper;
import com.atul.musicplayerlite.listener.MusicSelectListener;
import com.atul.musicplayerlite.model.Album;
import com.atul.musicplayerlite.online.ui.adapter.NetSongsAdapter;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Locale;

public class NetSelectedAlbum extends AppCompatActivity {


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
        setContentView(R.layout.activity_net_selected_album);

        album = getIntent().getParcelableExtra("album");

//        ExtendedFloatingActionButton downloadControl = findViewById(R.id.download_button);
        albumArt = findViewById(R.id.album_art);
        albumName = findViewById(R.id.album_name);
        albumDetails = findViewById(R.id.album_details);
        toolbar = findViewById(R.id.search_toolbar);
        toolbar.setTitle(album.title);
        toolbar.setSubtitle(String.format(Locale.getDefault(), "%d songs",
                album.music.size()));

        RecyclerView recyclerView = findViewById(R.id.songs_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new NetSongsAdapter(musicSelectListener, album.music));

//        downloadControl.setOnClickListener(v -> {
//            downloadAlbum();
//        });

        setAlbumDataToUi();
        setUpOptions();
    }

//    private void downloadAlbum() {
//        Downloader downloader = new Downloader(this);
//        for(Music music: album.music){
//            downloader.downloadMusic(music);
//        }
//    }


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
        albumDetails.setText(String.format(Locale.getDefault(), "%s . %d songs",
                album.music.get(0).artist,
                album.music.size()));

        boolean state = MPPreferences.getAlbumRequest(this);
        if (state)
            Glide.with(this)
                    .load(album.music.get(0).albumArt)
                    .placeholder(R.drawable.ic_album_art)
                    .into(albumArt);
    }
}