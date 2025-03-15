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

import com.example.spotifyclone.features.album.inter.AlbumMainCallbacks;
import com.example.spotifyclone.features.album.ui.AlbumFragment;
import com.example.spotifyclone.features.library.ui.LibraryFragment;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.player.ui.HomeFragment;
import com.example.spotifyclone.features.player.ui.PlayerBottomSheetFragment;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.example.spotifyclone.features.premium.ui.PremiumFragment;
import com.example.spotifyclone.features.search.model.SearchResult;
import com.example.spotifyclone.features.search.network.SearchService;
import com.example.spotifyclone.features.search.ui.SearchFragment;
import com.example.spotifyclone.features.genre.inter.GenreMainCallbacks;
import com.example.spotifyclone.features.search.viewmodel.SearchViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import android.util.Log;
import android.widget.EditText;

import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.album.ui.AlbumDetailFragment;
import com.example.spotifyclone.features.genre.model.Genre;
import com.example.spotifyclone.features.genre.ui.GenreDetailFragment;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements GenreMainCallbacks, AlbumMainCallbacks {
    private CardView miniPlayer;
    private ImageView miniPlayerImage;
    private TextView miniPlayerSongName, miniPlayerArtistName;
    private ImageButton miniPlayerPlayPauseButton, miniPlayerNextButton, miniPlayerPreviousButton;
    private MusicPlayerViewModel viewModel;
    private ProgressBar miniPlayerProgress;
    private EditText search_input; // genre-ids

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        initUI();
//        initViewModel();
//        setupListeners();
//        observeViewModel();
//
//        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
//        bottomNav.setOnNavigationItemSelectedListener(navListener);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.frame_container, new HomeFragment())
//                    .commit();
//        }

        //        // Create GenreFragment
//        setContentView(R.layout.activity_genrelayout);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.genre_list_holder, new GenreFragment())
//                .commit();
//          Create AlbumFragment
//        setContentView(R.layout.activity_albumlayout);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.album_list_holder, new AlbumFragment())
//                .commit();




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
            miniPlayerArtistName.setText(song.getSinger_ids() != null && song.getSinger_ids().length > 0 ? song.getSinger_ids()[0] : "Unknown Artist");
            if (song.getImage_url() != null && !song.getImage_url().isEmpty()) {
                Picasso.get().load(song.getImage_url()).into(miniPlayerImage);
            } else {
                miniPlayerImage.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            }
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (itemId == R.id.nav_search) {
            selectedFragment = new SearchFragment();
        } else if (itemId == R.id.nav_library) {
            selectedFragment = new LibraryFragment();
        } else if (itemId == R.id.nav_premium) {
            selectedFragment = new PremiumFragment();
        }
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, selectedFragment)
                    .commit();
        }
        return true;
    };

    // genre-ids branch
    @Override
    public void onMsgFromFragToMain(String sender, Genre genre) {
        if (sender.equals("GENRE_FRAGMENT")) {
            Log.d("MainActivity", "Genre selected: " + genre.getName());
            GenreDetailFragment detailFragment = GenreDetailFragment.newInstance(genre);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.genre_list_holder, detailFragment)
                    .addToBackStack(null) // add to backstack
                    .commit();
            // Hide search bar
            search_input = findViewById(R.id.search_input);
            search_input.setVisibility(View.GONE);

        } else if (sender.equals("GENRE DETAIL")) {
            getSupportFragmentManager().popBackStack(); // Quay lại Fragment trước đó
            search_input.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onMsgFromFragToMain(String sender, Album album) {
        if (sender.equals("ALBUM_FRAGMENT")) {
            Log.d("MainActivity", "Album selected: " + album.getTitle());

            AlbumDetailFragment detailFragment = AlbumDetailFragment.newInstance(album);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.album_list_holder, detailFragment)
                    .addToBackStack(null) //add to backstack
                    .commit();
            // Hide search bar
            search_input = findViewById(R.id.search_input);
            search_input.setVisibility(View.GONE);

        } else if (sender.equals("ALBUM DETAIL")) {
            Log.d("Main", "Have been step on there");
            getSupportFragmentManager().popBackStack(); // Quay lại Fragment trước đó
            search_input.setVisibility(View.VISIBLE);
        }
    }
}