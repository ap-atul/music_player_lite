package com.atul.musicplayer.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayer.R;
import com.atul.musicplayer.database.PlayListDatabase;
import com.atul.musicplayer.model.Music;
import com.atul.musicplayer.model.PlayList;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class PlaylistHandlerDialog extends BottomSheetDialog {
    private final PlayListDatabase database;
    private final Music music;

    public PlaylistHandlerDialog(@NonNull Context context, Music music) {
        super(context);
        setContentView(R.layout.dialog_playlist);

        this.music = music;

        RecyclerView playListView = findViewById(R.id.playlist_layout);
        assert playListView != null;
        playListView.setHasFixedSize(true);
        playListView.setLayoutManager(new LinearLayoutManager(getContext()));

        database = PlayListDatabase.getDatabase(context);
        database.dao().all().observeForever(playList -> {
            PlayListAdapter adapter = new PlayListAdapter(playList);
            playListView.setAdapter(adapter);
        });

        TextInputEditText name = findViewById(R.id.new_playlist_name);
        assert name != null;
        MaterialButton add = findViewById(R.id.new_playlist);
        assert add != null;
        add.setOnClickListener(v -> {
            if (name.getText() != null && name.getText().toString().length() > 0) {
                PlayList playList = new PlayList();
                playList.title = name.getText().toString();
                List<Music> musicList = new ArrayList<>();
                musicList.add(music);
                playList.musics = musicList;

                Toast.makeText(getContext(), "Song added to " + playList.title, Toast.LENGTH_SHORT).show();
                dismiss();

                PlayListDatabase.databaseExecutor.execute(() -> database.dao().add(playList));
            } else {
                Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.MyViewHolder> {
        private final List<PlayList> playLists;

        public PlayListAdapter(List<PlayList> playLists) {
            this.playLists = playLists;
        }

        @NonNull
        @Override
        public PlayListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_list, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PlayListAdapter.MyViewHolder holder, int position) {
            holder.name.setText(playLists.get(position).title);
        }

        @Override
        public int getItemCount() {
            return playLists.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private final TextView name;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.name);
                itemView.findViewById(R.id.root_layout).setOnClickListener(v -> {
                    PlayList playList = playLists.get(getAdapterPosition());
                    playList.musics.add(music);

                    Toast.makeText(getContext(), "Song added to " + playList.title, Toast.LENGTH_SHORT).show();
                    dismiss();
                    PlayListDatabase.databaseExecutor.execute(() -> database.dao().update(playList));
                });
            }
        }
    }

}
