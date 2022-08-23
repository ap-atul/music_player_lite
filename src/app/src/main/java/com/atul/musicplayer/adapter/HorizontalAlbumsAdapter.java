package com.atul.musicplayer.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayer.MPPreferences;
import com.atul.musicplayer.R;
import com.atul.musicplayer.listener.AlbumSelectListener;
import com.atul.musicplayer.model.Album;
import com.bumptech.glide.Glide;

import java.util.List;

public class HorizontalAlbumsAdapter extends RecyclerView.Adapter<HorizontalAlbumsAdapter.MyViewHolder> {

    public final List<Album> albumList;
    public final AlbumSelectListener listener;

    public HorizontalAlbumsAdapter(List<Album> albums, AlbumSelectListener listener) {
        this.albumList = albums;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item_albums, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.albumTitle.setText(albumList.get(position).title);
        if (holder.state)
            Glide.with(holder.albumArt.getContext())
                    .load(albumList.get(position).music.get(0).albumArt)
                    .placeholder(R.drawable.ic_album_art)
                    .into(holder.albumArt);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView albumArt;
        private final TextView albumTitle;
        private final boolean state;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            state = MPPreferences.getAlbumRequest(itemView.getContext());
            albumArt = itemView.findViewById(R.id.album_art);
            albumTitle = itemView.findViewById(R.id.album_title);

            itemView.findViewById(R.id.album_art_layout).setOnClickListener(v ->
                    listener.selectedAlbum(albumList.get(getAdapterPosition())));
        }
    }
}
