package com.atul.musicplayeronline.online.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayeronline.R;
import com.atul.musicplayeronline.listener.MusicSelectListener;
import com.atul.musicplayeronline.model.Music;
import com.atul.musicplayeronline.online.download.Downloader;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class NetSongsAdapter extends RecyclerView.Adapter<NetSongsAdapter.MyViewHolder> {

    private final List<Music> musicList;
    public MusicSelectListener listener;

    private Downloader downloader;

    public NetSongsAdapter(MusicSelectListener listener, List<Music> musics) {
        this.listener = listener;
        this.musicList = musics;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_net_songs, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.songName.setText(musicList.get(position).title);
        holder.albumName.setText(
                String.format(Locale.getDefault(), "%s • %s",
                        musicList.get(position).artist,
                        musicList.get(position).album)
        );

        Glide.with(holder.albumArt.getContext())
                .load(musicList.get(position).albumArt)
                .into(holder.albumArt);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView songName;
        private final TextView albumName;
        private final ImageView albumArt;
        private final ImageButton download;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            download = itemView.findViewById(R.id.control_download);
            songName = itemView.findViewById(R.id.song_name);
            albumName = itemView.findViewById(R.id.song_album);
            albumArt = itemView.findViewById(R.id.album_art);

            itemView.findViewById(R.id.root_layout).setOnClickListener(v -> {
                listener.setShuffleMode(false);
                listener.playQueue(musicList.subList(getAdapterPosition(), musicList.size()));
            });

            downloader = new Downloader(itemView.getContext().getApplicationContext());
            download.setOnClickListener(v -> {
                download.setAlpha(0.5F);
                downloader.downloadMusic(musicList.get(getAdapterPosition()));
            });
        }
    }
}
