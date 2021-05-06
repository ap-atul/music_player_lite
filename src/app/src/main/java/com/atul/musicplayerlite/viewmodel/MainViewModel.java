package com.atul.musicplayerlite.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.atul.musicplayerlite.helper.MusicLibraryHelper;
import com.atul.musicplayerlite.model.Album;
import com.atul.musicplayerlite.model.Artist;
import com.atul.musicplayerlite.model.Folder;
import com.atul.musicplayerlite.model.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class MainViewModel extends ViewModel {

    public List<Music> musicList;
    public List<Album> albumList;
    public List<Artist> artistList;
    public List<Folder> folderList;

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
                Album album = new Album(music.artist, music.album, String.valueOf(music.year), music.duration, list);
                map.put(music.album, album);
            }
        }

        for ( String k : map.keySet()){
            albumList.add(map.get(k));
        }

        Collections.sort(albumList, new AlbumComparator());
        if (reverse)
            Collections.reverse(albumList);

        return albumList;
    }

    public List<Artist> getArtists(boolean reverse) {
        HashMap<String, Artist> map = new HashMap<>();
        artistList = new ArrayList<>();
        albumList = getAlbums(false);

        for( Album album : albumList){
            if (map.containsKey(album.title)){
                Artist artist = map.get(album.artist);
                assert artist != null;

                artist.albums.add(album);
                artist.songCount += album.music.size();
                artist.albumCount += 1;
                map.put(album.artist, artist);

            } else {
                List<Album> list = new ArrayList<>();
                list.add(album);

                Artist artist = new Artist(album.artist, list, album.music.size(), list.size());
                map.put(album.artist, artist);
            }
        }

        for ( String k : map.keySet()){
            artistList.add(map.get(k));
        }

        Collections.sort(artistList, new ArtistComparator());
        if (reverse)
            Collections.reverse(artistList);

        return artistList;
    }

    public List<Folder> getFolders(boolean reverse){
        HashMap<String, Folder> map = new HashMap<>();

        for (Music music: musicList){
            if(map.containsKey(music.relativePath)){
                Folder folder = map.get(music.relativePath);
                folder.songsCount += 1;
                map.put(music.relativePath, folder);
            } else {
                Folder folder = new Folder(1, music.relativePath);
                folderList.add(folder);
                map.put(music.relativePath, folder);
            }
        }

        Collections.sort(folderList, new FolderComparator());
        if (reverse)
            Collections.reverse(folderList);

        return folderList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        musicList = null;
        albumList = null;
        artistList = null;
        folderList = null;
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
        return m1.title.compareTo(m2.title);
    }
}

class ArtistComparator implements Comparator<Artist>{
    @Override
    public int compare(Artist a1, Artist a2) {
        return a1.name.compareTo(a2.name);
    }
}

class FolderComparator implements Comparator<Folder> {

    @Override
    public int compare(Folder f1, Folder f2) {
        return f1.name.compareTo(f2.name);
    }
}