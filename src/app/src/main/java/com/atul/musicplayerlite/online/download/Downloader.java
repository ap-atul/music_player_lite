package com.atul.musicplayerlite.online.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.atul.musicplayerlite.MPConstants;
import com.atul.musicplayerlite.helper.VersioningHelper;
import com.atul.musicplayerlite.model.Music;
import com.bumptech.glide.Glide;

import org.apache.commons.io.FileUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.AndroidArtwork;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

public class Downloader {
    private final DownloadManager downloadManager;
    private final Context context;
    private Music music;
    BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                Bundle extras = intent.getExtras();
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
                Cursor c = downloadManager.query(q);

                if (c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        String fullPath;
                        File source;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            fullPath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            source = new File(Uri.parse(fullPath).getPath());
                        } else {
                            fullPath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                            source = new File(fullPath);
                        }

                        if (setTagData(source)) {
                            Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                c.close();
            }
            context.unregisterReceiver(this);
        }
    };

    public Downloader(Context context) {
        this.context = context;
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public void downloadMusic(Music music) {
        this.music = music;
        String fileName = music.title + ".mp4";
        Log.d(MPConstants.DEBUG_TAG, music.url);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(music.url))
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                .setTitle(MPConstants.DOWNLOAD_TITLE)
                .setDescription(MPConstants.DOWNLOAD_DESC)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setVisibleInDownloadsUi(true)
                .setMimeType("*/*")
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, fileName);

        downloadManager.enqueue(request);
        context.registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private boolean setTagData(File source) {
        new Thread() {
            @Override
            public void run() {

                try {

                    File art = Glide.with(context).asFile().load(music.albumArt).submit().get();
                    AudioFile audio = AudioFileIO.read(source);
                    Tag tag = audio.getTagOrCreateDefault();
                    tag.addField(FieldKey.ALBUM, music.album);
                    tag.addField(FieldKey.ARTIST, music.artist);
                    tag.addField(FieldKey.TITLE, music.title);
                    tag.addField(AndroidArtwork.createArtworkFromFile(art));

                    audio.commit();

                    File from = audio.getFile();
                    File to = new File(source.getAbsolutePath().replace(".mp4", ".mp3"));
                    if (from.exists()) {
                        boolean b = from.renameTo(to);
                        byte[] data = FileUtils.readFileToByteArray(to);
                        String filePath = to.getAbsolutePath();
                        b = from.delete();
                        b = to.delete();

                        String collection;
                        if (VersioningHelper.isVersionQ())
                            collection = MediaStore.Audio.AudioColumns.BUCKET_DISPLAY_NAME;
                        else
                            collection = MediaStore.Audio.AudioColumns.DATA;
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Audio.AudioColumns.ARTIST, music.artist);
                        values.put(MediaStore.Audio.AudioColumns.ALBUM, music.album);
                        values.put(MediaStore.Audio.AudioColumns.TITLE, music.title);
                        values.put(MediaStore.Audio.AudioColumns.DISPLAY_NAME, music.title);
                        values.put(collection, filePath);

                        Uri uri = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                        OutputStream stream = context.getContentResolver().openOutputStream(uri);
                        stream.write(data);
                        stream.close();
                    }


                } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException | CannotWriteException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();

        return true;
    }
}
