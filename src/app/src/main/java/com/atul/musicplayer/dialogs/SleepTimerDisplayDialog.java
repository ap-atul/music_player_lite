package com.atul.musicplayer.dialogs;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.atul.musicplayer.R;
import com.atul.musicplayer.listener.SleepTimerSetListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class SleepTimerDisplayDialog extends BottomSheetDialog {

    private final SleepTimerSetListener sleepTimerSetListener;

    public SleepTimerDisplayDialog(@NonNull Context context, SleepTimerSetListener listener) {
        super(context);
        setContentView(R.layout.dialog_display_sleep_timer);

        this.sleepTimerSetListener = listener;

        TextView timerView = findViewById(R.id.time_remaining);
        MaterialButton close = findViewById(R.id.close_dialog);
        MaterialButton stop = findViewById(R.id.stop_sleep_timer);

        if (timerView != null) {
            this.sleepTimerSetListener.getTick().observe((LifecycleOwner) context, tick ->
                    timerView.setText(getTimeRemaining(tick)));
        }

        if (close != null)
            close.setOnClickListener(v -> this.dismiss());

        if (stop != null)
            stop.setOnClickListener(v -> {
                sleepTimerSetListener.cancelTimer();
                this.dismiss();
            });
    }

    private String getTimeRemaining(long ms) {
        int totalSeconds = (int) (ms / 1000);
        int hours = totalSeconds / 3600;
        int minutes = totalSeconds / 60 % 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%1d:%02d:%02d", hours, minutes, seconds);
    }
}
