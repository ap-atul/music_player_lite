package com.atul.musicplayerlite.metadata;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.atul.musicplayerlite.MPConstants;
import com.atul.musicplayerlite.MPPreferences;
import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.helper.ThemeHelper;
import com.atul.musicplayerlite.model.Music;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

public class MetadataActivity extends AppCompatActivity {

    private Metadata metadata;
    private Music music;

    private TextInputEditText title;
    private TextInputEditText displayName;
    private TextInputEditText artist;
    private TextInputEditText album;
    private TextInputEditText year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeHelper.getTheme(MPPreferences.getTheme(getApplicationContext())));
        AppCompatDelegate.setDefaultNightMode(MPPreferences.getThemeMode(getApplicationContext()));
        setContentView(R.layout.activity_metadata);

        music = getIntent().getParcelableExtra("music");

        title = findViewById(R.id.title);
        displayName = findViewById(R.id.name);
        artist = findViewById(R.id.artist);
        album = findViewById(R.id.album);
        year = findViewById(R.id.year);
        MaterialButton update = findViewById(R.id.update);

        if (music != null) {
            title.setText(music.title);
            displayName.setText(music.displayName);
            artist.setText(music.artist);
            album.setText(music.album);
            year.setText(music.year);

            metadata.id = music.id;
        }

        update.setOnClickListener(v -> updateMetadata());
    }

    private void initAllData() {
        metadata = new Metadata();

        metadata.displayName = ifNullEmpty(displayName);
        metadata.title = ifNullEmpty(title);
        metadata.artist = ifNullEmpty(artist);
        metadata.album = ifNullEmpty(album);
        metadata.year = ifNullEmpty(year);
    }

    private String ifNullEmpty(TextInputEditText e) {
        return e.getText() != null ? e.getText().toString() : null;
    }

    private void updateAudioTag() {
        try {

            AudioFile audio = AudioFileIO.read(new File(music.absolutePath));

            Tag tag = audio.getTagOrCreateDefault();

            if (metadata.title != null)
                tag.addField(FieldKey.TITLE, metadata.title);

            if (metadata.album != null)
                tag.addField(FieldKey.ALBUM, metadata.album);

            if (metadata.artist != null)
                tag.addField(FieldKey.ARTIST, metadata.artist);

            if (metadata.year != null)
                tag.addField(FieldKey.YEAR, metadata.year);

            audio.setTag(tag);
            audio.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMetadata() {
        initAllData();
        updateAudioTag();

        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, metadata.id);
        ContentResolver resolver = getApplicationContext().getContentResolver();

        String selection = MediaStore.Audio.Media._ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(music.id)};

        ContentValues updatedSongDetails = new ContentValues();

        if (metadata.displayName != null)
            updatedSongDetails.put(MediaStore.Audio.Media.DISPLAY_NAME, metadata.displayName);

        if (metadata.title != null)
            updatedSongDetails.put(MediaStore.Audio.Media.TITLE, metadata.title);

        if (metadata.artist != null)
            updatedSongDetails.put(MediaStore.Audio.Media.ARTIST, metadata.artist);

        if (metadata.album != null)
            updatedSongDetails.put(MediaStore.Audio.Media.ALBUM, metadata.album);

        if (metadata.year != null)
            updatedSongDetails.put(MediaStore.Audio.Media.YEAR, metadata.year);

        int updates = resolver.update(
                uri,
                updatedSongDetails,
                selection,
                selectionArgs
        );

        Log.d(MPConstants.DEBUG_TAG, "DONE:: " + updates);
        if (updates > 0)
            Toast.makeText(this, "Song details are updated", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Song updates failed", Toast.LENGTH_SHORT).show();

    }

    static class Metadata {
        public String title;
        public String artist;
        public String album;
        public String displayName;
        public String year;

        public long id;
    }
}