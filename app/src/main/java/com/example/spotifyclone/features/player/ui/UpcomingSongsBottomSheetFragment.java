package com.example.spotifyclone.features.player.ui;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.player.adapter.UpcomingSongAdapter;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UpcomingSongsBottomSheetFragment extends BottomSheetDialogFragment implements UpcomingSongAdapter.OnSongClickListener {
    public static final String TAG = "UpcomingSongsBottomSheetFragment";
    private static final String ARG_UPCOMING_SONGS = "upcoming_songs";
    private static final String ARG_CURRENT_SONG = "current_song";

    private Song currentSong;
    private List<Song> songList;
    private MusicPlayerViewModel viewModel;
    private View rootView;
    private UpcomingSongAdapter adapter;
    private RecyclerView recyclerView;

    private TextView tvQueueTitle, tvCurrentSongTitle, tvCurrentSongArtist;
    private ImageButton btnPlay;
    private ImageView ivCurrentSongImage;

    public static UpcomingSongsBottomSheetFragment newInstance(List<Song> upcomingSongs, Song currentSong) {
        UpcomingSongsBottomSheetFragment fragment = new UpcomingSongsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_UPCOMING_SONGS, new ArrayList<>(upcomingSongs));
        args.putSerializable(ARG_CURRENT_SONG, currentSong);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            songList = (List<Song>) getArguments().getSerializable(ARG_UPCOMING_SONGS);
            currentSong = (Song) getArguments().getSerializable(ARG_CURRENT_SONG);
        }

        if (songList == null) {
            songList = new ArrayList<>();
        }

        initViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.bottom_sheet_fragment_upcoming_songs, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;
        int initialHeight = (int) (screenHeight * 0.8);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = initialHeight;
        view.setLayoutParams(layoutParams);

        initUI();
        setupListeners();
        observeViewmodel();
        setupRecyclerView();

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        behavior.setPeekHeight(initialHeight);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setFitToContents(true);
    }

    private void initUI() {
        recyclerView = rootView.findViewById(R.id.recyclerUpcomingSongs);
        tvQueueTitle = rootView.findViewById(R.id.tvQueueLabel);
        tvCurrentSongTitle = rootView.findViewById(R.id.tvCurrentSongTitle);
        tvCurrentSongArtist = rootView.findViewById(R.id.tvCurrentArtist);
        ivCurrentSongImage = rootView.findViewById(R.id.imgCurrentSong);
        btnPlay = rootView.findViewById(R.id.btnPlay);
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

    private void setupRecyclerView() {
        if (recyclerView == null) {
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new UpcomingSongAdapter(songList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSongClicked(Song song, int position) {
        if (viewModel != null) {
            viewModel.prioritizeSong(song);
        }
    }

    public void updateSongList(List<Song> newSongList) {
        if (adapter != null) {
            adapter.setSongs(newSongList);
        }
    }

    public void setupListeners() {
        btnPlay.setOnClickListener(v -> {
            if (currentSong != null) {
                viewModel.togglePlayPause(currentSong);
            }
        });
    }

    private void observeViewmodel() {
        viewModel.getCurrentSong().observe(this, song -> {
            currentSong = song;
            updateUI();
        });

        viewModel.getPlaybackState().observe(this, playbackState -> {
            updatePlayButton(playbackState == PlaybackState.PLAYING);
        });

        viewModel.getUpcomingSongs().observe(this, this::updateSongList);
    }

    private void updateUI() {
        if (currentSong != null) {
            tvCurrentSongTitle.setText(currentSong.getTitle());
            tvCurrentSongArtist.setText(
                    currentSong.getSingersString() != null && !currentSong.getSingersString().isEmpty() ? currentSong.getSingersString() : "Unknown Artist"
            );
            Picasso.get().load(currentSong.getImageUrl()).into(ivCurrentSongImage);
        }
    }

    private void updatePlayButton(boolean isPlaying) {
        if (isPlaying) {
            btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            btnPlay.setImageResource(android.R.drawable.ic_media_play);
        }
    }
}
