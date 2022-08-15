package com.atul.musicplayer.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayer.R;
import com.atul.musicplayer.listener.ArtistSelectListener;
import com.atul.musicplayer.model.Artist;

import java.util.List;
import java.util.Locale;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.MyViewHolder> {

    private final List<Artist> artistList;
    private final ArtistSelectListener selectListener;

    public ArtistAdapter(ArtistSelectListener selectListener, List<Artist> artistList) {
        this.artistList = artistList;
        this.selectListener = selectListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artists, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.artistName.setText(artistList.get(position).name);
        holder.artistHistory.setText(String.format(Locale.getDefault(), "%d Albums • %d Songs",
                artistList.get(position).albumCount,
                artistList.get(position).songCount));
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView artistName;
        private final TextView artistHistory;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            artistName = itemView.findViewById(R.id.artist_name);
            artistHistory = itemView.findViewById(R.id.artist_history);

            itemView.findViewById(R.id.root_layout).setOnClickListener(v ->
                    selectListener.selectedArtist(artistList.get(getAdapterPosition())));
        }
    }
}
