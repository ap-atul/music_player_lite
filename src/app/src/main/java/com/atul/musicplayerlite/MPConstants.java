package com.atul.musicplayerlite;

public class MPConstants {
    public static final String DEBUG_TAG = "MPLite_debug";

    public static final int PERMISSION_READ_STORAGE = 1009;

    public static final int NOTIFICATION_INTENT_REQ_CODE = 1010;

    public static final String NOTIFICATION_CHANNEL_ID = "com.atul.musicplayerlite.notificationId";
    public static final String NOTIFICATION_CHANNEL_TITLE = "com.atul.musicplayerlite.notificationTitle";

    public static final String Broadcast_PLAY_NEW_AUDIO = "com.atul.musicplayerlite.PlayNewAudio";

    public static final String MEDIA_SESSION_TAG = "com.atul.musicplayerlite.MediaSession";

    public static final int NOTIFICATION_ID = 101;
    public static final String PLAY_PAUSE_ACTION = "com.atul.musicplayerlite.PLAYPAUSE";
    public static final String NEXT_ACTION = "com.atul.musicplayerlite.NEXT";
    public static final String PREV_ACTION = "icom.atul.musicplayerlite.PREV";
    public static final String CHANNEL_ID = "com.atul.musicplayerlite.CHANNEL_ID";
    public static final int REQUEST_CODE = 100;

    public static final String ACTION_PLAY = "com.atul.musicplayerlite.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.atul.musicplayerlite.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.atul.musicplayerlite.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.atul.musicplayerlite.ACTION_NEXT";
    public static final String ACTION_STOP = "com.atul.musicplayerlite.ACTION_STOP";
    public static final String MEDIA_CHANNEL_ID = "media_playback_channel";

    public static final float VOLUME_DUCK = 0.2f;
    public static final float VOLUME_NORMAL = 1.0f;
    public static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    public static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    public static final int AUDIO_FOCUSED = 2;

    public static final int[] TAB_ICONS = new int[]{
            R.drawable.ic_music_note,
            R.drawable.ic_artist,
            R.drawable.ic_library_music,
            R.drawable.ic_folder_music,
            R.drawable.ic_settings
    };
}
