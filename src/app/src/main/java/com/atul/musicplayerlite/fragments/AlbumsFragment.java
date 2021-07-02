package com.atul.musicplayerlite.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.activities.PlaylistActivity;
import com.atul.musicplayerlite.activities.SelectedAlbumActivity;
import com.atul.musicplayerlite.adapter.AlbumsAdapter;
import com.atul.musicplayerlite.adapter.PlayListAdapter;
import com.atul.musicplayerlite.database.PlayListDatabase;
import com.atul.musicplayerlite.helper.ListHelper;
import com.atul.musicplayerlite.listener.AlbumSelectListener;
import com.atul.musicplayerlite.model.Album;
import com.atul.musicplayerlite.model.PlayList;
import com.atul.musicplayerlite.viewmodel.MainViewModel;
import com.atul.musicplayerlite.viewmodel.MainViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class AlbumsFragment extends Fragment implements AlbumSelectListener, SearchView.OnQueryTextListener, PlayListAdapter.PlayListListener {

    private final List<Album> albumList = new ArrayList<>();
    private final List<PlayList> playLists = new ArrayList<>();
    private AlbumsAdapter albumsAdapter;
    private List<Album> unchangedList = new ArrayList<>();
    private MainViewModel viewModel;
    private PlayListAdapter playListAdapter;

    private MaterialToolbar toolbar;
    private SearchView searchView;
    private TextView playlistHeader;

    public AlbumsFragment() {
    }

    public static AlbumsFragment newInstance() {
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
        PlayListDatabase database = PlayListDatabase.getDatabase(requireContext());

        unchangedList = viewModel.getAlbums(false);
        albumList.addAll(unchangedList);

        toolbar = view.findViewById(R.id.search_toolbar);
        playlistHeader = view.findViewById(R.id.playlist_header);

        RecyclerView recyclerView = view.findViewById(R.id.albums_layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
        albumsAdapter = new AlbumsAdapter(albumList, this);
        recyclerView.setAdapter(albumsAdapter);

        RecyclerView playListView = view.findViewById(R.id.playlist_layout);
        playListView.setHasFixedSize(true);
        playListView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        playListAdapter = new PlayListAdapter(this, playLists);
        playListView.setAdapter(playListAdapter);

        database.dao().all().observe(this, playList -> {
            playLists.clear();
            playLists.addAll(playList);
            playListAdapter.notifyDataSetChanged();

            if (playList.size() > 0)
                playlistHeader.setVisibility(View.VISIBLE);
            else
                playlistHeader.setVisibility(View.GONE);
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
                updateAdapter(ListHelper.sortAlbumByName(albumList, false));
                return true;
            } else if (id == R.id.menu_sort_dec) {
                updateAdapter(ListHelper.sortAlbumByName(albumList, true));
                return true;
            } else if (id == R.id.menu_most_songs) {
                updateAdapter(ListHelper.sortAlbumBySongs(albumList, false));
                return true;
            } else if (id == R.id.menu_least_songs) {
                updateAdapter(ListHelper.sortAlbumBySongs(albumList, true));
                return true;
            } else if (id == R.id.menu_longest_dur) {
                updateAdapter(ListHelper.sortAlbumByDuration(albumList, false));
                return true;
            } else if (id == R.id.menu_shortest_dur) {
                updateAdapter(ListHelper.sortAlbumByDuration(albumList, true));
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
        updateAdapter(ListHelper.searchByAlbumName(unchangedList, query.toLowerCase()));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        updateAdapter(ListHelper.searchByAlbumName(unchangedList, newText.toLowerCase()));
        return true;
    }

    private void updateAdapter(List<Album> list) {
        albumList.clear();
        albumList.addAll(list);
        albumsAdapter.notifyDataSetChanged();
    }

    @Override
    public void selectedAlbum(Album album) {
        requireActivity().startActivity(new Intent(
                getActivity(),
                SelectedAlbumActivity.class
        ).putExtra("album", album));
    }

    @Override
    public void click(PlayList playList) {
        startActivity(new Intent(requireContext(), PlaylistActivity.class)
                .putExtra("playlist", playList));
    }
}