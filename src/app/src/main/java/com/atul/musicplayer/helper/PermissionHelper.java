package com.atul.musicplayer.helper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.atul.musicplayer.MPConstants;

public class PermissionHelper {
    public static boolean hasReadStoragePermission(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        }
        else {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static boolean hasNotificationPermission(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public static boolean requirePermissionRationale(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_MEDIA_AUDIO);
        }
        else {
            return ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    public static void requestStoragePermission(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    context,
                    new String[]{Manifest.permission.READ_MEDIA_AUDIO},
                    MPConstants.PERMISSION_READ_STORAGE
            );
        } else {
            ActivityCompat.requestPermissions(
                    context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MPConstants.PERMISSION_READ_STORAGE
            );
        }
    }

    public static void requestNotificationPermission(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    context,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    MPConstants.PERMISSION_READ_STORAGE
            );
        }
    }
}
