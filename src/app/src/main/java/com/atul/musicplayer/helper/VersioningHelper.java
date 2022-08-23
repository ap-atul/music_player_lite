package com.atul.musicplayer.helper;

import android.os.Build;

public class VersioningHelper {
    public static boolean isVersionQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }
}
