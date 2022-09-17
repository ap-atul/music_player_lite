package com.atul.musicplayer.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.atul.musicplayer.App;
import com.atul.musicplayer.MPPreferences;
import com.atul.musicplayer.model.Album;
import com.atul.musicplayer.model.Artist;
import com.atul.musicplayer.model.Folder;
import com.atul.musicplayer.model.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class MainViewModel extends ViewModel {

    public MutableLiveData<List<Music>> songsList;
    public MutableLiveData<List<Album>> albumList;
    public MutableLiveData<List<Artist>> artistList;
    public MutableLiveData<List<Folder>> folderList;

    public MainViewModel() {
    }

    public MutableLiveData<List<Music>> getSongsList() {
        if (songsList != null) return songsList;
        return songsList = new MutableLiveData<>();
    }

    public void setSongsList(List<Music> musicList) {
        List<String> excludedFolderList = MPPreferences.getExcludedFolders(App.getContext());
        List<Music> songs = new ArrayList<>();

        for (Music music : musicList) {
            if (!excludedFolderList.contains(music.relativePath))
                songs.add(music);
        }

        Collections.sort(songs, new SongComparator());
        if (songsList == null) {
            songsList = new MutableLiveData<>();
        }
        songsList.setValue(songs);
    }

    public MutableLiveData<List<Album>> getAlbumList() {
        if (albumList != null) return albumList;
        return albumList = new MutableLiveData<>();
    }

    public MutableLiveData<List<Artist>> getArtistList() {
        if (artistList != null) return artistList;
        return artistList = new MutableLiveData<>();
    }

    public MutableLiveData<List<Folder>> getFolderList() {
        if (folderList != null) return folderList;
        return folderList = new MutableLiveData<>();
    }

    public void parseFolderList(List<Music> songsList) {
        HashMap<String, Folder> map = new HashMap<>();
        List<Folder> folders = new ArrayList<>();

        for (Music music : songsList) {
            Folder folder;
            if (map.containsKey(music.relativePath)) {
                folder = map.get(music.relativePath);
                assert folder != null;
                folder.songsCount += 1;
            } else {
                folder = new Folder(1, music.relativePath);
                folders.add(folder);
            }
            map.put(music.relativePath, folder);
        }

        Collections.sort(folders, new FolderComparator());
        if (folderList == null)
            folderList = new MutableLiveData<>();
        folderList.setValue(folders);
    }

    public void parseAlbumList(List<Music> songsList) {
        HashMap<String, Album> albumMap = new HashMap<>();

        for (Music music : songsList) {
            if (albumMap.containsKey(music.album)) {
                Album album = albumMap.get(music.album);
                assert album != null;
                album.duration += music.duration;
                album.music.add(music);

                albumMap.put(music.album, album);
            } else {
                List<Music> list = new ArrayList<>();
                list.add(music);
                Album album = new Album(music.artist, music.album, String.valueOf(music.year), music.duration, list);
                albumMap.put(music.album, album);
            }
        }

        List<Album> albums = new ArrayList<>(albumMap.values());
        Collections.sort(albums, new AlbumComparator());

        if (albumList == null)
            albumList = new MutableLiveData<>();
        albumList.setValue(albums);
    }

    public void parseArtistList(List<Album> albums) {
        HashMap<String, Artist> artistMap = new HashMap<>();

        for (Album album : albums) {
            if (artistMap.containsKey(album.artist)) {
                Artist artist = artistMap.get(album.artist);
                assert artist != null;

                artist.albums.add(album);
                artist.songCount += album.music.size();
                artist.albumCount += 1;
                artistMap.put(album.artist, artist);

            } else {
                List<Album> list = new ArrayList<>();
                list.add(album);

                Artist artist = new Artist(album.artist, list, album.music.size(), list.size());
                artistMap.put(album.artist, artist);
            }
        }

        List<Artist> artists = new ArrayList<>(artistMap.values());
        Collections.sort(artists, new ArtistComparator());

        if (artistList == null)
            artistList = new MutableLiveData<>();
        artistList.setValue(artists);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
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
        return Long.compare(m1.track, m2.track);
    }
}

class ArtistComparator implements Comparator<Artist> {
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