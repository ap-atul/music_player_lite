package com.atul.musicplayerlite.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.activities.SelectedAlbumActivity;
import com.atul.musicplayerlite.adapter.AlbumsAdapter;
import com.atul.musicplayerlite.helper.ListHelper;
import com.atul.musicplayerlite.listener.AlbumSelectListener;
import com.atul.musicplayerlite.model.Album;
import com.atul.musicplayerlite.viewmodel.MainViewModel;
import com.atul.musicplayerlite.viewmodel.MainViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class AlbumsFragment extends Fragment implements AlbumSelectListener, SearchView.OnQueryTextListener {

    private AlbumsAdapter albumsAdapter;
    private final List<Album> albumList = new ArrayList<>();
    private List<Album> unchangedList = new ArrayList<>();
    private MainViewModel viewModel;

    private MaterialToolbar toolbar;
    private SearchView searchView;

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

        unchangedList = viewModel.getAlbums(false);
        albumList.addAll(unchangedList);

        toolbar = view.findViewById(R.id.search_toolbar);

        RecyclerView recyclerView = view.findViewById(R.id.albums_layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        albumsAdapter = new AlbumsAdapter(albumList, this);
        recyclerView.setAdapter(albumsAdapter);

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
                updateAdapter(ListHelper.sortAlbumByName(albumList, false));
                return true;
            }

            else if(id == R.id.menu_sort_dec){
                updateAdapter(ListHelper.sortAlbumByName(albumList, true));
                return true;
            }

            else if(id == R.id.menu_most_songs){
                updateAdapter(ListHelper.sortAlbumBySongs(albumList, false));
                return true;
            }

            else if(id == R.id.menu_least_songs){
                updateAdapter(ListHelper.sortAlbumBySongs(albumList, true));
                return true;
            }

            else if(id == R.id.menu_longest_dur){
                updateAdapter(ListHelper.sortAlbumByDuration(albumList, false));
                return true;
            }

            else if(id == R.id.menu_shortest_dur){
                updateAdapter(ListHelper.sortAlbumByDuration(albumList, true));
                return true;
            }

            return false;
        });
        toolbar.setNavigationOnClickListener(v -> {
            if(searchView == null || searchView.isIconified())
                getActivity().finish();
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

    private void updateAdapter(List<Album> list){
        albumList.clear();
        albumList.addAll(list);
        albumsAdapter.notifyDataSetChanged();
    }

    @Override
    public void selectedAlbum(Album album) {
        getActivity().startActivity(new Intent(
                getActivity(),
                SelectedAlbumActivity.class
        ).putExtra("album", album));
    }
}