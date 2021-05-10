package com.atul.musicplayerlite.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    private final Context application;

    public MainViewModelFactory(Context application) {
        this.application = application;
    }

    @SuppressWarnings("unchecked") // lint hide
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(MainViewModel.class))
            return (T) new MainViewModel(application);

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}