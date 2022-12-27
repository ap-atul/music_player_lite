package com.atul.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayer.R;
import com.atul.musicplayer.activities.queue.QueueItemCallback;
import com.atul.musicplayer.helper.ThemeHelper;
import com.atul.musicplayer.model.Music;
import com.atul.musicplayer.player.PlayerQueue;

import java.util.Collections;
import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.MyViewHolder> implements QueueItemCallback.QueueItemInterface {

    private final List<Music> musicList;
    private final PlayerQueue playerQueue;
    private final Music currentMusic;
    private final @ColorInt
    int colorInt;
    int defaultTint;

    public QueueAdapter(Context context, List<Music> musics, PlayerQueue playerQueue) {
        this.musicList = musics;
        this.playerQueue = playerQueue;
        this.currentMusic = playerQueue.getCurrentMusic();

        colorInt = ThemeHelper.resolveColorAttr(
                context,
                R.attr.colorPrimary
        );

        defaultTint = context.getColor(R.color.colorTextMed);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_queue, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.songName.setText(musicList.get(position).title);

        if (currentMusic.title.equals(musicList.get(position).title)) {
            holder.albumName.setText(R.string.now_playing);
            holder.albumName.setTextColor(colorInt);
            holder.drag.setImageResource(R.drawable.ic_current_playing);
        } else {
            holder.albumName.setText(musicList.get(position).artist);
            holder.albumName.setTextColor(defaultTint);
            holder.drag.setImageResource(R.drawable.ic_drag_handle);
        }
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(musicList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(musicList, i, i - 1);
            }
        }

        playerQueue.swap(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(MyViewHolder myViewHolder) {
    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder) {
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView songName;
        private final TextView albumName;
        private final ImageButton drag;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            songName = itemView.findViewById(R.id.song_name);
            albumName = itemView.findViewById(R.id.song_album);
            drag = itemView.findViewById(R.id.control_drag);

            itemView.findViewById(R.id.control_close).setOnClickListener(v -> {
                int position = getAdapterPosition();

                if(position >= 0 && position < musicList.size()) {
                    boolean isPlaying = currentMusic.title.equals(musicList.get(position).title);

                    if (!isPlaying) {
                        musicList.remove(position);
                        playerQueue.removeMusicFromQueue(position);
                        notifyItemRemoved(position);
                    }
                }
            });
        }
    }
}
