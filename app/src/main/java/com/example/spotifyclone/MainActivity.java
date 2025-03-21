package com.example.spotifyclone;

import android.os.Bundle;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.features.home.ui.HomeFragment;
import com.example.spotifyclone.features.library.ui.LibraryFragment;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModel;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.ui.PlayerBottomSheetFragment;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.example.spotifyclone.features.premium.ui.PremiumFragment;
import com.example.spotifyclone.features.search.ui.SearchFragment;
import com.example.spotifyclone.shared.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import android.util.Log;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initUser();
        initUI();
        initViewModel();
        setupListeners();
        observeViewModel();
        setupNavigation();
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
        // Setup Bottom Navigation
        BottomNavigationView navView = findViewById(R.id.bottom_nav);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
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
             navController.navigate(R.id.profileFragment);
        } else if (id == R.id.nav_settings) {
            // TODO: Handle navigation to settings
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
                    song.getSingersString() != null && !song.getSingersString().isEmpty() ? song.getSingersString() : "Unknown Artist"
            );
            if (song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
                Picasso.get().load(song.getImageUrl()).into(miniPlayerImage);
            } else {
                miniPlayerImage.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            }
        }
    }
}