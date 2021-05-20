package com.atul.musicplayerlite.online.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.atul.jsa.controller.JsaFetch;
import com.atul.jsa.controller.JsaListener;
import com.atul.jsa.model.Album;
import com.atul.jsa.model.Music;
import com.atul.musicplayerlite.MPConstants;
import com.atul.musicplayerlite.helper.MusicLibraryHelper;
import com.atul.musicplayerlite.model.Network;

import java.util.List;

public class JsaReceiver extends BroadcastReceiver {

    public JsaReceiver(Context context, String query){
        JsaFetch jsaFetch = new JsaFetch(new JsaListener() {
            @Override
            public void setSongs(List<Music> list) {
                Intent intent = new Intent(MPConstants.NETWORK_RECEIVER_ID);
                Bundle extras = new Bundle();

                Network network = new Network();
                network.musicList = MusicLibraryHelper.jsaMusicToCurrent(list);

                extras.putParcelable(MPConstants.NETWORK_SONGS_KEY, network);
                intent.putExtras(extras);
                context.sendBroadcast(intent);
            }

            @Override
            public void setAlbums(List<Album> list) {
                Intent intent = new Intent(MPConstants.NETWORK_RECEIVER_ID);
                Bundle extras = new Bundle();

                Network network = new Network();
                network.albumList = MusicLibraryHelper.jsaAlbumToCurrent(list);

                extras.putParcelable(MPConstants.NETWORK_ALBUMS_KEY, network);
                intent.putExtras(extras);
                context.sendBroadcast(intent);
            }
        });

        jsaFetch.songs(query);
        jsaFetch.albums(query);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
