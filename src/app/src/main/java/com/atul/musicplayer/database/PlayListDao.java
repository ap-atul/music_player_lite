package com.atul.musicplayer.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.atul.musicplayer.MPConstants;
import com.atul.musicplayer.model.PlayList;

import java.util.List;

@Dao
public interface PlayListDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void add(PlayList playList);

    @Delete
    void delete(PlayList playList);

    @Query("SELECT * FROM " + MPConstants.MUSIC_TABLE)
    LiveData<List<PlayList>> all();

    @Update
    void update(PlayList playList);
}
