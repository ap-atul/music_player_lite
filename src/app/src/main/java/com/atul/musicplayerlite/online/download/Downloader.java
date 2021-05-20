package com.atul.musicplayerlite.online.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.atul.musicplayerlite.MPConstants;
import com.atul.musicplayerlite.helper.VersioningHelper;
import com.atul.musicplayerlite.model.Music;

public class Downloader {
//    private final DownloadManager downloadManager;
    private final Context context;
    private final Music music;
    private long downloadFileId;

    public Downloader(Context context, Music music){
        this.context = context;
        this.music = music;
//        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public void downloadMusic(){
        String fileName = music.title + ".mp3";
        Log.d(MPConstants.DEBUG_TAG, Uri.parse(music.url).toString());
        Log.d(MPConstants.DEBUG_TAG, music.url);
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(music.url))
//                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
//                .setTitle(music.title)
//                .setDescription(music.title)
//                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
//                .setAllowedOverMetered(true)
//                .setAllowedOverRoaming(true)
//                .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, fileName);

//        downloadFileId = downloadManager.enqueue(request);
//        Log.d(MPConstants.DEBUG_TAG, String.valueOf(downloadFileId));
//        context.registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            if(downloadManager != null){
//                Uri uri = downloadManager.getUriForDownloadedFile(downloadFileId);
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Audio.AudioColumns.ARTIST, music.artist);
//                values.put(MediaStore.Audio.AudioColumns.YEAR, 0);
//                values.put(MediaStore.Audio.AudioColumns.YEAR, 0);
//                values.put(MediaStore.Audio.AudioColumns.TITLE, music.title);
//                values.put(MediaStore.Audio.AudioColumns.DISPLAY_NAME, music.title);
//                values.put(MediaStore.Audio.AudioColumns.DURATION, 180000);
//
//                String collection;
//                if (VersioningHelper.isVersionQ())
//                    collection = MediaStore.Audio.AudioColumns.BUCKET_DISPLAY_NAME;
//                else
//                    collection = MediaStore.Audio.AudioColumns.DATA;
//
//                values.put(collection, uri.toString());
//                values.put(MediaStore.Audio.AudioColumns.ALBUM, music.album);
//                values.put(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI.toString(), uri.toString());
//                values.put(MediaStore.Audio.AudioColumns.DATE_MODIFIED, music.dateAdded);
//                context.getContentResolver().update(uri, values, new Bundle());
//            }
        }
    };
}
