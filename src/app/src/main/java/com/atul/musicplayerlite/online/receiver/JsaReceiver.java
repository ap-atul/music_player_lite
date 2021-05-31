package com.atul.musicplayerlite.online.receiver;

import androidx.lifecycle.MutableLiveData;

import com.atul.jsa.controller.JsaFetch;
import com.atul.jsa.controller.JsaListener;
import com.atul.jsa.model.Album;
import com.atul.jsa.model.Music;
import com.atul.musicplayerlite.helper.MusicLibraryHelper;

import java.util.List;

public class JsaReceiver {
    private final MutableLiveData<List<com.atul.musicplayerlite.model.Music>> musicList;
    private final MutableLiveData<List<com.atul.musicplayerlite.model.Album>> albumList;

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

    public MutableLiveData<List<com.atul.musicplayerlite.model.Music>> getMusics(){
        return musicList;
    }

    public MutableLiveData<List<com.atul.musicplayerlite.model.Album>> getAlbums(){
        return albumList;
    }
}
