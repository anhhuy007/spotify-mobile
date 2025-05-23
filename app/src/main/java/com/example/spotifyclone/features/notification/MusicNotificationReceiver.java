// MusicNotificationReceiver.java
package com.example.spotifyclone.features.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;

public class MusicNotificationReceiver extends BroadcastReceiver {
    public static final String ACTION_TOGGLE_SHUFFLE = "com.example.spotifyclone.ACTION_TOGGLE_SHUFFLE";
    private static final String TAG = "MusicNotificationReceiver";
    public static final String ACTION_PLAY_PAUSE = "com.example.spotifyclone.ACTION_PLAY_PAUSE";
    public static final String ACTION_NEXT = "com.example.spotifyclone.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "com.example.spotifyclone.ACTION_PREVIOUS";
    public static final String ACTION_SEEK_TO = "com.example.spotifyclone.ACTION_SEEK_TO";
    public static final String EXTRA_SEEK_POSITION = "seek_position";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            Log.e(TAG, "Received intent with null action");
            return;
        }

        try {
            MusicPlayerViewModel viewModel;
            SpotifyCloneApplication app = SpotifyCloneApplication.getInstance();
            viewModel = new ViewModelProvider(new ViewModelStoreOwner() {
                @NonNull
                @Override
                public ViewModelStore getViewModelStore() {
                    return app.getAppViewModelStore();
                }
            }, app.getMusicPlayerViewModelFactory()).get(MusicPlayerViewModel.class);

            String action = intent.getAction();
            switch (action) {
                case ACTION_PLAY_PAUSE:
                    viewModel.togglePlayPause();
                    break;
                case ACTION_NEXT:
                    if(Boolean.FALSE.equals(viewModel.isAdPlaying().getValue())) {
                        viewModel.playNext();
                    }
                    break;
                case ACTION_PREVIOUS:
                    if(Boolean.FALSE.equals(viewModel.isAdPlaying().getValue())) {
                        viewModel.playPrevious();
                    }
                    break;
                case ACTION_SEEK_TO:
                    if(Boolean.FALSE.equals(viewModel.isAdPlaying().getValue())) {
                        long seekPosition = intent.getLongExtra(EXTRA_SEEK_POSITION, -1);
                        if (seekPosition >= 0) {
                            viewModel.seekTo((int) seekPosition);
                        }
                    }
                    break;
                case ACTION_TOGGLE_SHUFFLE:
                    if(Boolean.FALSE.equals(viewModel.isAdPlaying().getValue())) {
                        viewModel.cycleShuffleMode();
                    }
                    break;
                default:
                    Log.w(TAG, "Unknown action: " + action);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing notification action", e);
        }
    }
}
