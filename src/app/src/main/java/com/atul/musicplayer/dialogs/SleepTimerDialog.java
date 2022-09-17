package com.atul.musicplayer.dialogs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayer.R;
import com.atul.musicplayer.adapter.SleepTimerAdapter;
import com.atul.musicplayer.listener.MinuteSelectListener;
import com.atul.musicplayer.listener.SleepTimerSetListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SleepTimerDialog extends BottomSheetDialog implements MinuteSelectListener {

    private final TextInputEditText minutesEditText;
    private final TextInputLayout minutesLayout;
    private final SleepTimerSetListener sleepTimerSetListener;

    public SleepTimerDialog(@NonNull Context context, SleepTimerSetListener listener) {
        super(context);
        setContentView(R.layout.dialog_sleep_timer);

        this.sleepTimerSetListener = listener;
        minutesLayout = findViewById(R.id.sleep_timer_minutes_layout);
        minutesEditText = findViewById(R.id.sleep_timer_minutes);
        MaterialButton addSleepTimerButton = findViewById(R.id.add_sleep_timer);
        RecyclerView minuteListView = findViewById(R.id.minutes_layout);

        assert minutesLayout != null;
        assert minutesEditText != null;

        minutesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                minutesLayout.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (minuteListView != null) {
            minuteListView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            minuteListView.setAdapter(new SleepTimerAdapter(this));
        }

        if (addSleepTimerButton == null) {
            return;
        }

        addSleepTimerButton.setOnClickListener(v -> {
            if (minutesEditText.getText() == null || minutesEditText.getText().toString().length() == 0) {
                minutesLayout.setError("Please enter minutes for the timer");
                return;
            }
            int minutes = Integer.parseInt(minutesEditText.getText().toString());
            sleepTimerSetListener.setTimer(minutes);
            this.dismiss();
        });
    }

    @Override
    public void select(int minutes) {
        minutesEditText.setText(String.valueOf(minutes));
    }
}
