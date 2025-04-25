package com.example.spotifyclone;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import com.example.spotifyclone.features.player.model.audio.MusicPlayerController;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModelFactory;
import com.example.spotifyclone.shared.repository.PlayerRepository;

public class SpotifyCloneApplication extends Application {
    private static SpotifyCloneApplication instance;
    private MusicPlayerViewModelFactory musicPlayerViewModelFactory;
    private ViewModelStore appViewModelStore;
    private MusicPlayerController musicPlayerController;
    private MusicPlayerViewModel musicPlayerViewModel;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        registerActivityLifecycleCallbacks(new AppLifecycleHandler(this));

        // Initialize ViewModelStore
        appViewModelStore = new ViewModelStore();

        // Initialize the factory once
        musicPlayerViewModelFactory = new MusicPlayerViewModelFactory(this);

        // Create a single controller instance
        musicPlayerController = MusicPlayerController.getInstance(this);

        // Set up MusicPlayerViewModel
        musicPlayerViewModel = new ViewModelProvider(new ViewModelStoreOwner() {
            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                return appViewModelStore;
            }
        }, musicPlayerViewModelFactory).get(MusicPlayerViewModel.class);
        setupPlayerRepositoryAndRestorePlayerViewModel();
    }


    private void setupPlayerRepositoryAndRestorePlayerViewModel() {
        // Create repository instance
        PlayerRepository playerRepository = new PlayerRepository(this);

        // Load the saved player state from repository and configure the view model
        if (musicPlayerViewModel != null) {
            musicPlayerViewModel.setUpMusicPlayerViewModel(playerRepository.loadPlayerState());
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
            // Clear ViewModel store
            if (appViewModelStore != null) {
                appViewModelStore.clear();
                getMusicPlayerController().close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            instance = null;
        }
    }
}