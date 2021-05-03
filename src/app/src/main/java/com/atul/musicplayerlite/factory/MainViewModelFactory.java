package com.atul.musicplayerlite.factory;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.atul.musicplayerlite.MainViewModel;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    private final Context application;

    public MainViewModelFactory(Context application){
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(application);
    }
}