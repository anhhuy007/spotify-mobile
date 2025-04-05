package com.example.spotifyclone.features.playlist.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.spotifyclone.features.playlist.network.PlaylistService;
import com.example.spotifyclone.shared.network.RetrofitClient;

public class PlaylistViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public PlaylistViewModelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PlaylistViewModel.class)) {
            PlaylistService playlistService = RetrofitClient.getClient(context).create(PlaylistService.class);
            return (T) new PlaylistViewModel(playlistService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

