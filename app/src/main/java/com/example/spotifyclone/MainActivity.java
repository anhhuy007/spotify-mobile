package com.example.spotifyclone;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.spotifyclone.features.home.ui.HomeFragment;
import com.example.spotifyclone.features.library.ui.LibraryFragment;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModel;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.ui.PlayerBottomSheetFragment;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.example.spotifyclone.features.premium.ui.PremiumFragment;
import com.example.spotifyclone.features.search.ui.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import android.widget.EditText;

public class MainActivity extends AppCompatActivity{
    private CardView miniPlayer;
    private ImageView miniPlayerImage;
    private TextView miniPlayerSongName, miniPlayerArtistName;
    private ImageButton miniPlayerPlayPauseButton, miniPlayerNextButton, miniPlayerPreviousButton;
    private MusicPlayerViewModel viewModel;
    private ProgressBar miniPlayerProgress;
    private EditText search_input; // genre-ids

    private AlbumViewModel albumViewModel;
    private NavController navController;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        //initViewModel();
        //setupListeners();
        //observeViewModel();
        setupNavigation();


        // Album
//        setContentView(R.layout.activity_albumlayout);
//        NavHostFragment navHostFragment =
//                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
//        NavController navController = navHostFragment.getNavController();





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
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(navView, navController);
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
//        viewModel.getCurrentSong().observe(this, this::updateSongInfo);
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
            miniPlayerArtistName.setText(song.getSingers() != null && song.getSingers().size() > 0 ? song.getSingers().get(0): "Unknown Artist");
            if (song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
                Picasso.get().load(song.getImageUrl()).into(miniPlayerImage);
            } else {
                miniPlayerImage.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            }
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
//        Fragment selectedFragment = null;
//        int itemId = item.getItemId();
//        if (itemId == R.id.nav_home) {
//            selectedFragment = new HomeFragment();
//        } else if (itemId == R.id.nav_search) {
//            selectedFragment = new SearchFragment();
//        } else if (itemId == R.id.nav_library) {
//            selectedFragment = new LibraryFragment();
//        } else if (itemId == R.id.nav_premium) {
//            selectedFragment = new PremiumFragment();
//        }
//        if (selectedFragment != null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.frame_container, selectedFragment)
//                    .commit();
//        }
        return true;
    };

    // genre-ids branch
//    @Override
//    public void onMsgFromFragToMain(String sender, Genre genre) {
//        if (sender.equals("GENRE_FRAGMENT")) {
//            Log.d("MainActivity", "Genre selected: " + genre.getName());
//            GenreDetailFragment detailFragment = GenreDetailFragment.newInstance(genre);
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.genre_list_holder, detailFragment)
//                    .addToBackStack(null) // add to backstack
//                    .commit();
//            // Hide search bar
//            search_input = findViewById(R.id.search_input);
//            search_input.setVisibility(View.GONE);
//
//        } else if (sender.equals("GENRE DETAIL")) {
//            getSupportFragmentManager().popBackStack(); // Quay lại Fragment trước đó
//            search_input.setVisibility(View.VISIBLE);
//        }
//
//    }

//    @Override
//    public void onMsgFromFragToMain(String sender, Album album) {
//        if (sender.equals("ALBUM_FRAGMENT")) {
//            Log.d("MainActivity", "Album selected: " + album.getTitle());
//
//            AlbumDetailFragment detailFragment = AlbumDetailFragment.newInstance(album);
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.album_list_holder, detailFragment)
//                    .addToBackStack(null) //add to backstack
//                    .commit();
//            // Hide search bar
//            search_input = findViewById(R.id.search_input);
//            search_input.setVisibility(View.GONE);
//
//        } else if (sender.equals("ALBUM DETAIL")) {
//            Log.d("Main", "Have been step on there");
//            getSupportFragmentManager().popBackStack(); // Quay lại Fragment trước đó
//            search_input.setVisibility(View.VISIBLE);
//        }
//    }
}