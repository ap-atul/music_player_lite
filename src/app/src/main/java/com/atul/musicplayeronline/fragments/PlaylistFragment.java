package com.atul.musicplayeronline.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayeronline.MPConstants;
import com.atul.musicplayeronline.MPPreferences;
import com.atul.musicplayeronline.R;
import com.atul.musicplayeronline.adapter.PlayListAdapter;
import com.atul.musicplayeronline.database.PlayListDatabase;
import com.atul.musicplayeronline.helper.MusicLibraryHelper;
import com.atul.musicplayeronline.listener.MusicSelectListener;
import com.atul.musicplayeronline.model.Music;
import com.atul.musicplayeronline.model.PlayList;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlaylistFragment extends Fragment implements PlayListAdapter.PlayListListener {

    private final
    MusicSelectListener musicSelectListener = MPConstants.musicSelectListener;
    private final List<PlayList> playLists = new ArrayList<>();
    private final List<Music> musicList = new ArrayList<>();
    private MaterialToolbar toolbar;
    private PlayList playList;
    private PlayListDatabase database;
    private SongsAdapter adapter;
    private PlayListAdapter playListAdapter;
    private TextView oops;

    public static PlaylistFragment newInstance() {
        return new PlaylistFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        database = PlayListDatabase.getDatabase(requireContext());

        ExtendedFloatingActionButton shuffleControl = view.findViewById(R.id.shuffle_button);
        toolbar = view.findViewById(R.id.search_toolbar);
        oops = view.findViewById(R.id.oops_text);

        RecyclerView playListView = view.findViewById(R.id.playlist_layout);
        playListView.setHasFixedSize(true);
        playListView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        playListAdapter = new PlayListAdapter(this, playLists);
        playListView.setAdapter(playListAdapter);

        RecyclerView recyclerView = view.findViewById(R.id.songs_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SongsAdapter(musicSelectListener, musicList);
        recyclerView.setAdapter(adapter);

        shuffleControl.setOnClickListener(v -> {
            musicSelectListener.setShuffleMode(true);
            musicSelectListener.playQueue(musicList);
        });

        database.dao().all().observe(this, playList -> {
            playLists.clear();
            playLists.addAll(playList);
            playListAdapter.notifyDataSetChanged();

            if (playList.size() > 0) {
                setCurrPlaylist(playList.get(0));
                oops.setVisibility(View.GONE);
            } else {
                oops.setVisibility(View.VISIBLE);
            }
        });

        setUpOptions();

        return view;
    }

    private void setCurrPlaylist(PlayList list) {
        playList = list;
        musicList.clear();
        musicList.addAll(list.musics);
        adapter.notifyDataSetChanged();
    }

    private void setUpOptions() {
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_add_to_queue) {
                musicSelectListener.addToQueue(musicList);
                return true;
            } else if (id == R.id.menu_delete_playlist) {

                if(playList != null) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setMessage("Are you sure you want to delete this playlist?")
                            .setPositiveButton("Delete", (dia, which) -> {
                                deletePlaylist();
                                dia.dismiss();
                            }).setNegativeButton("Cancel", (dia, which) -> dia.dismiss()).show();
                }
            }

            return false;
        });
    }

    private void deletePlaylist() {
        if(playList != null) {
            musicList.clear();
            Toast.makeText(requireContext(), "Playlist deleted successfully", Toast.LENGTH_SHORT).show();
            PlayListDatabase.databaseExecutor.execute(() -> database.dao().delete(playList));
        }
    }

    private void removeSongFromPlayList(Music music) {
        playList.musics.remove(music);
        adapter.notifyDataSetChanged();
        Toast.makeText(requireContext(), "Song removed from playlist", Toast.LENGTH_SHORT).show();
        PlayListDatabase.databaseExecutor.execute(() -> database.dao().update(playList));
    }

    @Override
    public void click(PlayList playList) {
        setCurrPlaylist(playList);
    }

    public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MyViewHolder> {

        private final List<Music> musicList;
        public MusicSelectListener listener;

        public SongsAdapter(MusicSelectListener listener, List<Music> musics) {
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
                    String.format(Locale.getDefault(), "%s • %s",
                            musicList.get(position).artist,
                            musicList.get(position).album)
            );

            if (musicList.get(position).dateAdded == -1)
                holder.songHistory.setVisibility(View.GONE);
            else
                holder.songHistory.setText(
                        String.format(Locale.getDefault(), "%s • %s",
                                MusicLibraryHelper.formatDuration(musicList.get(position).duration),
                                MusicLibraryHelper.formatDate(musicList.get(position).dateAdded))
                );

            if (holder.state)
                Glide.with(holder.albumArt.getContext())
                        .load(musicList.get(position).albumArt)
                        .placeholder(R.drawable.ic_album_art)
                        .into(holder.albumArt);
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
            private final boolean state;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                state = MPPreferences.getAlbumRequest(itemView.getContext());
                albumArt = itemView.findViewById(R.id.album_art);
                songHistory = itemView.findViewById(R.id.song_history);
                songName = itemView.findViewById(R.id.song_name);
                albumName = itemView.findViewById(R.id.song_album);

                MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(itemView.getContext())
                        .setMessage("Are you sure you want to remove this song from the playlist?")
                        .setPositiveButton("Remove", (dia, which) -> {
                            removeSongFromPlayList(musicList.get(getAdapterPosition()));
                            dia.dismiss();
                        }).setNegativeButton("Cancel", (dia, which) -> dia.dismiss());

                itemView.findViewById(R.id.root_layout).setOnClickListener(v -> {
                    listener.setShuffleMode(false);
                    listener.playQueue(musicList.subList(getAdapterPosition(), musicList.size()));
                });

                itemView.findViewById(R.id.root_layout).setOnLongClickListener(v -> {
                    dialog.show();
                    return true;
                });
            }
        }
    }
}

