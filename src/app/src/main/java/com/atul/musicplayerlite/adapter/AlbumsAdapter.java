package com.atul.musicplayerlite.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.helper.MusicLibraryHelper;
import com.atul.musicplayerlite.model.Album;
import com.atul.musicplayerlite.model.Music;

import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    private final List<Album> albumList;

    public AlbumsAdapter(List<Album> albums){
        this.albumList = albums;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_albums, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.songName.setText(albumList.get(position).title);
        holder.albumName.setText(String.format("%s . %s . %d songs",
                albumList.get(position).music.get(0).artist,
                albumList.get(position).year,
                albumList.get(position).music.size()));

        Bitmap art = MusicLibraryHelper.getThumbnail(holder.albumArt.getContext(),
                albumList.get(position).music.get(0).albumArt);

        if(art == null)
            holder.albumArt.setImageResource(R.drawable.ic_controls_play);
        else
            holder.albumArt.setImageBitmap(art);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView songName;
        private final TextView albumName;
        private final ImageView albumArt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            albumArt = itemView.findViewById(R.id.albumArt);
            songName = itemView.findViewById(R.id.song_name);
            albumName = itemView.findViewById(R.id.song_album);
        }
    }
}
