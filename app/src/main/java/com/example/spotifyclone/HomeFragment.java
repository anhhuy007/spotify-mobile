package com.example.spotifyclone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.player.network.SongService;
import com.example.spotifyclone.features.player.ui.SongAdapter;
import com.example.spotifyclone.features.player.ui.SongItemType;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.example.spotifyclone.SpotifyCloneApplication;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements SongAdapter.OnSongClickListener {
    private RecyclerView topSongsRecyclerView, popularSongsRecyclerView;
    private SongAdapter topSongsAdapter, popularSongsAdapter;
    private MusicPlayerViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initUI(view);
        setupViewModel();
        observeViewModel();
        return view;
    }

    private void initUI(View view) {
        topSongsRecyclerView = view.findViewById(R.id.rv_top_songs);
        topSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        topSongsAdapter = new SongAdapter(new ArrayList<>(), SongItemType.VERTICAL, this);
        topSongsRecyclerView.setAdapter(topSongsAdapter);

        popularSongsRecyclerView = view.findViewById(R.id.rv_popular_songs);
        popularSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularSongsAdapter = new SongAdapter(new ArrayList<>(), SongItemType.HORIZONTAL, this);
        popularSongsRecyclerView.setAdapter(popularSongsAdapter);
    }

    private void setupViewModel() {
        SpotifyCloneApplication app = SpotifyCloneApplication.getInstance();
        viewModel = new ViewModelProvider(new ViewModelStoreOwner() {
            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                return app.getAppViewModelStore();
            }
        }, app.getMusicPlayerViewModelFactory()).get(MusicPlayerViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getTopSongs().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null && !songs.isEmpty()) {
                topSongsAdapter.setSongs(songs);
                popularSongsAdapter.setSongs(songs);
            }
        });
        viewModel.getPlaybackState().observe(getViewLifecycleOwner(), playbackState -> {
            if (playbackState != null) {
                viewModel.getCurrentSong().observe(getViewLifecycleOwner(), song -> {
                    if (song != null) {
                        topSongsAdapter.updateUI(playbackState, song);
                        popularSongsAdapter.updateUI(playbackState, song);
                    }
                });
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorMessage ->
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onSongClick(Song song) {
        viewModel.playSong(song);
    }

    @Override
    public void onPlayClick(Song song) {
        viewModel.togglePlayPause(song);
    }
}
