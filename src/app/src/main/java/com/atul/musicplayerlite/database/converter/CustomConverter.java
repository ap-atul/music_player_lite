package com.atul.musicplayerlite.database.converter;

import androidx.room.TypeConverter;

import com.atul.musicplayerlite.model.Music;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class CustomConverter {
    public static final Gson gson = new Gson();

    public static class MusicListConverter {
        @TypeConverter
        public static String musicToStr(List<Music> music) {
            return gson.toJson(music);
        }

        @TypeConverter
        public static List<Music> strToMusic(String data) {
            if (data == null) return Collections.emptyList();
            return gson.fromJson(data, new TypeToken<List<Music>>() {
            }.getType());
        }
    }
}
