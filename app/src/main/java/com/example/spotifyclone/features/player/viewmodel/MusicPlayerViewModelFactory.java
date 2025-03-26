package com.example.spotifyclone.features.player.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.player.model.audio.MusicPlayerController;
import com.example.spotifyclone.features.player.network.SongService;
import android.content.Context;

public class MusicPlayerViewModelFactory implements ViewModelProvider.Factory {
    private final Context applicationContext;
    private MusicPlayerViewModel cachedViewModel;

    public MusicPlayerViewModelFactory(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MusicPlayerViewModel.class)) {
            if (cachedViewModel == null) {
                // Get the controller from Application
                MusicPlayerController controller =
                        SpotifyCloneApplication.getInstance().getMusicPlayerController();
                cachedViewModel = new MusicPlayerViewModel(applicationContext, controller);
            }
            return (T) cachedViewModel;
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}