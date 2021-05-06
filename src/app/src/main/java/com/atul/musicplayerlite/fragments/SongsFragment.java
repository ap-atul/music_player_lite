package com.atul.musicplayerlite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class SongsFragment extends Fragment {

    private MainViewModel viewModel;
    private static MusicSelectListener listener;

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

        List<Music> musicList = viewModel.getSongs(false);

        ExtendedFloatingActionButton shuffleControl = view.findViewById(R.id.shuffle_button);
        shuffleControl.setText(String.valueOf(musicList.size()));

        RecyclerView recyclerView = view.findViewById(R.id.songs_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new SongsAdapter(listener, musicList));

        shuffleControl.setOnClickListener(v -> {
            listener.setShuffleMode(MPConstants.PLAYER_QUEUE_MODE_SHUFFLE_ON);
            listener.playQueue(musicList);
        });

        return view;
    }
}