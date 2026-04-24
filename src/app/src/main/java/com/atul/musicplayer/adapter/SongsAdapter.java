package com.atul.musicplayer.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayer.App;
import com.atul.musicplayer.MPPreferences;
import com.atul.musicplayer.R;
import com.atul.musicplayer.helper.MusicLibraryHelper;
import com.atul.musicplayer.listener.MusicSelectListener;
import com.atul.musicplayer.model.Music;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MyViewHolder> {

    private final List<Music> musicList;
    public final MusicSelectListener listener;
    private final boolean state;

    public SongsAdapter(MusicSelectListener listener, List<Music> musics) {
        this.listener = listener;
        this.musicList = musics;
        this.state = MPPreferences.getAlbumRequest(App.getContext());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songs, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Music music = musicList.get(position);

        holder.songName.setText(music.title);
        holder.albumName.setText(
                String.format(Locale.getDefault(), "%s • %s",
                        music.artist,
                        music.album)
        );

        if (music.dateAdded == -1)
            holder.songHistory.setVisibility(View.GONE);
        else
            holder.songHistory.setText(
                    String.format(Locale.getDefault(), "%s • %s",
                            MusicLibraryHelper.formatDuration(music.duration),
                            MusicLibraryHelper.formatDate(music.dateAdded))
            );

        if (state) {
            if (!music.albumArt.isEmpty()) {
                Glide.with(holder.albumArt.getContext())
                        .load(music.albumArt)
                        .placeholder(R.drawable.ic_album_art)
                        .into(holder.albumArt);
            } else {
                holder.albumArt.setImageResource(R.drawable.ic_album_art);
            }
        } else {
            holder.albumArtLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView songName;
        private final TextView albumName;
        private final TextView songHistory;
        private final ImageView albumArt;
        private final MaterialCardView albumArtLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            albumArt = itemView.findViewById(R.id.album_art);
            songHistory = itemView.findViewById(R.id.song_history);
            songName = itemView.findViewById(R.id.song_name);
            albumName = itemView.findViewById(R.id.song_album);
            albumArtLayout = itemView.findViewById(R.id.album_art_layout);


            itemView.findViewById(R.id.root_layout).setOnClickListener(v -> {
                    List<Music> toPlay = new ArrayList<>();
                    boolean autoPlay = MPPreferences.getAutoPlay(itemView.getContext());
                    if (autoPlay) {
                        toPlay.addAll(musicList.subList(getAdapterPosition(), musicList.size()));
                    } else {
                        toPlay.add(musicList.get(getAdapterPosition()));
                    }
                    listener.playQueue(toPlay, false);
                }
            );
        }
    }
}
