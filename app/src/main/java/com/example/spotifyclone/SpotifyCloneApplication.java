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
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        registerActivityLifecycleCallbacks(new AppLifecycleHandler(this));

        // Create a single controller instance
        musicPlayerController = MusicPlayerController.getInstance(this);

        // Initialize ViewModelStore
        appViewModelStore = new ViewModelStore();

        // Initialize the factory once
        musicPlayerViewModelFactory = new MusicPlayerViewModelFactory(this);

        // Initialize notification channel
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
            Log.d("TERMINATE","Test");
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