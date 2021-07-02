package com.atul.musicplayerlite.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.atul.musicplayerlite.MPConstants;
import com.atul.musicplayerlite.model.Music;

import java.util.List;

@Dao
public interface MusicDao {
    @Insert
    void add(Music music);

    @Delete
    void delete(Music music);

    @Query("SELECT * FROM " + MPConstants.MUSIC_TABLE)
    LiveData<List<Music>> all();
}
