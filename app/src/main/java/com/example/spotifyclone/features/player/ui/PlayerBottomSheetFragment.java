package com.example.spotifyclone.features.player.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.features.album.ui.AlbumBottomSheet;
import com.example.spotifyclone.features.player.model.playlist.RepeatMode;
import com.example.spotifyclone.features.player.model.playlist.ShuffleMode;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.example.spotifyclone.shared.model.User;
import com.example.spotifyclone.features.playlist.ui.NewPlaylistBottomSheet;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import java.io.File;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class PlayerBottomSheetFragment extends BottomSheetDialogFragment {
    public static final String TAG = "PlayerBottomSheetFragment";
    private static final String ARG_SONG = "arg_song";

    private Song song;
    private List<Song> upcomingSongs = new ArrayList<>();
    private MusicPlayerViewModel viewModel;
    private ImageButton btnDown, btnOptions, btnAdd, btnShuffle, btnPrevious, btnPlay, btnNext, btnRepeat, btnMultiMedia, btnPlaylist, btnShare, btnShareLyrics, btnExpand;
    private TextView tvPlayType, tvPlayName, tvSongTitle, tvArtistName, tvCurrentTime, tvTotalTime, tvLyricsTitle, tvLyricsContent, tvArtistIntro, tvArtistFullName, tvListenersCount, tvArtistDescription;
    private ImageView ivSongCover, ivArtistImage;
    private SeekBar progressBar;
    private boolean isUserSeeking = false;
    private View rootView;
    private UpcomingSongsBottomSheetFragment upcomingSongsBottomSheetFragment;
    private CardView artistCard, lyricsCard;
    private User currentUser;


    public static PlayerBottomSheetFragment newInstance(Song song) {
        PlayerBottomSheetFragment fragment = new PlayerBottomSheetFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SONG, song);
        fragment.setArguments(args);
        return fragment;
    }

    private void initUser() {
        AuthRepository authRepository = new AuthRepository(getApplicationContext());
        currentUser = authRepository.getUser();
    }

    private Context getApplicationContext() {
        return SpotifyCloneApplication.getInstance().getApplicationContext();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            song = getArguments().getParcelable(ARG_SONG);
        }
        initViewModel();
        initUser();
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
        tvPlayName = rootView.findViewById(R.id.tvPlayName);
        tvPlayType = rootView.findViewById(R.id.tvPlayType);
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
        lyricsCard = rootView.findViewById(R.id.lyricsCard);

        // Artist Info Section
        artistCard = rootView.findViewById(R.id.artistCard);
        ivArtistImage = rootView.findViewById(R.id.ivArtistImage);
        tvArtistIntro = rootView.findViewById(R.id.tvArtistIntro);
        tvArtistFullName = rootView.findViewById(R.id.tvArtistFullName);
        tvListenersCount = rootView.findViewById(R.id.tvListenersCount);
        tvArtistDescription = rootView.findViewById(R.id.tvArtistDescription);

//        if(currentUser != null && !currentUser.isPremium()) {
//            btnPlaylist.setAlpha(0.5f);
//            btnPlaylist.setEnabled(false);
//        } else {
//            btnShareLyrics.setVisibility(View.GONE);
//        }
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
        btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        AlbumBottomSheet albumBottomSheet = AlbumBottomSheet.newInstance(song.getId(),song.getImageUrl(), song.getTitle(), song.getAuthorNames());
                        albumBottomSheet.show(fragmentManager, "AlbumBottomSheet");
                    } catch (Exception e) {
                        Log.e("", "Error showing AlbumBottomSheet", e);
                    }
                });

            }
        });

        btnAdd.setOnClickListener(v -> { });
        btnPlay.setOnClickListener(v -> {
            if (song != null) {
                viewModel.togglePlayPause();
            }
        });
        btnPrevious.setOnClickListener(v -> viewModel.playPrevious());
        btnNext.setOnClickListener(v -> viewModel.playNext());
        btnShuffle.setOnClickListener(v -> viewModel.cycleShuffleMode());
        btnRepeat.setOnClickListener(v -> viewModel.cycleRepeatMode());
        btnMultiMedia.setOnClickListener(v -> {
            SleepTimerDialog sleepTimerDialog = SleepTimerDialog.newInstance();
            sleepTimerDialog.show(getParentFragmentManager(), "SleepTimerDialog");
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
            Bundle args = new Bundle();
            args.putString("ARTIST_ID", song.getSingerIdAt(0));

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
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
        // Set up observers for play type changes
        viewModel.getPlayType().observe(getViewLifecycleOwner(), type -> {
            Log.d(TAG, "Play type: " + type);
            updatePlayTypeAndVisibility(type, viewModel.isAdPlaying().getValue());
        });

        // Set up observers for play name changes
        viewModel.getPlayName().observe(getViewLifecycleOwner(), name -> {
            updatePlayNameVisibility(name, viewModel.getPlayType().getValue(), viewModel.isAdPlaying().getValue());
        });

        // Set up observers for ad playing status
        viewModel.isAdPlaying().observe(getViewLifecycleOwner(), isAdPlaying -> {
            updateUIForAdPlayback(isAdPlaying, viewModel.getPlayType().getValue());
        });
    }

    private void updatePlayTypeAndVisibility(MusicPlayerViewModel.PlaybackSourceType type, Boolean isAdPlaying) {
        if (Boolean.TRUE.equals(isAdPlaying)) {
            return; // Let the ad observer handle this case
        }

        String playTypeText;

        if (type == MusicPlayerViewModel.PlaybackSourceType.ALBUM) {
            playTypeText = "ĐANG PHÁT TỪ ALBUM";
            artistCard.setVisibility(View.VISIBLE);
        } else if (type == MusicPlayerViewModel.PlaybackSourceType.RANDOM) {
            playTypeText = "ĐANG PHÁT CÁC BÀI HÁT ĐƯỢC ĐỀ XUẤT CHO BẠN";
            tvPlayName.setVisibility(View.GONE);
            artistCard.setVisibility(View.VISIBLE);
        } else if (type == MusicPlayerViewModel.PlaybackSourceType.ARTIST) {
            playTypeText = "ĐANG PHÁT TỪ NGHỆ SĨ";
            artistCard.setVisibility(View.VISIBLE);
        } else if (type == MusicPlayerViewModel.PlaybackSourceType.PLAYLIST) {
            playTypeText = "ĐANG PHÁT TỪ DANH SÁCH PHÁT";
            artistCard.setVisibility(View.VISIBLE);
        } else if (type == MusicPlayerViewModel.PlaybackSourceType.LOCAL) {
            playTypeText = "ĐANG PHÁT NHẠC NGOẠI TUYẾN";
            tvPlayName.setVisibility(View.GONE);
            artistCard.setVisibility(View.GONE);
        } else if (type == MusicPlayerViewModel.PlaybackSourceType.SEARCH) {
            playTypeText = "ĐANG PHÁT TỪ TÌM KIẾM";
            artistCard.setVisibility(View.VISIBLE);
        } else {
            playTypeText = "ĐANG PHÁT";
            artistCard.setVisibility(View.VISIBLE);
        }

        tvPlayType.setText(playTypeText);
    }

    private void updatePlayNameVisibility(String name, MusicPlayerViewModel.PlaybackSourceType type, Boolean isAdPlaying) {
        if (Boolean.TRUE.equals(isAdPlaying)) {
            tvPlayName.setVisibility(View.GONE);
            return;
        }

        // Always hide for RANDOM and LOCAL types
        if (type == MusicPlayerViewModel.PlaybackSourceType.RANDOM ||
                type == MusicPlayerViewModel.PlaybackSourceType.LOCAL) {
            tvPlayName.setVisibility(View.GONE);
            return;
        }

        // For other types, show only if name exists
        if (name != null && !name.isEmpty()) {
            tvPlayName.setText(name);
            tvPlayName.setVisibility(View.VISIBLE);
        } else {
            tvPlayName.setVisibility(View.GONE);
        }
    }

    private void updateUIForAdPlayback(boolean isAdPlaying, MusicPlayerViewModel.PlaybackSourceType type) {
        if (isAdPlaying) {
            // Ad is playing - disable controls and update UI
            tvPlayType.setText("ĐANG PHÁT QUẢNG CÁO");

            // Disable controls
            btnPrevious.setEnabled(false);
            btnNext.setEnabled(false);
            btnShuffle.setEnabled(false);
            btnRepeat.setEnabled(false);
            progressBar.setEnabled(false);

            // Adjust visuals
            btnPrevious.setAlpha(0.5f);
            btnNext.setAlpha(0.5f);
            btnShuffle.setAlpha(0.5f);
            btnRepeat.setAlpha(0.5f);

            // Hide elements
            artistCard.setVisibility(View.GONE);
            lyricsCard.setVisibility(View.GONE);
            tvPlayName.setVisibility(View.GONE);
        } else {
            // Regular playback - enable controls
            btnPrevious.setEnabled(true);
            btnNext.setEnabled(true);
            btnShuffle.setEnabled(true);
            btnRepeat.setEnabled(true);
            progressBar.setEnabled(true);

            // Restore visuals
            btnPrevious.setAlpha(1.0f);
            btnNext.setAlpha(1.0f);
            btnShuffle.setAlpha(1.0f);
            btnRepeat.setAlpha(1.0f);

            // Show elements (visibility depends on current play type)
            lyricsCard.setVisibility(View.VISIBLE);

            // Update UI based on current play type
            updatePlayTypeAndVisibility(type, false);

            // Update play name visibility
            updatePlayNameVisibility(viewModel.getPlayName().getValue(), type, false);
        }
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
            if(lyrics!=null){
                tvLyricsContent.setText(lyrics.replace("\\n", "\n"));

            }
            if (song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
                if(song.getImageUrl().contains("http")) {
                    Picasso.get().load(song.getImageUrl()).placeholder(R.drawable.progress_drawable).into(ivSongCover);
                } else {
                    Picasso.get().load(new File(song.getImageUrl())).placeholder(R.drawable.progress_drawable).into(ivSongCover);
                }
            }
            tvCurrentTime.setText("0:00");
            tvTotalTime.setText("0:00");
            progressBar.setProgress(0);

            // Artist Info Section
            if(song.getSingerImageUrlAt(0) != null && !song.getSingerImageUrlAt(0).isEmpty()) {
                Picasso.get().load(song.getSingerImageUrlAt(0)).placeholder(R.drawable.progress_drawable).into(ivArtistImage);
            }
//            Log.d("Artist Image", song.getSingerImageUrlAt(0));
            if(viewModel.getPlayType().getValue() == MusicPlayerViewModel.PlaybackSourceType.LOCAL) {
                artistCard.setVisibility(View.GONE);
            } else {
                if(song.getSingerImageUrlAt(0) != null && !song.getSingerImageUrlAt(0).isEmpty())  Picasso.get().load(song.getSingerImageUrlAt(0)).placeholder(R.drawable.progress_drawable).into(ivArtistImage);
            }
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
        if (newSongList == null) return;

        List<Song> safeNewList;
        try {
            safeNewList = new ArrayList<>(newSongList);
        } catch (ConcurrentModificationException e) {
            Log.e(TAG, "ConcurrentModificationException while copying newSongList", e);
            return;
        }

        if (upcomingSongsBottomSheetFragment != null && upcomingSongsBottomSheetFragment.isVisible()) {
            upcomingSongsBottomSheetFragment.updateSongList(safeNewList);
        }

        if (!safeNewList.equals(upcomingSongs)) {
            upcomingSongs = new ArrayList<>(safeNewList);
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
            if (lyricsCard != null) {
                lyricsCard.setCardBackgroundColor(color);
            }        });
    }
}