package com.atul.musicplayerlite.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.activities.SelectedAlbumActivity;
import com.atul.musicplayerlite.adapter.AlbumsAdapter;
import com.atul.musicplayerlite.listener.AlbumSelectListener;
import com.atul.musicplayerlite.listener.MusicSelectListener;
import com.atul.musicplayerlite.model.Album;
import com.atul.musicplayerlite.viewmodel.MainViewModel;
import com.atul.musicplayerlite.viewmodel.MainViewModelFactory;

import java.util.List;

public class AlbumsFragment extends Fragment implements AlbumSelectListener {

    private static MusicSelectListener musicSelectListener;
    private MainViewModel viewModel;

    public AlbumsFragment() {
    }

    public static AlbumsFragment newInstance(MusicSelectListener listener) {
        musicSelectListener = listener;
        AlbumsFragment fragment = new AlbumsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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

        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        List<Album> albumList = viewModel.getAlbums(false);

        RecyclerView recyclerView = view.findViewById(R.id.albums_layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new AlbumsAdapter(albumList, this));

        return view;
    }

    @Override
    public void playAlbum(Album album) {
        getActivity().startActivity(new Intent(
                getActivity(),
                SelectedAlbumActivity.class
        ).putExtra("album", album));
    }
}