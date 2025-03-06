package com.example.spotifyclone.features.player.ui;

import android.os.Bundle;
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

public class UpcomingSongsBottomSheetFragment extends BottomSheetDialogFragment {
    public static final String TAG = "UpcomingSongsBottomSheetFragment";
    private static final String ARG_UPCOMING_SONGS = "upcoming_songs";
    private static final String ARG_CURRENT_SONG = "current_song";
    private Song currentSong;
    private List<Song> songList;

    // View Model
    private MusicPlayerViewModel viewModel;
    private View rootView;

    // Adapter
    private UpcomingSongAdapter adapter;
    private OnSongActionListener songActionListener;
    private RecyclerView recyclerView;

    // Views
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

        // Get song list from arguments
        if (getArguments() != null) {
            songList = (List<Song>) getArguments().getSerializable(ARG_UPCOMING_SONGS);
            currentSong = (Song) getArguments().getSerializable(ARG_CURRENT_SONG);
        }

        if (songList == null) {
            songList = new ArrayList<>();
        }

        // Initialize ViewModel
        initViewModel();
    }

    public void setOnSongActionListener(OnSongActionListener listener) {
        this.songActionListener = listener;
    }


    public interface OnSongActionListener {
        void onSongSelected(Song song, int position);
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

        initUI();
        setupListeners();
        observeViewmodel();
        setupRecyclerView();

        // Set expanded state for the bottom sheet
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
            return; // Exit if recyclerView is null
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new UpcomingSongAdapter(songList, new UpcomingSongAdapter.OnSongClickListener() {
            @Override
            public void onSongClicked(Song song, int position) {
                if (songActionListener != null) {
                    songActionListener.onSongSelected(song, position);
                }
            }
        });

        recyclerView.setAdapter(adapter);
    }

    // Method to update the song list
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
            tvCurrentSongArtist.setText(currentSong.getAuthor_ids() != null && currentSong.getAuthor_ids().length > 0 ? currentSong.getAuthor_ids()[0] : "Unknown Artist");
            Picasso.get().load(currentSong.getImage_url()).into(ivCurrentSongImage);
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