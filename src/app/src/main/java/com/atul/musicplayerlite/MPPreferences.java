package com.atul.musicplayerlite;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

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

    public static void storePlayerState(Context context, boolean val){
        getEditor(context).putBoolean(MPConstants.SETTINGS_PLAYER_STATE, val).apply();
    }

    public static int getTheme(Context context){
        return getSharedPref(context).getInt(MPConstants.SETTINGS_THEME, R.color.blue);
    }

    public static boolean getPlayerState(Context context){
        return getSharedPref(context).getBoolean(MPConstants.SETTINGS_PLAYER_STATE, false);
    }
}
