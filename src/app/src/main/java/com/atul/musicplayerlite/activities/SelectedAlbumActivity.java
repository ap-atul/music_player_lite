package com.atul.musicplayerlite.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayerlite.MPConstants;
import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.adapter.SongsAdapter;
import com.atul.musicplayerlite.helper.MusicLibraryHelper;
import com.atul.musicplayerlite.listener.MusicSelectListener;
import com.atul.musicplayerlite.model.Album;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class SelectedAlbumActivity extends AppCompatActivity {

    private MusicSelectListener musicSelectListener = MPConstants.musicSelectListener;

    private ImageView albumArt;
    private TextView albumName;
    private TextView albumDetails;
    private ExtendedFloatingActionButton shuffleControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_album);

        Album album = getIntent().getParcelableExtra("album");

        shuffleControl = findViewById(R.id.shuffle_button);
        albumArt = findViewById(R.id.album_art);
        albumName = findViewById(R.id.album_name);
        albumDetails = findViewById(R.id.album_details);

        RecyclerView recyclerView = findViewById(R.id.songs_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SongsAdapter(musicSelectListener, album.music));

        setAlbumDataToUi(album);
    }

    private void setAlbumDataToUi(Album album) {
        albumName.setText(album.title);
        albumDetails.setText(String.format(Locale.getDefault(), "%s . %s . %d songs",
                album.music.get(0).artist,
                album.year,
                album.music.size()));

        Bitmap art = MusicLibraryHelper.getThumbnail(this,
                album.music.get(0).albumArt);
        if (art == null)
            albumArt.setImageResource(R.drawable.ic_album_art);
        albumArt.setImageBitmap(art);
    }
}