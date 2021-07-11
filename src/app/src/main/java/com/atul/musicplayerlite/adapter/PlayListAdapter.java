package com.atul.musicplayerlite.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayerlite.MPPreferences;
import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.model.PlayList;
import com.bumptech.glide.Glide;

import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.MyViewHolder> {
    private final List<PlayList> playLists;
    private final PlayListListener playListListener;

    public PlayListAdapter(PlayListListener playListListener, List<PlayList> playLists) {
        this.playLists = playLists;
        this.playListListener = playListListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(playLists.get(position).title);

        if (holder.state)
            Glide.with(holder.art.getContext())
                    .load(playLists.get(position).musics.get(0).albumArt)
                    .placeholder(R.drawable.ic_album_art)
                    .into(holder.art);
    }

    @Override
    public int getItemCount() {
        return playLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageView art;
        private final boolean state;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            state = MPPreferences.getAlbumRequest(itemView.getContext());
            name = itemView.findViewById(R.id.title);
            art = itemView.findViewById(R.id.art);

            itemView.findViewById(R.id.art_layout).setOnClickListener(v ->
                    playListListener.click(playLists.get(getAdapterPosition())));
        }
    }

    public interface PlayListListener {
        void click(PlayList playList);
    }
}
