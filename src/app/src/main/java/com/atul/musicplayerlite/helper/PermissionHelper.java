package com.atul.musicplayerlite.helper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.atul.musicplayerlite.MPConstants;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class PermissionHelper {

    public static boolean manageStoragePermission (Activity context) {
        if (!hasReadStoragePermission(context)){
            // required a dialog?
            if(ActivityCompat.shouldShowRequestPermissionRationale( context, Manifest.permission.READ_EXTERNAL_STORAGE )){
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Requesting permission")
                        .setMessage("Enable storage permission for accessing the media files.")
                        .setPositiveButton("Accept", (dialog, which) -> askReadStoragePermission(context)).show();
            } else
                askReadStoragePermission(context);
        }

        return hasReadStoragePermission(context);
    }

    public static boolean hasReadStoragePermission (Activity context) {
        return (
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        );
    }

    public static void askReadStoragePermission (Activity context) {
        ActivityCompat.requestPermissions(
                context,
                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE},
                MPConstants.PERMISSION_READ_STORAGE
        );
    }
}
