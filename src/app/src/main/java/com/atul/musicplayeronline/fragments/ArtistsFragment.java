package com.atul.musicplayeronline.fragments;

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

import com.atul.musicplayeronline.R;
import com.atul.musicplayeronline.activities.SelectedArtistActivity;
import com.atul.musicplayeronline.adapter.ArtistAdapter;
import com.atul.musicplayeronline.helper.ListHelper;
import com.atul.musicplayeronline.listener.ArtistSelectListener;
import com.atul.musicplayeronline.model.Artist;
import com.atul.musicplayeronline.viewmodel.MainViewModel;
import com.atul.musicplayeronline.viewmodel.MainViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class ArtistsFragment extends Fragment implements SearchView.OnQueryTextListener, ArtistSelectListener {

    private final List<Artist> artistList = new ArrayList<>();
    private MainViewModel viewModel;
    private ArtistAdapter artistAdapter;
    private List<Artist> unchangedList = new ArrayList<>();

    private MaterialToolbar toolbar;
    private SearchView searchView;

    public ArtistsFragment() {
    }

    public static ArtistsFragment newInstance() {
        return new ArtistsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity(), new MainViewModelFactory(requireActivity())).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_artists, container, false);

        unchangedList = viewModel.getArtists(false);
        artistList.addAll(unchangedList);

        toolbar = view.findViewById(R.id.search_toolbar);

        RecyclerView recyclerView = view.findViewById(R.id.artist_layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        artistAdapter = new ArtistAdapter(this, artistList);
        recyclerView.setAdapter(artistAdapter);

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
                updateAdapter(ListHelper.sortArtistByName(artistList, false));
                return true;
            } else if (id == R.id.menu_sort_dec) {
                updateAdapter(ListHelper.sortArtistByName(artistList, true));
                return true;
            } else if (id == R.id.menu_most_songs) {
                updateAdapter(ListHelper.sortArtistBySongs(artistList, false));
                return true;
            } else if (id == R.id.menu_least_songs) {
                updateAdapter(ListHelper.sortArtistBySongs(artistList, true));
                return true;
            } else if (id == R.id.menu_most_albums) {
                updateAdapter(ListHelper.sortArtistByAlbums(artistList, false));
                return true;
            } else if (id == R.id.menu_least_albums) {
                updateAdapter(ListHelper.sortArtistByAlbums(artistList, true));
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
        updateAdapter(ListHelper.searchArtistByName(unchangedList, query.toLowerCase()));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        updateAdapter(ListHelper.searchArtistByName(unchangedList, newText.toLowerCase()));
        return true;
    }

    private void updateAdapter(List<Artist> list) {
        artistList.clear();
        artistList.addAll(list);
        artistAdapter.notifyDataSetChanged();
    }

    @Override
    public void selectedArtist(Artist artist) {
        requireActivity().startActivity(new Intent(
                getActivity(), SelectedArtistActivity.class
        ).putExtra("artist", artist));
    }
}