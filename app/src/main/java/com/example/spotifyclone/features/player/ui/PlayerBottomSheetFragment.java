package com.example.spotifyclone.features.player.ui;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.spotifyclone.R;
import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.home.ui.HomeFragmentDirections;
import com.example.spotifyclone.features.player.model.playlist.RepeatMode;
import com.example.spotifyclone.features.player.model.playlist.ShuffleMode;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;

public class PlayerBottomSheetFragment extends BottomSheetDialogFragment {
    public static final String TAG = "PlayerBottomSheetFragment";
    private static final String ARG_SONG = "arg_song";

    private Song song;
    private List<Song> upcomingSongs = new ArrayList<>();
    private MusicPlayerViewModel viewModel;
    private ImageButton btnDown, btnOptions, btnAdd, btnShuffle, btnPrevious, btnPlay, btnNext, btnRepeat, btnMultiMedia, btnPlaylist, btnShare, btnShareLyrics, btnExpand;
    private TextView tvPlaylistInfo, tvSongTitle, tvArtistName, tvCurrentTime, tvTotalTime, tvLyricsTitle, tvLyricsContent, tvArtistIntro, tvArtistFullName, tvListenersCount, tvArtistDescription;
    private ImageView ivSongCover, ivArtistImage;
    private SeekBar progressBar;
    private boolean isUserSeeking = false;
    private View rootView;
    private UpcomingSongsBottomSheetFragment upcomingSongsBottomSheetFragment;
    private CardView artistCard;


    public static PlayerBottomSheetFragment newInstance(Song song) {
        PlayerBottomSheetFragment fragment = new PlayerBottomSheetFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SONG, song);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            song = getArguments().getParcelable(ARG_SONG);
        }
        initViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.bottom_sheet_fragment_player, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
        setupListeners();
        observeViewModel();

        // Set expanded state by default
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        //Set background gradient
        setupGradientBackground(view, song.getImageUrl());
    }

    private void initUI() {
        btnDown = rootView.findViewById(R.id.btnDown);
        tvPlaylistInfo = rootView.findViewById(R.id.tvPlaylistInfo);
        btnOptions = rootView.findViewById(R.id.btnOptions);
        ivSongCover = rootView.findViewById(R.id.ivSongCover);
        tvSongTitle = rootView.findViewById(R.id.tvSongTitle);
        tvArtistName = rootView.findViewById(R.id.tvArtistName);
        btnAdd = rootView.findViewById(R.id.btnAdd);
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        tvCurrentTime = rootView.findViewById(R.id.tvCurrentTime);
        tvTotalTime = rootView.findViewById(R.id.tvTotalTime);
        btnShuffle = rootView.findViewById(R.id.btnShuffle);
        btnPrevious = rootView.findViewById(R.id.btnPrevious);
        btnPlay = rootView.findViewById(R.id.btnPlay);
        btnNext = rootView.findViewById(R.id.btnNext);
        btnRepeat = rootView.findViewById(R.id.btnRepeat);
        btnMultiMedia = rootView.findViewById(R.id.btnMultimedia);
        btnPlaylist = rootView.findViewById(R.id.btnPlaylist);
        btnShare = rootView.findViewById(R.id.btnShare);
        btnShareLyrics = rootView.findViewById(R.id.btnShareLyrics);
        btnExpand = rootView.findViewById(R.id.btnExpand);
        tvLyricsTitle = rootView.findViewById(R.id.tvLyricsTitle);
        tvLyricsContent = rootView.findViewById(R.id.tvLyricsContent);

        // Artist Info Section
        artistCard = rootView.findViewById(R.id.artistCard);
        ivArtistImage = rootView.findViewById(R.id.ivArtistImage);
        tvArtistIntro = rootView.findViewById(R.id.tvArtistIntro);
        tvArtistFullName = rootView.findViewById(R.id.tvArtistFullName);
        tvListenersCount = rootView.findViewById(R.id.tvListenersCount);
        tvArtistDescription = rootView.findViewById(R.id.tvArtistDescription);
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
        btnDown.setOnClickListener(v -> dismiss());
        btnOptions.setOnClickListener(v -> { });
        btnAdd.setOnClickListener(v -> { });
        btnPlay.setOnClickListener(v -> {
            if (song != null) {
                viewModel.togglePlayPause(song);
            }
        });
        btnPrevious.setOnClickListener(v -> viewModel.playPrevious());
        btnNext.setOnClickListener(v -> viewModel.playNext());
        btnShuffle.setOnClickListener(v -> viewModel.cycleShuffleMode());
        btnRepeat.setOnClickListener(v -> viewModel.cycleRepeatMode());
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

        btnExpand.setOnClickListener(v -> {
            if (song != null) {
                LyricBottomSheetFragment lyricSheet = LyricBottomSheetFragment.newInstance(song);
                lyricSheet.show(getParentFragmentManager(), LyricBottomSheetFragment.TAG);
            }
        });

        btnPlaylist.setOnClickListener(v -> {
            showUpcomingSongsBottomSheet();
        });

        artistCard.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

            Bundle args = new Bundle();
            args.putString("artistId", song.getSingerIdAt(0));
            navController.navigate(R.id.artistFragment, args);

            dismiss();
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

        viewModel.getShuffleMode().observe(getViewLifecycleOwner(), shuffleMode -> {
            if (shuffleMode != null) {
                if (shuffleMode == ShuffleMode.SHUFFLE_OFF) {
                    btnShuffle.setImageResource(R.drawable.ic_shuffle_off);
                } else {
                    btnShuffle.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });

        viewModel.getRepeatMode().observe(getViewLifecycleOwner(), repeatMode -> {
            if (repeatMode != null) {
                if (repeatMode == RepeatMode.REPEAT_OFF) {
                    btnRepeat.setImageResource(R.drawable.ic_repeat_off);
                } else if (repeatMode == RepeatMode.REPEAT_INFINITE) {
                    btnRepeat.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });

        viewModel.getUpcomingSongs().observe(getViewLifecycleOwner(), this::updateUpcomingSongsList);

    }

    private void updateUI() {
        if (song != null) {
            tvSongTitle.setText(song.getTitle());
            tvArtistName.setText(song.getSingersString().toString());
            tvLyricsTitle.setText(song.getTitle());
            String lyrics = song.getLyrics();
            if (lyrics != null && lyrics.length() > 3) {
                lyrics = lyrics.substring(3);
            }
            tvLyricsContent.setText(lyrics.replace("\\n", "\n"));
            if (song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
                Picasso.get().load(song.getImageUrl()).into(ivSongCover);
            }
            tvPlaylistInfo.setText("Now Playing");
            tvCurrentTime.setText("0:00");
            tvTotalTime.setText("0:00");
            progressBar.setProgress(0);

            // Artist Info Section
            Log.d("Artist Image", song.getSingerImageUrlAt(0));
            Picasso.get().load(song.getSingerImageUrlAt(0)).into(ivArtistImage);
            tvArtistFullName.setText(song.getSingerNameAt(0));
            tvArtistDescription.setText(song.getSingerBioAt(0));
            tvListenersCount.setText(String.valueOf(song.getSingerFollowersAt(0)) + " người nghe hằng tháng");

            setupGradientBackground(rootView, song.getImageUrl());
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

    private void showUpcomingSongsBottomSheet() {
        upcomingSongsBottomSheetFragment = UpcomingSongsBottomSheetFragment.newInstance(upcomingSongs, song);
        upcomingSongsBottomSheetFragment.show(getParentFragmentManager(), UpcomingSongsBottomSheetFragment.TAG);
    }

    private void updateUpcomingSongsList(List<Song> newSongList) {
        if (upcomingSongsBottomSheetFragment != null && upcomingSongsBottomSheetFragment.isVisible()) {
            upcomingSongsBottomSheetFragment.updateSongList(newSongList);
        }

        if (newSongList != null && !newSongList.equals(upcomingSongs)) {
            upcomingSongs = new ArrayList<>(newSongList);
        }
    }

    private void setupGradientBackground(View view, String coverUrl) {
        int nightModeFlags = requireContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkMode = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES);
        int secondColor = isDarkMode ? Color.BLACK : Color.WHITE;

        DominantColorExtractor.getDominantColor(requireContext(), coverUrl, color -> {
            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{color, secondColor}
            );
            gradient.setCornerRadius(0f);

            view.findViewById(R.id.root_layout).setBackground(gradient);
            CardView lyricsCard = view.findViewById(R.id.lyricsCard);
            if (lyricsCard != null) {
                lyricsCard.setCardBackgroundColor(color);
            }        });
    }
}