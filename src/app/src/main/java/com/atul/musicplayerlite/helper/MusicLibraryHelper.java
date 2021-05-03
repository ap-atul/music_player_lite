package com.atul.musicplayerlite.helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import androidx.annotation.RequiresApi;

import com.atul.musicplayerlite.model.Music;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicLibraryHelper {

    public static List<Music> fetchMusicLibrary (Context context) {
        String collection;
        List<Music> musicList = new ArrayList<>();

        if (VersioningHelper.isVersionQ())
            collection = MediaStore.Audio.AudioColumns.BUCKET_DISPLAY_NAME;
        else
            collection = MediaStore.Audio.AudioColumns.DATA;

        String[] projection = new String[] {
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.YEAR,
                MediaStore.Audio.AudioColumns.TRACK,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.ALBUM,
                collection,
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.DATE_MODIFIED
        };

        String selection = MediaStore.Audio.AudioColumns.IS_MUSIC + " = 1";
        String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        Cursor musicCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, sortOrder);

        int artistInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST);
        int yearInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.YEAR);
        int trackInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK);
        int titleInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE);
        int displayMameInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME);
        int durationInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION);
        int albumIdInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID);
        int albumInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM);
        int relativePathInd = musicCursor.getColumnIndexOrThrow(collection);
        int idInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID);
        int dateModifiedInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_MODIFIED);

        while (musicCursor.moveToNext()){
            String artist = musicCursor.getString(artistInd);
            String title = musicCursor.getString(titleInd);
            String displayName = musicCursor.getString(displayMameInd);
            String album = musicCursor.getString(albumInd);
            String relativePath = musicCursor.getString(relativePathInd);

            if(VersioningHelper.isVersionQ())
                relativePath += "/";
            else if (relativePath != null) {
                File check = new File(relativePath).getParentFile();
                if(check != null) {
                    relativePath = check.getName() + "/";
                }
            } else {
                relativePath = "/";
            }

            int year = musicCursor.getInt(yearInd);
            int track = musicCursor.getInt(trackInd);
            int startFrom = 0;
            int dateAdded = musicCursor.getInt(dateModifiedInd);

            long id = musicCursor.getLong(idInd);
            long duration = musicCursor.getLong(durationInd);
            long albumId = musicCursor.getLong(albumIdInd);

            musicList.add(new Music(
                    artist, title, displayName, album, relativePath, "0",
                    year, track, startFrom, dateAdded,
                    id, duration, albumId
            ));
        }

        return musicList;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Bitmap getThumbnail (Context context, String uri) throws IOException {
        return context.getContentResolver().loadThumbnail(Uri.parse(uri), new Size(640, 680), null);
    }
}
