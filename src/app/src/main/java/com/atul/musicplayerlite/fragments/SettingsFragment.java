package com.atul.musicplayerlite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayerlite.MPPreferences;
import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.adapter.AccentAdapter;
import com.atul.musicplayerlite.helper.ThemeHelper;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment implements View.OnClickListener{

    private RecyclerView accentView;
    private boolean state;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        SwitchMaterial switchMaterial = view.findViewById(R.id.album_switch);
        accentView = view.findViewById(R.id.accent_view);
        LinearLayout accentOption = view.findViewById(R.id.accent_option);
        LinearLayout albumOption = view.findViewById(R.id.album_options);

        state = MPPreferences.getAlbumRequest(getActivity().getApplicationContext());
        switchMaterial.setChecked(state);

        accentView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        accentView.setAdapter(new AccentAdapter(getActivity()));

        accentOption.setOnClickListener(this);
        albumOption.setOnClickListener(this);
        switchMaterial.setOnClickListener(this);
        return view;

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.accent_option) {
            int visibility = (accentView.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
            accentView.setVisibility(visibility);
        }

        else if (id == R.id.album_options){
            setAlbumRequest();
        }

        else if(id == R.id.album_switch){
            setAlbumRequest();
        }
    }

    private void setAlbumRequest() {
        MPPreferences.storeAlbumRequest(requireActivity().getApplicationContext(), (!state));
        ThemeHelper.applySettings(getActivity());
    }
}