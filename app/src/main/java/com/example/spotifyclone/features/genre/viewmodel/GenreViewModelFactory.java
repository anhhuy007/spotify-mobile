package com.example.spotifyclone.features.genre.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.features.genre.network.GenreService;
import com.example.spotifyclone.shared.network.RetrofitClient;


// for ViewModel to receive variable constructor.
public class GenreViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public GenreViewModelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GenreViewModel.class)) {
            GenreService genreService = RetrofitClient.getClient(context).create(GenreService.class);
            return (T) new GenreViewModel(genreService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}


