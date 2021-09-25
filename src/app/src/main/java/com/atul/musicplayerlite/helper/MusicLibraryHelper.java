package com.atul.musicplayerlite.helper;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.FileUtils;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.model.Music;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
            collection = MediaStore.Audio.Media.BUCKET_DISPLAY_NAME;
        else
            collection = MediaStore.Audio.Media.DATA;

        String[] projection = new String[]{
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,  // error from android side, it works < 29
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM,
                collection,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATE_MODIFIED,
                MediaStore.Audio.Media.DATA
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
        String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        @SuppressLint("Recycle")
        Cursor musicCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, sortOrder);

        int artistInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
        int yearInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR);
        int trackInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK);
        int titleInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
        int displayNameInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
        int durationInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
        int albumIdInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
        int albumInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
        int relativePathInd = musicCursor.getColumnIndexOrThrow(collection);
        int idInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
        int dateModifiedInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED);
        int contentUriInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

        while (musicCursor.moveToNext()) {
            String artist = musicCursor.getString(artistInd);
            String title = musicCursor.getString(titleInd);
            String displayName = musicCursor.getString(displayNameInd);
            String album = musicCursor.getString(albumInd);
            String relativePath = musicCursor.getString(relativePathInd);
            String absolutePath = musicCursor.getString(contentUriInd);

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
                    artist, title, displayName, album, relativePath, absolutePath,
                    year, track, startFrom, dateAdded,
                    id, duration, albumId,
                    ContentUris.withAppendedId(Uri.parse(context.getResources().getString(R.string.album_art_dir)), albumId)
            ));
        }

        if (!musicCursor.isClosed())
            musicCursor.close();

        return musicList;
    }

    public static Bitmap getThumbnail(Context context, String uri) {
        try {
            ParcelFileDescriptor fileDescriptor = context.getContentResolver().openFileDescriptor(Uri.parse(uri), "r");
            if (uri == null)
                return null;

            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor());
            fileDescriptor.close();

            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }

    public static File getPathFromUri(Context context, Uri uri) throws IOException {
        String fileName = getFileName(context, uri);
        File file = new File(context.getExternalCacheDir(), fileName);

        if (file.createNewFile()) {
            OutputStream outputStream = new FileOutputStream(file);
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileUtils.copy(inputStream, outputStream); //Simply reads input to output stream
            outputStream.flush();
        }

        return file;
    }

    public static String getFileName(Context context, Uri uri) {
        String fileName = getFileNameFromCursor(context, uri);
        if (fileName == null) {
            String fileExtension = getFileExtension(context, uri);
            fileName = "temp_file" + (fileExtension != null ? "." + fileExtension : "");
        } else if (!fileName.contains(".")) {
            String fileExtension = getFileExtension(context, uri);
            fileName = fileName + "." + fileExtension;
        }
        return fileName;
    }

    public static String getFileExtension(Context context, Uri uri) {
        String fileType = context.getContentResolver().getType(uri);
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType);
    }

    public static String getFileNameFromCursor(Context context, Uri uri) {
        Cursor fileCursor = context.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null);
        String fileName = null;
        if (fileCursor != null && fileCursor.moveToFirst()) {
            int cIndex = fileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (cIndex != -1) {
                fileName = fileCursor.getString(cIndex);
            }
        }
        return fileName;
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

    public static String formatDate(long dateAdded) {
        SimpleDateFormat fromFormat = new SimpleDateFormat("s", Locale.getDefault());
        SimpleDateFormat toFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());

        try {
            Date date = fromFormat.parse(String.valueOf(dateAdded));
            assert date != null;
            return toFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int[] getBitSampleRates(Music music) {
        try {
            MediaExtractor extractor = new MediaExtractor();
            extractor.setDataSource(music.absolutePath);

            MediaFormat format = extractor.getTrackFormat(0);
            int sample = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            int bitrate = format.getInteger(MediaFormat.KEY_BIT_RATE);
            int rate = Math.abs(bitrate / 1000);

            return new int[]{
                    sample,
                    normalizeRate(rate)
            };

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new int[]{0, 0};
    }

    private static int normalizeRate(int rate) {
        return (rate > 320) ? 320 : 120;
    }
}
