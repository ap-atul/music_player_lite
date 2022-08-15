package com.atul.musicplayer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayer.R;
import com.atul.musicplayer.adapter.SongsAdapter;
import com.atul.musicplayer.dialogs.SongOptionDialog;
import com.atul.musicplayer.helper.ListHelper;
import com.atul.musicplayer.listener.MusicSelectListener;
import com.atul.musicplayer.listener.PlayListListener;
import com.atul.musicplayer.model.Music;
import com.atul.musicplayer.viewmodel.MainViewModel;
import com.atul.musicplayer.viewmodel.MainViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SongsFragment extends Fragment implements SearchView.OnQueryTextListener, PlayListListener {

    private static MusicSelectListener listener;
    private final List<Music> musicList = new ArrayList<>();
    private MainViewModel viewModel;
    private SongsAdapter songsAdapter;
    private List<Music> unChangedList = new ArrayList<>();

    private MaterialToolbar toolbar;
    private SearchView searchView;
    private ExtendedFloatingActionButton shuffleControl;

    public SongsFragment() {
    }

    public static SongsFragment newInstance(MusicSelectListener selectListener) {
        SongsFragment.listener = selectListener;
        return new SongsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity(),
                new MainViewModelFactory(requireActivity())).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        unChangedList = viewModel.getSongs(false);
        musicList.clear();
        musicList.addAll(unChangedList);

        toolbar = view.findViewById(R.id.search_toolbar);

        shuffleControl = view.findViewById(R.id.shuffle_button);
        shuffleControl.setText(String.valueOf(musicList.size()));

        RecyclerView recyclerView = view.findViewById(R.id.songs_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        songsAdapter = new SongsAdapter(listener, this, musicList);
        recyclerView.setAdapter(songsAdapter);

        shuffleControl.setOnClickListener(v -> {
            listener.setShuffleMode(true);
            listener.playQueue(musicList);
        });

        setUpOptions();
        return view;
    }

    private void setUpOptions() {
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.search) {
                searchView = (SearchView) item.getActionView();
                setUpSearchView();
                return true;
            } else if (id == R.id.menu_sort_asc) {
                updateAdapter(ListHelper.sortMusic(musicList, false));
                return true;
            } else if (id == R.id.menu_sort_dec) {
                updateAdapter(ListHelper.sortMusic(musicList, true));
                return true;
            } else if (id == R.id.menu_newest_first) {
                updateAdapter(ListHelper.sortMusicByDateAdded(musicList, false));
                return true;
            } else if (id == R.id.menu_oldest_first) {
                updateAdapter(ListHelper.sortMusicByDateAdded(musicList, true));
                return true;
            }

            return false;
        });
        toolbar.setNavigationOnClickListener(v -> {
            if (searchView == null || searchView.isIconified())
                requireActivity().finish();
        });
    }

    private void setUpSearchView() {
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        updateAdapter(ListHelper.searchMusicByName(unChangedList, query.toLowerCase()));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        updateAdapter(ListHelper.searchMusicByName(unChangedList, newText.toLowerCase()));
        return true;
    }

    private void updateAdapter(List<Music> list) {
        musicList.clear();
        musicList.addAll(list);
        songsAdapter.notifyDataSetChanged();

        shuffleControl.setText(String.valueOf(musicList.size()));
    }

    @Override
    public void option(Context context, Music music) {
        SongOptionDialog dialog = new SongOptionDialog(context, music);
        dialog.show();
    }
}