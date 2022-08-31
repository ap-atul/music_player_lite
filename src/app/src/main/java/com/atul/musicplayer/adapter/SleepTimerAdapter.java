package com.atul.musicplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayer.MPConstants;
import com.atul.musicplayer.R;
import com.atul.musicplayer.listener.MinuteSelectListener;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SleepTimerAdapter extends RecyclerView.Adapter<SleepTimerAdapter.MyViewHolder> {

    private final List<Integer> minutes;
    private final MinuteSelectListener listener;

    public SleepTimerAdapter(MinuteSelectListener listener) {
        this.minutes = MPConstants.MINUTES_LIST;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_minutes, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.minuteButton.setText(String.valueOf(minutes.get(position)));
    }

    @Override
    public int getItemCount() {
        return minutes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final MaterialButton minuteButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            minuteButton = itemView.findViewById(R.id.minutes_item);

            minuteButton.setOnClickListener(v -> listener.select(minutes.get(getAdapterPosition())));
        }
    }
}
