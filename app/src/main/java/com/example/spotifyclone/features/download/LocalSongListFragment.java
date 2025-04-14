package com.example.spotifyclone.features.download;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.spotifyclone.R;
import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;
import java.util.ArrayList;
import java.util.List;

public class LocalSongListFragment extends Fragment implements LocalSongAdapter.OnSongClickListener {
    private MusicPlayerViewModel musicPlayerViewModel;
    private LocalSongAdapter localSongAdapter;
    private List<Song> localSongs;
    //UI elements
    private RecyclerView localSongRecyclerView;
    private Toolbar toolbar;
    private ImageButton play_button;
    private NestedScrollView nestedScrollView;
    private SongDatabaseHelper songDatabaseHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_song_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initViews(view);
        setupViewModel();
//        setupUI();
        setupRecyclerView(view);
        setupToolbar((AppCompatActivity) requireActivity());
        setupListeners();
        setupScrollListener();
        setupGradientBackground(view);
    }

    private void initData() {
        songDatabaseHelper = new SongDatabaseHelper(requireContext());
        if(songDatabaseHelper.getAllSavedSongs() != null) {
            localSongs = songDatabaseHelper.getAllSavedSongs();
        } else {
            localSongs = new ArrayList<>();
        }
    }

    private void initViews(View view){
        play_button = view.findViewById(R.id.play_button);
        toolbar = view.findViewById(R.id.toolbar);
        nestedScrollView = view.findViewById(R.id.nestedScrollview);
    }

    private void setupViewModel(){
        SpotifyCloneApplication app = SpotifyCloneApplication.getInstance();
        musicPlayerViewModel = new ViewModelProvider(new ViewModelStoreOwner() {
            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                return app.getAppViewModelStore();
            }
        }, app.getMusicPlayerViewModelFactory()).get(MusicPlayerViewModel.class);

        musicPlayerViewModel.getPlaybackState().observe(getViewLifecycleOwner(), playbackState -> {
            if (playbackState == PlaybackState.PLAYING && musicPlayerViewModel.getPlayType().getValue() == MusicPlayerViewModel.PlaybackSourceType.LOCAL) {
                updatePlayButton(true);
            } else {
                updatePlayButton(false);
            }
        });
    }

    private void updatePlayButton(boolean isPlaying) {
        if (isPlaying) {
            play_button.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
        } else {
            play_button.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
        }
    }
    private  void setupRecyclerView(View view){
        localSongRecyclerView=view.findViewById(R.id.song_recyclerview);
        localSongRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        localSongAdapter = new LocalSongAdapter(new ArrayList<>(), this);
        localSongRecyclerView.setAdapter(localSongAdapter);
        localSongAdapter.setSongs(localSongs);
    }

    private void setupToolbar(AppCompatActivity activity) {
        activity.setSupportActionBar(toolbar);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setTitle("Nhạc ngoại tuyến");
        }

        activity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == android.R.id.home) {
                    NavHostFragment.findNavController(LocalSongListFragment.this).navigateUp();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }


    private void setupListeners() {
        play_button.setOnClickListener(v-> {
            if (localSongs != null
                    && !localSongs.isEmpty()) {
                musicPlayerViewModel.togglePlayPauseLocal();
            }
        });
    }

    private void setupScrollListener() {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    DominantColorExtractor.getDominantColor(requireContext(), String.valueOf(R.raw.local), color -> {
                        toolbar.setBackgroundColor(color);
                    });
                } else if (scrollY == 0) {
                    toolbar.setBackground(new ColorDrawable(Color.TRANSPARENT));
                }
            }
        });
    }

    private void setupGradientBackground(View view) {
        int nightModeFlags = requireContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkMode = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES);
        int secondColor = isDarkMode ? Color.BLACK : Color.WHITE;

        DominantColorExtractor.getDominantColor(requireContext(), String.valueOf(R.raw.local), color -> {
            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{color, secondColor}
            );
            gradient.setCornerRadius(0f);

            view.findViewById(R.id.imageConstraintLayout).setBackground(gradient);
        });
    }
    @Override
    public void onSongClick(Song song) {
        if(localSongs == null || localSongs.isEmpty()) {
            return;
        }
        musicPlayerViewModel.playLocalSongs(song);
    }
}
