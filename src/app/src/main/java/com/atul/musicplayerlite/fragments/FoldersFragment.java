package com.atul.musicplayerlite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.atul.musicplayerlite.R;

public class FoldersFragment extends Fragment {

    public FoldersFragment() {
    }

    public static FoldersFragment newInstance() {
        FoldersFragment fragment = new FoldersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_folders, container, false);

//        PlayerManager playerManager = new PlayerManager(getContext(), musicList);
//        playerManager.start(); // start from first song

        return view;

    }
}