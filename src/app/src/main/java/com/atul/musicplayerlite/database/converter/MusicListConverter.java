package com.atul.musicplayerlite.database.converter;

import androidx.room.TypeConverter;

import com.atul.musicplayerlite.model.Music;
import com.google.gson.Gson;

public class MusicListConverter {
    static Gson gson = new Gson();

    @TypeConverter
    public static String musicToStr(Music music){
        return gson.toJson(music);
    }

    @TypeConverter
    public static Music strToMusic(String data){
        return gson.fromJson(data, Music.class);
    }
}
