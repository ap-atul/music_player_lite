package com.atul.musicplayerlite;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.atul.musicplayerlite.helper.MusicLibraryHelper;
import com.atul.musicplayerlite.model.Music;

import java.util.List;


public class MainViewModel extends ViewModel {

    // list from the resources
    public List<Music> musicList;

    public MainViewModel(Context context){
        musicList = MusicLibraryHelper.fetchMusicLibrary(context);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        musicList = null;
    }
}
