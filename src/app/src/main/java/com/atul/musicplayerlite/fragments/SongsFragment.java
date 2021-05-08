package com.atul.musicplayerlite.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayerlite.MPConstants;
import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.adapter.SongsAdapter;
import com.atul.musicplayerlite.listener.MusicSelectListener;
import com.atul.musicplayerlite.model.Music;
import com.atul.musicplayerlite.viewmodel.MainViewModel;
import com.atul.musicplayerlite.viewmodel.MainViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SongsFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnMenuItemClickListener {

    private static MusicSelectListener listener;
    private MainViewModel viewModel;

    private MaterialToolbar toolbar;
    private SearchView searchView;
    private List<Music> musicList = new ArrayList<>();
    private RecyclerView recyclerView;

    public SongsFragment() {
    }

    public static SongsFragment newInstance(MusicSelectListener selectListener) {
        SongsFragment fragment = new SongsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        SongsFragment.listener = selectListener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity(), new MainViewModelFactory(requireActivity())).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        musicList = viewModel.getSongs(false);

        toolbar = view.findViewById(R.id.search_toolbar);

        ExtendedFloatingActionButton shuffleControl = view.findViewById(R.id.shuffle_button);
        shuffleControl.setText(String.valueOf(musicList.size()));

        recyclerView = view.findViewById(R.id.songs_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        SongsAdapter songsAdapter = new SongsAdapter(listener, musicList);
        recyclerView.setAdapter(songsAdapter);

        shuffleControl.setOnClickListener(v -> {
            listener.setShuffleMode(MPConstants.PLAYER_QUEUE_MODE_SHUFFLE_ON);
            listener.playQueue(musicList);
        });

        setUpOptions();
        return view;
    }

    private void setUpOptions() {
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if(id == R.id.search){
                searchView = (SearchView) item.getActionView();
                setUpSearchView();
                return true;
            }

            else if(id == R.id.menu_sort_asc){
                Log.d(MPConstants.DEBUG_TAG, "Sorting ascending");
                return true;
            }

            else if(id == R.id.menu_sort_dec){
                Log.d(MPConstants.DEBUG_TAG, "Sorting descending");
                return true;
            }

            return false;
        });
    }

    private void setUpSearchView() {
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        List<Music> out = musicList;
        recyclerView.setAdapter(
                new SongsAdapter(listener, viewModel.searchMusicByName(out, query.toLowerCase())));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        List<Music> out = musicList;
        recyclerView.setAdapter(
                new SongsAdapter(listener, viewModel.searchMusicByName(out, newText.toLowerCase())));
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menu_sort_asc){
            Log.d(MPConstants.DEBUG_TAG, "Sorting ascending");
            return true;
        }
        else if(id == R.id.menu_sort_dec){
            Log.d(MPConstants.DEBUG_TAG, "Sorting descending");
            return true;
        }
        return false;
    }
}