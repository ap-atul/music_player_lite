package com.atul.musicplayer;

import com.atul.musicplayer.listener.MusicSelectListener;

import java.util.Arrays;
import java.util.List;

public class MPConstants {
    public static final String PACKAGE_NAME = "com.atul.musicplayerlite";
    public static final String DEBUG_TAG = "MPLite_debug";
    public static final String GITHUB_REPO_URL = "https://github.com/AP-Atul/music_player_lite";

    public static final int PERMISSION_READ_STORAGE = 1009;
    public static final long TWENTY_SECONDS_IN_MS = 20000;

    public static final String MEDIA_SESSION_TAG = "com.atul.musicplayerlite.MediaSession";

    public static final int NOTIFICATION_ID = 101;
    public static final String PLAY_PAUSE_ACTION = "com.atul.musicplayerlite.PLAYPAUSE";
    public static final String NEXT_ACTION = "com.atul.musicplayerlite.NEXT";
    public static final String PREV_ACTION = "com.atul.musicplayerlite.PREV";
    public static final String CHANNEL_ID = "com.atul.musicplayerlite.CHANNEL_ID";
    public static final int REQUEST_CODE = 100;

    public static final float VOLUME_DUCK = 0.2f;
    public static final float VOLUME_NORMAL = 1.0f;
    public static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    public static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    public static final int AUDIO_FOCUSED = 2;

    public static final int[] TAB_ICONS = new int[]{
            R.drawable.ic_music_note,
            R.drawable.ic_artist,
            R.drawable.ic_library_music,
            R.drawable.ic_settings,
    };
    public static final String SETTINGS_THEME = "shared_pref_theme";
    public static final String SETTINGS_ALBUM_REQUEST = "shared_pref_album_request";
    public static final String SETTINGS_AUTO_PLAY = "shared_pref_auto_play_music";
    public static final String SETTINGS_THEME_MODE = "shared_pref_theme_mode";
    public static final String SETTINGS_EXCLUDED_FOLDER = "shared_pref_excluded_folders";
    public static final String EXCLUDED_FOLDER_SEPARATOR = ":::";
    public static final List<Integer> ACCENT_LIST = Arrays.asList(
            R.color.red,
            R.color.pink,
            R.color.purple,
            R.color.deep_purple,
            R.color.red,
            R.color.indigo,
            R.color.blue,
            R.color.light_blue,
            R.color.cyan,
            R.color.teal,
            R.color.green,
            R.color.light_green,
            R.color.lime,
            R.color.yellow,
            R.color.amber,
            R.color.orange,
            R.color.deep_orange,
            R.color.brown,
            R.color.grey,
            R.color.blue_grey,

            R.color.red_300,
            R.color.pink_300,
            R.color.purple_300,
            R.color.deep_purple_300,
            R.color.red_300,
            R.color.indigo_300,
            R.color.blue_300,
            R.color.light_blue_300,
            R.color.cyan_300,
            R.color.teal_300,
            R.color.green_300,
            R.color.light_green_300,
            R.color.lime_300,
            R.color.yellow_300,
            R.color.amber_300,
            R.color.orange_300,
            R.color.deep_orange_300,
            R.color.brown_300,
            R.color.grey_300,
            R.color.blue_grey_300
    );
    public static final List<Integer> MINUTES_LIST = Arrays.asList(
            5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 65, 70, 75, 80, 85, 90, 95, 100
    );
    public static final int SORT_MUSIC_BY_TITLE = 0;
    public static final int SORT_MUSIC_BY_DATE_ADDED = 1;
    public static final int SORT_ARTIST_BY_NAME = 0;
    public static final int SORT_ARTIST_BY_ALBUMS = 1;
    public static final int SORT_ARTIST_BY_SONGS = 2;
    public static final int SORT_ALBUM_BY_TITLE = 0;
    public static final int SORT_ALBUM_BY_DURATION = 1;
    public static final int SORT_ALBUM_BY_SONGS = 2;
    public static final String PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=com.atul.musicplayer";
    public static MusicSelectListener musicSelectListener;
}
