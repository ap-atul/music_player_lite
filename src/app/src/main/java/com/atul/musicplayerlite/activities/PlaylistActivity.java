package com.atul.musicplayerlite.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayerlite.MPConstants;
import com.atul.musicplayerlite.MPPreferences;
import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.database.PlayListDatabase;
import com.atul.musicplayerlite.helper.MusicLibraryHelper;
import com.atul.musicplayerlite.helper.ThemeHelper;
import com.atul.musicplayerlite.listener.MusicSelectListener;
import com.atul.musicplayerlite.model.Music;
import com.atul.musicplayerlite.model.PlayList;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;
import java.util.Locale;

public class PlaylistActivity extends AppCompatActivity {

    private final
    MusicSelectListener musicSelectListener = MPConstants.musicSelectListener;

    private MaterialToolbar toolbar;
    private PlayList playList;
    private PlayListDatabase database;
    private SongsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeHelper.getTheme(MPPreferences.getTheme(getApplicationContext())));
        AppCompatDelegate.setDefaultNightMode(MPPreferences.getThemeMode(getApplicationContext()));
        setContentView(R.layout.activity_playlist);

        database = PlayListDatabase.getDatabase(this);
        playList = getIntent().getParcelableExtra("playlist");

        ExtendedFloatingActionButton shuffleControl = findViewById(R.id.shuffle_button);
        toolbar = findViewById(R.id.search_toolbar);
        toolbar.setTitle(playList.title);
        toolbar.setSubtitle(String.format(Locale.getDefault(), "%d songs",
                playList.musics.size()));

        RecyclerView recyclerView = findViewById(R.id.songs_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SongsAdapter(musicSelectListener, playList.musics);
        recyclerView.setAdapter(adapter);

        shuffleControl.setOnClickListener(v -> {
            musicSelectListener.setShuffleMode(true);
            musicSelectListener.playQueue(playList.musics);
        });

        setUpOptions();
    }

    private void setUpOptions() {
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_add_to_queue) {
                musicSelectListener.addToQueue(playList.musics);
                return true;
            } else if (id == R.id.menu_delete_playlist) {

                new MaterialAlertDialogBuilder(this)
                        .setMessage("Are you sure you want to delete this playlist?")
                        .setPositiveButton("Delete", (dia, which) -> {
                            deletePlaylist();
                            dia.dismiss();
                        }).setNegativeButton("Cancel", (dia, which) -> dia.dismiss()).show();
            }

            return false;
        });
        toolbar.setNavigationOnClickListener(v ->
                finish()
        );
    }

    private void deletePlaylist() {
        Toast.makeText(this, "Playlist deleted successfully", Toast.LENGTH_SHORT).show();
        PlayListDatabase.databaseExecutor.execute(() -> database.dao().delete(playList));
        finish();
    }

    private void removeSongFromPlayList(Music music) {
        playList.musics.remove(music);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Song removed from playlist", Toast.LENGTH_SHORT).show();
        PlayListDatabase.databaseExecutor.execute(() -> database.dao().update(playList));
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

            if(musicList.get(position).dateAdded == -1)
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

