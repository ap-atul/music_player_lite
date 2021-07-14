package com.atul.musicplayeronline.online.receiver;

import androidx.lifecycle.MutableLiveData;

import com.atul.jsa.controller.JsaFetch;
import com.atul.jsa.controller.JsaListener;
import com.atul.jsa.model.Album;
import com.atul.jsa.model.Music;
import com.atul.musicplayeronline.helper.MusicLibraryHelper;

import java.util.List;

public class JsaReceiver {
    private final MutableLiveData<List<com.atul.musicplayeronline.model.Music>> musicList;
    private final MutableLiveData<List<com.atul.musicplayeronline.model.Album>> albumList;

    public JsaReceiver () {
        musicList = new MutableLiveData<>();
        albumList = new MutableLiveData<>();
    }

    public void search(String query){
        JsaFetch jsaFetch = new JsaFetch(new JsaListener() {
            @Override
            public void setSongs(List<Music> list) {
                musicList.postValue(MusicLibraryHelper.jsaMusicToCurrent(list));
            }

            @Override
            public void setAlbums(List<Album> list) {
                albumList.postValue(MusicLibraryHelper.jsaAlbumToCurrent(list));
            }
        });

        jsaFetch.songs(query);
        jsaFetch.albums(query);
    }

    public MutableLiveData<List<com.atul.musicplayeronline.model.Music>> getMusics(){
        return musicList;
    }

    public MutableLiveData<List<com.atul.musicplayeronline.model.Album>> getAlbums(){
        return albumList;
    }
}
