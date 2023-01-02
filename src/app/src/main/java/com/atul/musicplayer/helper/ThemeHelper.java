package com.atul.musicplayer.helper;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

import com.atul.musicplayer.MainActivity;
import com.atul.musicplayer.R;

import java.util.HashMap;

public class ThemeHelper {

    public static HashMap<Integer, Integer> getThemeMap() {
        HashMap<Integer, Integer> themes = new HashMap<>();
        themes.put(R.color.red, R.style.BaseTheme_Red);
        themes.put(R.color.pink, R.style.BaseTheme_Pink);
        themes.put(R.color.purple, R.style.BaseTheme_Purple);
        themes.put(R.color.deep_purple, R.style.BaseTheme_DeepPurple);
        themes.put(R.color.indigo, R.style.BaseTheme_Indigo);
        themes.put(R.color.blue, R.style.BaseTheme_Blue);
        themes.put(R.color.light_blue, R.style.BaseTheme_LightBlue);
        themes.put(R.color.cyan, R.style.BaseTheme_Cyan);
        themes.put(R.color.teal, R.style.BaseTheme_Teal);
        themes.put(R.color.green, R.style.BaseTheme_Green);
        themes.put(R.color.light_green, R.style.BaseTheme_LightGreen);
        themes.put(R.color.lime, R.style.BaseTheme_Lime);
        themes.put(R.color.yellow, R.style.BaseTheme_Yellow);
        themes.put(R.color.amber, R.style.BaseTheme_Amber);
        themes.put(R.color.orange, R.style.BaseTheme_Orange);
        themes.put(R.color.deep_orange, R.style.BaseTheme_DeepOrange);
        themes.put(R.color.brown, R.style.BaseTheme_Brown);
        themes.put(R.color.grey, R.style.BaseTheme_Grey);
        themes.put(R.color.blue_grey, R.style.BaseTheme_BlueGrey);

        themes.put(R.color.red_300, R.style.BaseTheme_Red300);
        themes.put(R.color.pink_300, R.style.BaseTheme_Pink300);
        themes.put(R.color.purple_300, R.style.BaseTheme_Purple300);
        themes.put(R.color.deep_purple_300, R.style.BaseTheme_DeepPurple300);
        themes.put(R.color.indigo_300, R.style.BaseTheme_Indigo300);
        themes.put(R.color.blue_300, R.style.BaseTheme_Blue300);
        themes.put(R.color.light_blue_300, R.style.BaseTheme_LightBlue300);
        themes.put(R.color.cyan_300, R.style.BaseTheme_Cyan300);
        themes.put(R.color.teal_300, R.style.BaseTheme_Teal300);
        themes.put(R.color.green_300, R.style.BaseTheme_Green300);
        themes.put(R.color.light_green_300, R.style.BaseTheme_LightGreen300);
        themes.put(R.color.lime_300, R.style.BaseTheme_Lime300);
        themes.put(R.color.yellow_300, R.style.BaseTheme_Yellow300);
        themes.put(R.color.amber_300, R.style.BaseTheme_Amber300);
        themes.put(R.color.orange_300, R.style.BaseTheme_Orange300);
        themes.put(R.color.deep_orange_300, R.style.BaseTheme_DeepOrange300);
        themes.put(R.color.brown_300, R.style.BaseTheme_Brown300);
        themes.put(R.color.grey_300, R.style.BaseTheme_Grey300);
        themes.put(R.color.blue_grey_300, R.style.BaseTheme_BlueGrey300);

        return themes;
    }

    public static Integer getTheme(Integer accentColor) {
        Integer theme = getThemeMap().get(accentColor);
        if(theme == null) {
            return getThemeMap().get(R.color.blue);
        }
        return theme;
    }

    public static void applySettings(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK
        );

        activity.finishAfterTransition();
        activity.startActivity(intent);
        activity.overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );
    }

    public static int resolveColorAttr(Context context, int attr) {
        TypedValue resolveTheme = resolveThemeAttr(context, attr);
        int color = resolveTheme.resourceId != 0
                ? resolveTheme.resourceId : resolveTheme.data;

        return ContextCompat.getColor(context, color);
    }

    private static TypedValue resolveThemeAttr(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue;
    }
}
