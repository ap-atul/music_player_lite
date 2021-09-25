package com.atul.musicplayerlite.metadata;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.atul.musicplayerlite.MPConstants;
import com.atul.musicplayerlite.MPPreferences;
import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.helper.MusicLibraryHelper;
import com.atul.musicplayerlite.helper.ThemeHelper;
import com.atul.musicplayerlite.model.Music;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.io.FileUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.AndroidArtwork;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class MetadataActivity extends AppCompatActivity {

    private Metadata metadata;
    private TextInputEditText title;
    private TextInputEditText displayName;
    private TextInputEditText artist;
    private TextInputEditText album;
    private TextInputEditText year;
    private ImageView albumArt;
    private final ActivityResultLauncher<String> getImageFromStorage
            = registerForActivityResult(new ActivityResultContracts.GetContent(),
            (ActivityResultCallback<Uri>) uri -> {
                if (uri != null)
                    updateImageView(uri);
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeHelper.getTheme(MPPreferences.getTheme(getApplicationContext())));
        AppCompatDelegate.setDefaultNightMode(MPPreferences.getThemeMode(getApplicationContext()));
        setContentView(R.layout.activity_metadata);

        Music music = getIntent().getParcelableExtra("music");
        metadata = new Metadata();

        title = findViewById(R.id.title);
        displayName = findViewById(R.id.name);
        artist = findViewById(R.id.artist);
        album = findViewById(R.id.album);
        year = findViewById(R.id.year);
        albumArt = findViewById(R.id.album_art);
        MaterialButton update = findViewById(R.id.update);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        if (music != null) {
            title.setText(music.title);
            displayName.setText(music.displayName);
            artist.setText(music.artist);
            album.setText(music.album);
            year.setText(String.valueOf(music.year));

            Glide.with(this)
                    .load(music.albumArt)
                    .placeholder(R.drawable.ic_album_art)
                    .into(albumArt);

            metadata.id = music.id;
            metadata.absolutePath = music.absolutePath;
            metadata.albumId = music.albumId;
        }

        update.setOnClickListener(v -> updateAudioTag());
        toolbar.setNavigationOnClickListener(v -> finish());
        albumArt.setOnClickListener(v -> getImageFromStorage.launch("image/*"));
    }

    private String ifNullEmpty(TextInputEditText e) {
        return e.getText() != null ? e.getText().toString() : null;
    }

    private void updateAudioTag() {
        metadata.displayName = ifNullEmpty(displayName);
        metadata.title = ifNullEmpty(title);
        metadata.artist = ifNullEmpty(artist);
        metadata.album = ifNullEmpty(album);
        metadata.year = ifNullEmpty(year);

        try {

            File originalFile = new File(metadata.absolutePath);
            File tempFile = new File(getExternalCacheDir().getPath(), originalFile.getName());
            FileUtils.copyFile(originalFile, tempFile);

            AudioFile audio = AudioFileIO.read(tempFile);
            Tag tag = audio.getTagOrCreateDefault();

            if (metadata.title != null)
                tag.setField(FieldKey.TITLE, metadata.title);

            if (metadata.album != null)
                tag.setField(FieldKey.ALBUM, metadata.album);

            if (metadata.artist != null)
                tag.setField(FieldKey.ARTIST, metadata.artist);

            if (metadata.year != null) {
                Log.d(MPConstants.DEBUG_TAG, metadata.year);
                tag.setField(FieldKey.ORIGINAL_YEAR, metadata.year);
                tag.setField(FieldKey.YEAR, metadata.year);
            }

            if (metadata.albumArt != null) {
                File albumArtFile = MusicLibraryHelper.getPathFromUri(this, metadata.albumArt);
                if (albumArtFile.exists()) {
                    if (tag.getFirstArtwork() != null) tag.deleteArtworkField();
                    tag.setField(AndroidArtwork.createArtworkFromFile(albumArtFile));

                    new Thread(() -> Glide.get(MetadataActivity.this).clearDiskCache()).start();
                }
            }

            audio.setTag(tag);
            audio.commit();

            if (FileUtils.deleteQuietly(originalFile))
                FileUtils.moveFile(audio.getFile(), originalFile);

            updateMetadata();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMetadata() {

        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, metadata.id);
        ContentResolver resolver = getApplicationContext().getContentResolver();

        String selection = MediaStore.Audio.Media._ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(metadata.id)};

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

    private void updateImageView(Uri uri) {
        metadata.albumArt = uri;

        Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.ic_album_art)
                .into(albumArt);
    }

    static class Metadata {
        public String title;
        public String artist;
        public String album;
        public String year;
        public String displayName;
        public String absolutePath;

        public long id;
        public long albumId;

        public Uri albumArt;

        @Override
        @NotNull
        public String toString() {
            return "Metadata{" +
                    "title='" + title + '\'' +
                    ", artist='" + artist + '\'' +
                    ", album='" + album + '\'' +
                    ", displayName='" + displayName + '\'' +
                    ", year='" + year + '\'' +
                    ", id=" + id +
                    ", albumArt=" + albumArt +
                    '}';
        }
    }
}