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
    private final SongService songService;
    private MusicPlayerViewModel cachedViewModel;

    public MusicPlayerViewModelFactory(Context context, SongService songService) {
        this.applicationContext = context.getApplicationContext();
        this.songService = songService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MusicPlayerViewModel.class)) {
            if (cachedViewModel == null) {
                // Get the controller from Application
                MusicPlayerController controller =
                        SpotifyCloneApplication.getInstance().getMusicPlayerController();
                cachedViewModel = new MusicPlayerViewModel(controller, songService);
            }
            return (T) cachedViewModel;
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}