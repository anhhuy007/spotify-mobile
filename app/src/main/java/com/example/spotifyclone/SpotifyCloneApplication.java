package com.example.spotifyclone;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.features.player.model.audio.MusicPlayerController;
import com.example.spotifyclone.features.player.network.SongService;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModelFactory;
import com.example.spotifyclone.shared.network.RetrofitClient;

public class SpotifyCloneApplication extends Application {
    private static SpotifyCloneApplication instance;
    private MusicPlayerViewModelFactory musicPlayerViewModelFactory;
    private ViewModelStore appViewModelStore;
    private MusicPlayerController musicPlayerController;
    private static final String CHANNEL_ID = "SPOTIFY";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Create a single controller instance
        musicPlayerController = MusicPlayerController.getInstance(this);

        // Initialize ViewModelStore
        appViewModelStore = new ViewModelStore();

        // Initialize the factory once
        musicPlayerViewModelFactory = new MusicPlayerViewModelFactory(this);

        // Initialize notification channel
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel Spotify", NotificationManager.IMPORTANCE_DEFAULT);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static SpotifyCloneApplication getInstance() {
        return instance;
    }

    public MusicPlayerViewModelFactory getMusicPlayerViewModelFactory() {
        return musicPlayerViewModelFactory;
    }

    public ViewModelStore getAppViewModelStore() {
        return appViewModelStore;
    }

    public MusicPlayerController getMusicPlayerController() {
        return musicPlayerController;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        try {
            if (musicPlayerController != null && !musicPlayerController.isReleased()) {
                musicPlayerController.close();
            }

            // Clear ViewModel store
            if (appViewModelStore != null) {
                appViewModelStore.clear();
            }
        } catch (Exception e) {
            Log.e("SpotifyCloneApplication", "Error releasing resources", e);
        }
    }
}