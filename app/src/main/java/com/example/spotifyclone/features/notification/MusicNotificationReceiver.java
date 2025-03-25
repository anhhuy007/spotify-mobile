package com.example.spotifyclone.features.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;

public class MusicNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            MusicPlayerViewModel viewModel;
            SpotifyCloneApplication app = SpotifyCloneApplication.getInstance();
            viewModel = new ViewModelProvider(new ViewModelStoreOwner() {
                @NonNull
                @Override
                public ViewModelStore getViewModelStore() {
                    return app.getAppViewModelStore();
                }
            }, app.getMusicPlayerViewModelFactory()).get(MusicPlayerViewModel.class);

            switch (action) {
                case "ACTION_PLAY_PAUSE":
                    viewModel.togglePlayPause();
                    break;
                case "ACTION_NEXT":
                    viewModel.playNext();
                    break;
                case "ACTION_PREVIOUS":
                    viewModel.playPrevious();
                    break;
                case "ACTION_SHUFFLE":
                    viewModel.cycleShuffleMode();
                    break;
                case "ACTION_CLOSE":
                    viewModel.stop();
                    break;
            }
        }
    }
}