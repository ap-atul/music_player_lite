package com.atul.musicplayer.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainViewModelFactory implements ViewModelProvider.Factory {


    public MainViewModelFactory() {
    }

    @SuppressWarnings("unchecked") // lint hide
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class))
            return (T) new MainViewModel();

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}