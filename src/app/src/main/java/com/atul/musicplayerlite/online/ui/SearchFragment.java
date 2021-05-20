package com.atul.musicplayerlite.online.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayerlite.MPConstants;
import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.activities.SelectedAlbumActivity;
import com.atul.musicplayerlite.listener.AlbumSelectListener;
import com.atul.musicplayerlite.listener.MusicSelectListener;
import com.atul.musicplayerlite.model.Album;
import com.atul.musicplayerlite.model.Music;
import com.atul.musicplayerlite.model.Network;
import com.atul.musicplayerlite.online.receiver.JsaReceiver;
import com.atul.musicplayerlite.online.ui.adapter.NetAlbumsAdapter;
import com.atul.musicplayerlite.online.ui.adapter.NetSongsAdapter;

import java.util.List;

public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener, AlbumSelectListener {

    private RecyclerView songsView;
    private RecyclerView albumsView;
    private TextView songsTitle;
    private TextView albumsTitle;
    private ProgressBar progressBar;
    private SearchView searchView;

    private final MusicSelectListener musicSelectListener = MPConstants.musicSelectListener;
    private NetSongsAdapter songsAdapter;
    private NetAlbumsAdapter albumsAdapter;
    private JsaReceiver receiver;

    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = view.findViewById(R.id.api_search);
        searchView.setOnQueryTextListener(this);

        songsTitle = view.findViewById(R.id.songs_title);
        albumsTitle = view.findViewById(R.id.albums_title);
        progressBar = view.findViewById(R.id.net_progress);

        songsView = view.findViewById(R.id.songs_layout);
        songsView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        albumsView = view.findViewById(R.id.albums_layout);
        albumsView.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false));

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(query.length() > 0){
            progressBar.setVisibility(View.VISIBLE);
            receiver = new JsaReceiver(requireActivity(), query){
                @Override
                public void onReceive(Context context, Intent intent) {
                    super.onReceive(context, intent);

                    Network network = intent.getParcelableExtra(MPConstants.NETWORK_SONGS_KEY);
                    if(network != null){
                        setAdapter(network.musicList);
                    }

                    Network albumNet = intent.getParcelableExtra(MPConstants.NETWORK_ALBUMS_KEY);
                    if(albumNet != null){
                        setAlbumAdapter(albumNet.albumList);
                    }
                }
            };

            requireActivity().registerReceiver(receiver, new IntentFilter(MPConstants.NETWORK_RECEIVER_ID));
            return true;
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void setAdapter(List<Music> list) {
        songsAdapter = new NetSongsAdapter(musicSelectListener, list);
        songsView.setAdapter(songsAdapter);
        songsView.setVisibility(View.VISIBLE);

        if(list.size() > 0) {
            songsTitle.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void setAlbumAdapter(List<Album> albumList) {
        albumsAdapter = new NetAlbumsAdapter(albumList, this);
        albumsView.setAdapter(albumsAdapter);
        albumsView.setVisibility(View.VISIBLE);

        if(albumList.size() > 0) {
            albumsTitle.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void selectedAlbum(Album album) {
        searchView.clearFocus();
        requireActivity().startActivity(new Intent(
                getActivity(),
                SelectedAlbumActivity.class
        ).putExtra("album", album));
    }
}