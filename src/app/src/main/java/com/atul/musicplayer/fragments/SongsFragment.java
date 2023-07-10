package com.atul.musicplayer.fragments;

import android.annotation.SuppressLint;
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
import com.atul.musicplayer.helper.ListHelper;
import com.atul.musicplayer.listener.MusicSelectListener;
import com.atul.musicplayer.model.Music;
import com.atul.musicplayer.viewmodel.MainViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SongsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static MusicSelectListener listener;
    private final List<Music> musicList = new ArrayList<>();
    private MainViewModel viewModel;
    private SongsAdapter songsAdapter;
    private List<Music> unChangedList = new ArrayList<>();

    private MaterialToolbar toolbar;
    private SearchView searchView;
    private ExtendedFloatingActionButton shuffleControl;

    public SongsFragment() {
        // Unused
    }

    public static SongsFragment newInstance(MusicSelectListener selectListener) {
        SongsFragment.listener = selectListener;
        return new SongsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        toolbar = view.findViewById(R.id.search_toolbar);

        shuffleControl = view.findViewById(R.id.shuffle_button);

        RecyclerView recyclerView = view.findViewById(R.id.songs_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        songsAdapter = new SongsAdapter(listener, musicList);
        recyclerView.setAdapter(songsAdapter);

        shuffleControl.setOnClickListener(v -> listener.playQueue(musicList, true));
        viewModel.getSongsList().observe(requireActivity(), this::setUpUi);

        setUpOptions();
        return view;
    }

    private void setUpUi(List<Music> songList) {
        unChangedList = songList;
        musicList.clear();
        musicList.addAll(unChangedList);
        shuffleControl.setText(String.valueOf(songList.size()));
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
        onQueryTextChange(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        setSearchResult(query);
        return true;
    }

    private void setSearchResult(String query) {
        if(query.length() > 0) {
            updateAdapter(ListHelper.searchMusicByName(unChangedList, query.toLowerCase()));
        }else {
            updateAdapter(unChangedList);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateAdapter(List<Music> list) {
        musicList.clear();
        musicList.addAll(list);
        songsAdapter.notifyDataSetChanged();

        shuffleControl.setText(String.valueOf(musicList.size()));
    }
}