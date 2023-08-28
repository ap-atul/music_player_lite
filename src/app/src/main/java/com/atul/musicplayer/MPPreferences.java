package com.atul.musicplayer;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MPPreferences {
    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                MPConstants.PACKAGE_NAME, Context.MODE_PRIVATE
        );
        return sharedPreferences.edit();
    }

    private static SharedPreferences getSharedPref(Context context) {
        return context.getSharedPreferences(
                MPConstants.PACKAGE_NAME, Context.MODE_PRIVATE
        );
    }

    public static void storeTheme(Context context, int theme) {
        getEditor(context).putInt(MPConstants.SETTINGS_THEME, theme).apply();
    }

    public static int getTheme(Context context) {
        return getSharedPref(context).getInt(MPConstants.SETTINGS_THEME, R.color.blue);
    }

    public static void storeAlbumRequest(Context context, boolean val) {
        getEditor(context).putBoolean(MPConstants.SETTINGS_ALBUM_REQUEST, val).apply();
    }

    public static void storeAutoPlay(Context context, boolean val) {
        getEditor(context).putBoolean(MPConstants.SETTINGS_AUTO_PLAY, val).apply();
    }

    public static boolean getAlbumRequest(Context context) {
        return getSharedPref(context).getBoolean(MPConstants.SETTINGS_ALBUM_REQUEST, false);
    }

    public static boolean getAutoPlay(Context context) {
        return getSharedPref(context).getBoolean(MPConstants.SETTINGS_AUTO_PLAY, true);
    }

    public static void storeThemeMode(Context context, int theme) {
        getEditor(context).putInt(MPConstants.SETTINGS_THEME_MODE, theme).apply();
    }

    public static int getThemeMode(Context context) {
        return getSharedPref(context).getInt(MPConstants.SETTINGS_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public static void storeExcludedFolders(Context context, List<String> folders) {
        folders.removeAll(Arrays.asList("", null));
        getEditor(context).putString(MPConstants.SETTINGS_EXCLUDED_FOLDER, String.join(MPConstants.EXCLUDED_FOLDER_SEPARATOR, folders)).apply();
    }

    public static List<String> getExcludedFolders(Context context) {
        try {
            String[] folders = getSharedPref(context).getString(MPConstants.SETTINGS_EXCLUDED_FOLDER, "").split(MPConstants.EXCLUDED_FOLDER_SEPARATOR);
            return new ArrayList<>(Arrays.asList(folders));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
