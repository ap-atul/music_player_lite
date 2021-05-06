package com.atul.musicplayerlite;

import com.atul.musicplayerlite.listener.MusicSelectListener;

public class MPConstants {
    public static final String DEBUG_TAG = "MPLite_debug";

    public static final int PERMISSION_READ_STORAGE = 1009;

    public static final String MEDIA_SESSION_TAG = "com.atul.musicplayerlite.MediaSession";

    public static final int NOTIFICATION_ID = 101;
    public static final String PLAY_PAUSE_ACTION = "com.atul.musicplayerlite.PLAYPAUSE";
    public static final String NEXT_ACTION = "com.atul.musicplayerlite.NEXT";
    public static final String PREV_ACTION = "icom.atul.musicplayerlite.PREV";
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
            R.drawable.ic_settings
    };

    public static final String SAVE_INSTANCE_KEY_PLAYER = "save_player";
    public static final String SAVE_INSTANCE_VAL_PLAYER = "player playing";

    public static MusicSelectListener musicSelectListener;
}
