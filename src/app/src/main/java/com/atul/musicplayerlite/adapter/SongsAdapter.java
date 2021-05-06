package com.atul.musicplayerlite.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.helper.MusicLibraryHelper;
import com.atul.musicplayerlite.listener.MusicSelectListener;
import com.atul.musicplayerlite.model.Music;

import java.util.List;
import java.util.Locale;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MyViewHolder> {

    private final List<Music> musicList;
    public MusicSelectListener listener;

    public SongsAdapter(MusicSelectListener listener, List<Music> musics){
        this.listener = listener;
        this.musicList = musics;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songs, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.songName.setText(musicList.get(position).title);
        holder.albumName.setText(
                String.format(Locale.getDefault(), "%s . %s",
                        musicList.get(position).artist,
                        musicList.get(position).album)
        );
        holder.songHistory.setText(
                String.format(Locale.getDefault(), "%s . %s",
                        MusicLibraryHelper.formatDuration(musicList.get(position).duration),
                        MusicLibraryHelper.formatDate(musicList.get(position).dateAdded))
        );
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView songName;
        private final TextView albumName;
        private final TextView songHistory;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            songHistory = itemView.findViewById(R.id.song_history);
            songName = itemView.findViewById(R.id.song_name);
            albumName = itemView.findViewById(R.id.song_album);

            itemView.findViewById(R.id.root_layout).setOnClickListener(v ->
                    listener.playQueue(musicList.subList(getAdapterPosition(), musicList.size())));
        }
    }
}
