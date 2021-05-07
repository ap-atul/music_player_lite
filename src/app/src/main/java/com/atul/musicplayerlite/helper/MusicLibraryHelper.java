package com.atul.musicplayerlite.helper;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.model.Music;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MusicLibraryHelper {

    public static List<Music> fetchMusicLibrary(Context context) {
        String collection;
        List<Music> musicList = new ArrayList<>();

        if (VersioningHelper.isVersionQ())
            collection = MediaStore.Audio.AudioColumns.BUCKET_DISPLAY_NAME;
        else
            collection = MediaStore.Audio.AudioColumns.DATA;

        String[] projection = new String[]{
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
        @SuppressLint("Recycle")
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

        while (musicCursor.moveToNext()) {
            String artist = musicCursor.getString(artistInd);
            String title = musicCursor.getString(titleInd);
            String displayName = musicCursor.getString(displayMameInd);
            String album = musicCursor.getString(albumInd);
            String relativePath = musicCursor.getString(relativePathInd);

            if (VersioningHelper.isVersionQ())
                relativePath += "/";
            else if (relativePath != null) {
                File check = new File(relativePath).getParentFile();
                if (check != null) {
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
                    artist, title, displayName, album, relativePath,
                    year, track, startFrom, dateAdded,
                    id, duration, albumId,
                    ContentUris.withAppendedId(Uri.parse(context.getResources().getString(R.string.album_art_dir)), albumId)
            ));
        }

        return musicList;
    }

    public static Bitmap getThumbnail(Context context, Uri uri) {
        try {
            ParcelFileDescriptor fileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor());
            fileDescriptor.close();

            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }

    public static String formatDuration(long duration) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        String second = String.valueOf(seconds);

        if (second.length() == 1)
            second = "0" + second;
        else
            second = second.substring(0, 2);

        return String.format(Locale.getDefault(), "%02dm %ss",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                second
        );
    }

    public static String formatDurationTimeStyle(long duration) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        String second = String.valueOf(seconds);

        if (second.length() == 1)
            second = "0" + second;
        else
            second = second.substring(0, 2);

        return String.format(Locale.getDefault(), "%02d:%s",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                second
        );
    }

    public static String formatDate(int dateAdded) {
        SimpleDateFormat fromFormat = new SimpleDateFormat("s", Locale.getDefault());
        SimpleDateFormat toFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());

        try {
            Date date = fromFormat.parse(String.valueOf(dateAdded));
            return toFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
