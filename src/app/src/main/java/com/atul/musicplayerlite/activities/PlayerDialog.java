package com.atul.musicplayerlite.activities;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.atul.musicplayerlite.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class PlayerDialog extends BottomSheetDialog {
    public PlayerDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_player);
    }
}
