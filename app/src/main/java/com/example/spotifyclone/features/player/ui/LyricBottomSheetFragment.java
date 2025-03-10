package com.example.spotifyclone.features.player.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.spotifyclone.R;
import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class LyricBottomSheetFragment extends BottomSheetDialogFragment {
    public static final String TAG = "LyricBottomSheetFragment";
    private static final String ARG_SONG = "arg_song";
    private Song song;
    private MusicPlayerViewModel viewModel;
    private ImageView btnBack;
    private TextView tvTitle, tvArtist, tvLyrics, tvCurrentTime, tvTotalTime;
    private SeekBar progressBar;
    private ImageButton btnPlay;
    private boolean isUserSeeking = false;

    public static LyricBottomSheetFragment newInstance(Song song) {
        LyricBottomSheetFragment fragment = new LyricBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SONG, song);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        SpotifyCloneApplication app = SpotifyCloneApplication.getInstance();
        viewModel = new ViewModelProvider(new ViewModelStoreOwner() {
            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                return app.getAppViewModelStore();
            }
        }, app.getMusicPlayerViewModelFactory()).get(MusicPlayerViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_fragment_lyric, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            song = (Song) getArguments().getSerializable(ARG_SONG);
        }

        initUI(view);
        setupListeners();
        observeViewModel();
        updateUI();
    }

    private void initUI(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvArtist = view.findViewById(R.id.tvArtist);
        tvLyrics = view.findViewById(R.id.tvLyrics);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        tvTotalTime = view.findViewById(R.id.tvTotalTime);
        btnPlay = view.findViewById(R.id.btnPlay);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> dismiss());
        btnPlay.setOnClickListener(v -> {
            if (song != null) {
                viewModel.togglePlayPause(song);
            }
        });
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Long totalDuration = viewModel.getDuration().getValue();
                    if (totalDuration != null && totalDuration > 0) {
                        int newPosition = (int) ((progress / 100.0) * totalDuration);
                        tvCurrentTime.setText(formatTime(newPosition));
                    }
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Long totalDuration = viewModel.getDuration().getValue();
                if (totalDuration != null && totalDuration > 0) {
                    int progressPercentage = seekBar.getProgress();
                    int newPosition = (int) ((progressPercentage / 100.0) * totalDuration);
                    viewModel.seekTo(newPosition);
                }
                isUserSeeking = false;
            }
        });
    }

    private void observeViewModel() {
        viewModel.getCurrentSong().observe(getViewLifecycleOwner(), currentSong -> {
            if (currentSong != null) {
                song = currentSong;
                updateUI();
            }
        });
        viewModel.getPlaybackState().observe(getViewLifecycleOwner(), playbackState -> {
            if (playbackState != null) {
                updatePlayButton(playbackState == PlaybackState.PLAYING);
            }
        });
        viewModel.getCurrentDuration().observe(getViewLifecycleOwner(), currentDuration -> {
            viewModel.getDuration().observe(getViewLifecycleOwner(), duration -> {
                if (!isUserSeeking) {
                    updateProgress(currentDuration, duration);
                }
            });
        });
    }

    private void updateUI() {
        if (song != null) {
            tvTitle.setText(song.getTitle());
            tvArtist.setText(song.getAuthor_ids().toString());
            tvLyrics.setText(song.getLyrics());
            tvCurrentTime.setText("0:00");
            tvTotalTime.setText("0:00");
            progressBar.setProgress(0);
        }
    }

    private void updatePlayButton(boolean isPlaying) {
        if (isPlaying) {
            btnPlay.setImageResource(R.drawable.ic_pause_circle);
            btnPlay.setTag("pause");
        } else {
            btnPlay.setImageResource(R.drawable.ic_play_circle);
            btnPlay.setTag("play");
        }
    }

    private void updateProgress(long currentPosition, long duration) {
        tvCurrentTime.setText(formatTime(currentPosition));
        tvTotalTime.setText(formatTime(duration));
        int progressValue = duration > 0 ? (int) ((currentPosition * 100) / duration) : 0;
        progressBar.setProgress(progressValue);
    }

    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
