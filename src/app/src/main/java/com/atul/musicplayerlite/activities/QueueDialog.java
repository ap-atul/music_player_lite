package com.atul.musicplayerlite.activities;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.activities.queue.QueueItemCallback;
import com.atul.musicplayerlite.adapter.QueueAdapter;
import com.atul.musicplayerlite.model.Music;
import com.atul.musicplayerlite.player.PlayerQueue;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class QueueDialog extends BottomSheetDialog {

    private QueueAdapter queueAdapter;
    private List<Music> musicList = new ArrayList<>();
    private ItemTouchHelper.Callback callback;
    private ItemTouchHelper touchHelper;

    public QueueDialog(@NonNull Context context, PlayerQueue queue) {
        super(context);
        setContentView(R.layout.dialog_queue);

        musicList.clear();
        musicList.addAll(queue.getCurrentQueue());

        RecyclerView queueLayout = findViewById(R.id.queue_layout);
        assert queueLayout != null;

        queueLayout.setLayoutManager(new LinearLayoutManager(getContext()));
        queueAdapter = new QueueAdapter(context, musicList, queue);
        callback = new QueueItemCallback(queueAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(queueLayout);

        queueLayout.setAdapter(queueAdapter);
    }
}
