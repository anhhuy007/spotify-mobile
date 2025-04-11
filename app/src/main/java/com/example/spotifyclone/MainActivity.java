package com.example.spotifyclone;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.spotifyclone.features.authentication.network.TokenManager;
import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.features.home.ui.HomeFragment;
import com.example.spotifyclone.features.library.ui.LibraryFragment;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModel;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.ui.PlayerBottomSheetFragment;
import com.example.spotifyclone.features.player.ui.SleepTimerDialog;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.example.spotifyclone.features.premium.ui.PremiumFragment;
import com.example.spotifyclone.features.search.ui.SearchFragment;
import com.example.spotifyclone.shared.model.PlayerState;
import com.example.spotifyclone.shared.model.User;
import com.example.spotifyclone.shared.repository.PlayerRepository;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import android.Manifest;

import android.util.Log;

import java.io.File;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private CardView miniPlayer;
    private ImageView miniPlayerImage;
    private TextView miniPlayerSongName, miniPlayerArtistName;
    private ImageButton miniPlayerPlayPauseButton, miniPlayerNextButton, miniPlayerPreviousButton;
    private MusicPlayerViewModel viewModel;
    private ProgressBar miniPlayerProgress;
    private EditText search_input; // genre-ids
    private AlbumViewModel albumViewModel;
    private NavController navController;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private User currentUser;
    private PlayerRepository playerRepository;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            initUser();
            initUI();
            initViewModel();
            observeViewModel();
            setupPlayerRepositoryAndRestorePlayerViewModel();
            setupListeners();
            setupNavigation();
            checkNotificationPermission();
        }

    private void setupPlayerRepositoryAndRestorePlayerViewModel() {
        this.playerRepository = new PlayerRepository(this);
        viewModel.setUpMusicPlayerViewModel(playerRepository.loadPlayerState());
    }

    private void initUser() {
        AuthRepository authRepository = new AuthRepository(getApplicationContext());
        currentUser = authRepository.getUser();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.bottom_nav);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navView, navController);

        // Setup Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // Method to open drawer from fragments
    public void openDrawer() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation drawer item clicks
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            // TODO: Handle navigation to profile
            // Could use Navigation Component to navigate to profileFragment
            navController.navigate(R.id.topArtist);
//            navController.navigate(R.id.editProfileFragment);

        } else if (id == R.id.nav_settings) {
             navController.navigate(R.id.settingsFragment);

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initUI() {
        miniPlayer = findViewById(R.id.mini_player);
        miniPlayerImage = findViewById(R.id.mini_player_image);
        miniPlayerSongName = findViewById(R.id.mini_player_title);
        miniPlayerArtistName = findViewById(R.id.mini_player_artist);
        miniPlayerPlayPauseButton = findViewById(R.id.mini_player_play_pause);
        miniPlayerNextButton = findViewById(R.id.mini_player_next);
        miniPlayerPreviousButton = findViewById(R.id.mini_player_prev);
        miniPlayer.setVisibility(View.GONE);
        miniPlayerProgress = findViewById(R.id.mini_player_progress);

        // Nav header in side bar
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        ImageView userAvatar =  headerView.findViewById(R.id.drawer_header_avatar);
        TextView userName = headerView.findViewById(R.id.drawer_header_username);
        TextView userEmail = headerView.findViewById(R.id.drawer_header_email);

        Picasso.get().load(currentUser.getAvatarUrl()).into(userAvatar);
        userName.setText(currentUser.getUsername());
        userEmail.setText(currentUser.getEmail());
    }

    private void initViewModel() {
        SpotifyCloneApplication app = SpotifyCloneApplication.getInstance();
        viewModel = new ViewModelProvider(new ViewModelStoreOwner() {
            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                return app.getAppViewModelStore();
            }
        }, app.getMusicPlayerViewModelFactory()).get(MusicPlayerViewModel.class);
        SpotifyCloneApplication.getInstance()
                .getMusicPlayerController()
                .attachViewModel(viewModel);
    }

    private void setupListeners() {
        miniPlayerPlayPauseButton.setOnClickListener(v -> viewModel.togglePlayPause());
        miniPlayerNextButton.setOnClickListener(v -> viewModel.playNext());
        miniPlayerPreviousButton.setOnClickListener(v -> viewModel.playPrevious());
        miniPlayer.setOnClickListener(v -> {
            Song currentSong = viewModel.getCurrentSong().getValue();
            if (currentSong != null) {
                PlayerBottomSheetFragment playerSheet = PlayerBottomSheetFragment.newInstance(currentSong);
                playerSheet.show(getSupportFragmentManager(), PlayerBottomSheetFragment.TAG);
            } else {
                Toast.makeText(MainActivity.this, "No song is currently playing", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeViewModel() {
        viewModel.getCurrentSong().observe(this, this::updateSongInfo);
        viewModel.getPlaybackState().observe(this, this::updatePlaybackState);
        viewModel.getCurrentDuration().observe(this, currentDuration -> {
            viewModel.getDuration().observe(this, duration -> {
                if (duration != null && duration > 0) {
                    int progress = (int) ((currentDuration * 100) / duration);
                    miniPlayerProgress.setProgress(progress);
                }
            });
        });
        viewModel.isAdPlaying().observe(this, isAdPlaying -> {
            if (isAdPlaying) {
                miniPlayerNextButton.setEnabled(false);
                miniPlayerPreviousButton.setEnabled(false);

                miniPlayerNextButton.setAlpha(0.5f);
                miniPlayerPreviousButton.setAlpha(0.5f);
            } else {
                miniPlayerNextButton.setEnabled(true);
                miniPlayerPreviousButton.setEnabled(true);

                miniPlayerNextButton.setAlpha(1f);
                miniPlayerPreviousButton.setAlpha(1f);
            }
        });
    }

    private void updatePlaybackState(PlaybackState state) {
        if (state == PlaybackState.PLAYING) {
            miniPlayerPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            miniPlayerPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private void updateSongInfo(Song song) {
        if (song != null) {
            miniPlayer.setVisibility(View.VISIBLE);
            miniPlayerSongName.setText(song.getTitle() != null ? song.getTitle() : "No Title");
            miniPlayerArtistName.setText(
                    song.getSingersString() != null && !song.getSingersString().isEmpty() ? song.getSingersString() : "Spotify"
            );
            if (song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
                if (viewModel.getPlayType().getValue() != MusicPlayerViewModel.PlaybackSourceType.LOCAL) {
                    Picasso.get().load(song.getImageUrl()).into(miniPlayerImage);
                }
                else {
                    Picasso.get().load(new File(song.getImageUrl())).into(miniPlayerImage);
                }
            } else {
                miniPlayerImage.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            }

            setupGradientBackground(miniPlayer, song.getImageUrl());
        }
    }

    private void setupGradientBackground(View view, String coverUrl) {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkMode = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES);
        int secondColor = isDarkMode ? Color.BLACK : Color.WHITE;

        DominantColorExtractor.getDominantColor(this, coverUrl, color -> {
            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{color, secondColor}
            );
            float cornerRadius = 16f;
            gradient.setCornerRadius(cornerRadius);

            view.findViewById(R.id.mini_player).setBackground(gradient);
        });
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            } else {
                retrieveFCMToken();
            }
        } else {
            retrieveFCMToken();
        }
    }

    private void retrieveFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Log.d("FCM", "FCM Token: " + token);
                    sendTokenToServer(token);
                });
    }

    private void sendTokenToServer(String token) {
        // Implement API call to send token to your backend
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("FCM", "Notification permission granted");
                retrieveFCMToken();
            } else {
                Log.w("FCM", "Notification permission denied");
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("PAUSE", "test");
        saveNow();
    }
    private void savePlayerState(){
        if (Boolean.FALSE.equals(viewModel.isAdPlaying().getValue())) {
            saveNow();
            return;
        }

        viewModel.setAdPlaying(false);
        viewModel.playNext();

        Observer<PlaybackState> playbackObserver = new Observer<PlaybackState>() {
            @Override
            public void onChanged(PlaybackState state) {
                if (state == PlaybackState.PLAYING) {
                    viewModel.getPlaybackState().removeObserver(this);
                    saveNow();
                }
            }
        };
        viewModel.getPlaybackState().observeForever(playbackObserver);
    }
    private void saveNow() {
        PlayerState currentPlayerState = new PlayerState(
                viewModel.getCurrentSong().getValue(),
                viewModel.getUpcomingSongs().getValue(),
                viewModel.getCurrentPlaylistId().getValue(),
                viewModel.getCurrentArtistId().getValue(),
                viewModel.getCurrentArtistId().getValue(),
                viewModel.getShuffleMode().getValue(),
                viewModel.getRepeatMode().getValue(),
                viewModel.getDuration().getValue(),
                viewModel.getCurrentDuration().getValue(),
                viewModel.getPlayName().getValue(),
                viewModel.getPlayType().getValue()
        );
        playerRepository.savePlayerState(currentPlayerState);
    }
}