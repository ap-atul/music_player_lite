package com.atul.musicplayerlite;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.atul.musicplayerlite.helper.MusicLibraryHelper;
import com.atul.musicplayerlite.model.Album;
import com.atul.musicplayerlite.model.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class MainViewModel extends ViewModel {

    public List<Music> musicList;
    public List<Album> albumList;
    public List<Album> artistList;

    public MainViewModel(Context context){
        musicList = MusicLibraryHelper.fetchMusicLibrary(context);
        Collections.sort(musicList, new SongComparator());
    }

    public List<Music> getSongs (boolean reverse) {
        if (reverse)
            Collections.reverse(musicList);

        return musicList;
    }

    public List<Album> getAlbums (boolean reverse) {
        HashMap<String, Album> map = new HashMap<>();
        albumList = new ArrayList<>();

        for( Music music : musicList){
            if (map.containsKey(music.album)){
                Album album = map.get(music.album);
                assert album != null;
                album.duration += music.duration;
                album.music.add(music);

                map.put(music.album, album);

            } else {
                List<Music> list = new ArrayList<>();
                list.add(music);
                Album album = new Album(music.album, String.valueOf(music.year), music.duration, list);
                map.put(music.album, album);
            }
        }

        for ( String k : map.keySet()){
            albumList.add(map.get(k));
        }

        if (reverse) {
            Collections.sort(albumList, new AlbumComparator());
            Collections.reverse(albumList);
        }
        else
            Collections.sort(albumList, new AlbumComparator());
        return albumList;
    }

    public List<Album> getArtist (boolean reverse) {
        HashMap<String, Album> map = new HashMap<>();
        artistList = new ArrayList<>();

        for( Music music : musicList){
            if (map.containsKey(music.artist)){
                Album album = map.get(music.artist);
                assert album != null;
                album.duration += music.duration;
                album.music.add(music);

                map.put(music.artist, album);

            } else {
                List<Music> list = new ArrayList<>();
                list.add(music);
                Album album = new Album(music.artist, String.valueOf(music.year), music.duration, list);
                map.put(music.artist, album);
            }
        }

        for ( String k : map.keySet()){
            artistList.add(map.get(k));
        }

        if (reverse) {
            Collections.sort(artistList, new AlbumComparator());
            Collections.reverse(artistList);
        }
        else
            Collections.sort(artistList, new AlbumComparator());
        return artistList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        musicList = null;
    }
}


class AlbumComparator implements Comparator<Album> {
    @Override
    public int compare(Album a1, Album a2) {
        return a1.title.compareTo(a2.title);
    }
}

class SongComparator implements Comparator<Music> {

    @Override
    public int compare(Music m1, Music m2) {
        return m1.displayName.compareTo(m2.displayName);
    }
}