package com.example.spotifyclone.features.history;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.spotifyclone.R;
import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.download.LocalSongAdapter;
import com.example.spotifyclone.features.download.LocalSongListFragment;
import com.example.spotifyclone.features.download.SongDatabaseHelper;
import com.example.spotifyclone.features.player.model.audio.MusicPlayerController;
import com.example.spotifyclone.features.player.model.playlist.ShuffleMode;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.player.network.SongService;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistorySongListFragment extends Fragment implements HistorySongAdapter.onSongClickListener {
    private MusicPlayerViewModel musicPlayerViewModel;
    private HistorySongAdapter historySongAdapter;
    private List<HistorySong> historySongs;
    private List<Song> songs;
    //UI elements
    private RecyclerView historySongRecyclerView;
    private Toolbar toolbar;
    private ImageButton play_button, shuffle_button;
    private NestedScrollView nestedScrollView;
    private HistorySongDatabaseHelper historySongDatabaseHelper;
    private SongService songService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_song_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initData(view);
        setupViewModel();
        setupRecyclerView(view);
        setupToolbar((AppCompatActivity) requireActivity());
        setupListeners();
        setupScrollListener();
        setupGradientBackground(view);
    }

    private void initData(View view) {
        this.historySongDatabaseHelper = HistorySongDatabaseHelper.getInstance(requireContext());
        this.historySongs = historySongDatabaseHelper.getHistorySongs();
        List<String> song_ids = new ArrayList<>();
        for (HistorySong historySong : historySongs) {
            song_ids.add(historySong.getSongId());
        }
        this.songService = RetrofitClient.getClient(requireContext()).create(SongService.class);
        fetchListSongs(song_ids, songs -> {
            this.songs = songs;
            historySongAdapter.setData(historySongs, songs);
        });
    }

    private void initViews(View view){
        play_button = view.findViewById(R.id.play_button);
        shuffle_button = view.findViewById(R.id.shuffle_button);
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
            if (playbackState == PlaybackState.PLAYING && musicPlayerViewModel.getPlayType().getValue() == MusicPlayerViewModel.PlaybackSourceType.HISTORY) {
                play_button.setImageResource(R.drawable.ic_pause_circle);
            } else {
                play_button.setImageResource(R.drawable.ic_play_circle);
            }
        });

        musicPlayerViewModel.getShuffleMode().observe(getViewLifecycleOwner(), shuffleMode -> {
            if (shuffleMode == ShuffleMode.SHUFFLE_OFF) {
                shuffle_button.setImageResource(R.drawable.ic_shuffle_off);
            } else {
                shuffle_button.setImageResource(R.drawable.ic_shuffle_on);
            }
        });

    }
    private void setupRecyclerView(View view) {
        historySongRecyclerView = view.findViewById(R.id.song_recyclerview);
        historySongRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historySongAdapter = new HistorySongAdapter(new ArrayList<>(), new ArrayList<>(),this);
        historySongRecyclerView.setAdapter(historySongAdapter);
    }
    private void setupToolbar(AppCompatActivity activity) {
        activity.setSupportActionBar(toolbar);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setTitle("Những bài hát đã nghe");
        }

        activity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == android.R.id.home) {
                    NavHostFragment.findNavController(HistorySongListFragment.this).navigateUp();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void setupListeners() {
        play_button.setOnClickListener(v -> {
            if (historySongs != null && !historySongs.isEmpty()) {
                musicPlayerViewModel.togglePlayPauseHistory(songs);
            }
        });

        shuffle_button.setOnClickListener(v -> {
            musicPlayerViewModel.cycleShuffleMode();
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

    private void fetchListSongs(List<String> song_ids, MusicPlayerController.FetchSongsCallback callback) {
        if (song_ids == null || song_ids.isEmpty()) {
            callback.onSongsFetched(new ArrayList<>());
            return;
        }
        List<Song> result = new ArrayList<>();
        AtomicInteger pendingCalls = new AtomicInteger(song_ids.size());

        for (String id : song_ids) {
            Call<APIResponse<Song>> call = songService.getSongById(id);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<APIResponse<Song>> call, @NonNull Response<APIResponse<Song>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Song song = response.body().getData();
                        synchronized (result) {
                            result.add(song);
                        }
                    } else {
                        Log.e("DEBUG", "onFailure: " + response.message());
                    }

                    if (pendingCalls.decrementAndGet() == 0) {
                        callback.onSongsFetched(result);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<APIResponse<Song>> call, @NonNull Throwable t) {
                    if (pendingCalls.decrementAndGet() == 0) {
                        callback.onSongsFetched(result);
                    }
                }
            });
        }

    }
    @Override
    public void onSongClick(Song song) {
        if (historySongs == null || historySongs.isEmpty()) {
            return;
        }
        musicPlayerViewModel.playHistorySongs(songs, song);
    }
}