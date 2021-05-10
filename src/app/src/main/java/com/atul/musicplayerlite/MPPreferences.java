package com.atul.musicplayerlite;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class MPPreferences {
    private static SharedPreferences.Editor getEditor(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                MPConstants.PACKAGE_NAME, Context.MODE_PRIVATE
        );
        return sharedPreferences.edit();
    }

    private static SharedPreferences getSharedPref(Context context){
        return context.getSharedPreferences(
                MPConstants.PACKAGE_NAME, Context.MODE_PRIVATE
        );
    }

    public static void storeTheme(Context context, int theme){
        getEditor(context).putInt(MPConstants.SETTINGS_THEME, theme).apply();
    }
    public static int getTheme(Context context){
        return getSharedPref(context).getInt(MPConstants.SETTINGS_THEME, R.color.blue);
    }

    public static void storeAlbumRequest(Context context, boolean val){
        getEditor(context).putBoolean(MPConstants.SETTINGS_ALBUM_REQUEST, val).apply();
    }

    public static boolean getAlbumRequest(Context context){
        return getSharedPref(context).getBoolean(MPConstants.SETTINGS_ALBUM_REQUEST, false);
    }

    public static void storeThemeMode(Context context, int theme){
        getEditor(context).putInt(MPConstants.SETTINGS_THEME_MODE, theme).apply();
    }

    public static int getThemeMode(Context context){
        return getSharedPref(context).getInt(MPConstants.SETTINGS_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }
}
