package com.example.spotifyclone.features.album.ui;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.album.adapter.AlbumAdapter;
import com.example.spotifyclone.features.album.adapter.AlbumSongAdapter;
import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModel;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModelFactory;
import com.example.spotifyclone.features.player.model.playlist.ShuffleMode;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.example.spotifyclone.features.playlist.ui.PlaylistDetailFragmentDirections;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AlbumDetailFragment extends Fragment implements AlbumSongAdapter.OnItemClickListener {
    private ImageView albumImage;
    private TextView artist_name;
    private RecyclerView recyclerView, artist_album, related_album;
    private AlbumSongAdapter songAdapter;
    private ImageView artist_image;
    private TextView artist_name2;
    private TextView artist_album_text;
    private AlbumAdapter artist_albumAdapter;
    private MusicPlayerViewModel viewModel;
    private ImageButton playButton, shuffleButton;
    private AlbumAdapter related_albumAdapter;
    private Toolbar toolbar;
    private NestedScrollView nestedScrollView;
    private String coverUrl;
    private String albumTitle;
    private String albumId;
    private String dayCreate;
    private List<String> artistNames;
    private String artistUrl;
    private List<Song> albumSong;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_album_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AlbumDetailFragmentArgs args=AlbumDetailFragmentArgs.fromBundle(getArguments());
        if (args != null) {
            coverUrl=args.getCoverUrl();
            albumTitle=args.getTitle();
            albumId=args.getId();
            artistNames=Arrays.asList(args.getArtist());
            artistUrl=args.getArtistUrl();
        } else {
            Log.e("AlbumDetailFragment", "Arguments is null");
            NavHostFragment.findNavController(this).navigateUp();
            return;
        }

        initViews(view);
        setupUI();
        setupViewModel();
        setupRecyclerView(view);
        setupToolbar((AppCompatActivity) requireActivity());
        setupListeners();
        setupScrollListener();
        setupGradientBackground(view);
    }

    private void setupListeners() {
        playButton.setOnClickListener(v-> {
            viewModel.togglePlayPause(albumId, albumTitle, MusicPlayerViewModel.PlaybackSourceType.ALBUM);
        });
        shuffleButton.setOnClickListener(v -> {
            viewModel.cycleShuffleMode();
        });
    }

    private void initViews(View view) {
        albumImage = view.findViewById(R.id.album_image);
        artist_name = view.findViewById(R.id.artist_name);
        toolbar = view.findViewById(R.id.toolbar);

        // Artist info
        artist_name2 = view.findViewById(R.id.artist);
        artist_image = view.findViewById(R.id.artist_image);
        artist_album_text = view.findViewById(R.id.artist_album_text);

        nestedScrollView = view.findViewById(R.id.nestedScrollview);

        playButton = view.findViewById(R.id.play_button);
        shuffleButton = view.findViewById(R.id.shuffle_button);
    }
    @SuppressLint("SetTextI18n")
    private void setupUI() {
        // Load album image
        artist_name.setText(String.join(" ,", artistNames));Glide.with(requireContext())
                .load(coverUrl)
                .into(albumImage);

        // Artist info
        artist_name2.setText(String.join(" ,", artistNames));
        Glide.with(requireContext())
                .load(artistUrl)
                .into(artist_image);
        artist_album_text.setText("Thêm nữa từ " + String.join(" ,", artistNames));


    }

    private void setupToolbar(AppCompatActivity activity) {
        activity.setSupportActionBar(toolbar);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setTitle(albumTitle);
        }

        // Use MenuProvider for handling toolbar menu events
        activity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == android.R.id.home) {
                    NavHostFragment.findNavController(AlbumDetailFragment.this).navigateUp();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void setupRecyclerView(View view) {
        // Song of album
        recyclerView = view.findViewById(R.id.song_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        songAdapter = new AlbumSongAdapter(getContext(), albumSong, 3, (songId, songImage, songTitle,  authorNames, view1) -> {
            AlbumDetailFragmentDirections.ActionNavAlbumDetailToAlbumBottomSheet action =
                    AlbumDetailFragmentDirections.actionNavAlbumDetailToAlbumBottomSheet(
                            songId,
                            songImage,
                            songTitle,
                            authorNames.toArray(new String[0]) // Chuyển List thành Array
                    );
            Navigation.findNavController(view1).navigate(action);
        });

        recyclerView.setAdapter(songAdapter);
        songAdapter.setOnItemClickListener(this);

        // Album of the same artist
        artist_album = view.findViewById(R.id.artist_album);
        artist_album.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        int widthInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 200, requireContext().getResources().getDisplayMetrics()
        );

        artist_albumAdapter = new AlbumAdapter(requireContext(), new ArrayList<>(), this::navigateToAlbumDetail, widthInPx, ViewGroup.LayoutParams.WRAP_CONTENT);

        artist_album.setAdapter(artist_albumAdapter);

        // Related albums
        related_album = view.findViewById(R.id.related_album);
        related_album.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        related_albumAdapter = new AlbumAdapter(requireContext(), new ArrayList<>(), this::navigateToAlbumDetail, widthInPx, ViewGroup.LayoutParams.WRAP_CONTENT);

        related_album.setAdapter(related_albumAdapter);
    }
    private void navigateToAlbumDetail(Album album){
        NavDirections action = AlbumDetailFragmentDirections.actionNavAlbumDetailSelf(
                album.getId(),
                album.getTitle(),
                album.getArtists_name().toArray(new String[0]), // List<String> → String[]
                album.getReleaseDate() != null ? album.getReleaseDate().getTime() : 0L, // Date → long
                album.getCoverUrl(),
                album.getCreatedAt() != null ? album.getCreatedAt().getTime() : 0L, // Date → long
                album.getLike_count(),
                album.getUpdatedAt() != null ? album.getUpdatedAt().getTime() : 0L, // Date → long
                album.getArtist_url().get(0) // Take the first url
        );
        Navigation.findNavController(requireView()).navigate(action);

    }

    private void setupViewModel() {
        AlbumViewModel albumViewModel = new ViewModelProvider(
                this,
                new AlbumViewModelFactory(requireContext())
        ).get(AlbumViewModel.class);

        albumViewModel.getSongs().observe(getViewLifecycleOwner(), songs -> {
            albumSong=songs;
            songAdapter.setData(songs);
        });

        albumViewModel.getAlbums().observe(getViewLifecycleOwner(), albums -> {
            related_albumAdapter.setData(albums);
        });

        albumViewModel.getArtistAlbums().observe(getViewLifecycleOwner(), albums -> {
            artist_albumAdapter.setData(albums);
        });


        // Fetch data
        albumViewModel.fetchAlbumSongs(albumId);
        albumViewModel.fetchAlbumsByIds();
        albumViewModel.fetchAlbumsByArtists(artistNames);

        SpotifyCloneApplication app = SpotifyCloneApplication.getInstance();
        viewModel = new ViewModelProvider(new ViewModelStoreOwner() {
            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                return app.getAppViewModelStore();
            }
        }, app.getMusicPlayerViewModelFactory()).get(MusicPlayerViewModel.class);

        viewModel.getPlaybackState().observe(getViewLifecycleOwner(), playbackState -> {
            if (playbackState != null) {
                updatePlayButton(playbackState == PlaybackState.PLAYING);
            }
        });
        viewModel.getShuffleMode().observe(getViewLifecycleOwner(), shuffleMode -> {
            if (shuffleMode != null) {
                updateShuffleButton(shuffleMode);
            }
        });
    }

    private void updateShuffleButton(ShuffleMode shuffleMode) {
        if (shuffleMode == ShuffleMode.SHUFFLE_ON) {
            shuffleButton.setImageResource(R.drawable.ic_shuffle_on);
            shuffleButton.setTag("shuffle_on");
        } else {
            shuffleButton.setImageResource(R.drawable.ic_shuffle_off);
            shuffleButton.setTag("shuffle_off");
        }
    }

    private void updatePlayButton(boolean isPlaying) {
        if (isPlaying && viewModel.getCurrentAlbumId() != null && Objects.equals(viewModel.getCurrentAlbumId().getValue(), albumId) && Objects.equals(viewModel.getPlayType().getValue(), MusicPlayerViewModel.PlaybackSourceType.ALBUM)) {
            playButton.setImageResource(R.drawable.ic_pause_circle);
            playButton.setTag("pause");
        } else {
            playButton.setImageResource(R.drawable.ic_play_circle);
            playButton.setTag("play");

        }
    }
    private void setupScrollListener() {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    DominantColorExtractor.getDominantColor(requireContext(), coverUrl, color -> {
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

        DominantColorExtractor.getDominantColor(requireContext(), coverUrl, color -> {
            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{color, secondColor}
            );
            gradient.setCornerRadius(0f);

            view.findViewById(R.id.imageConstraintLayout).setBackground(gradient);
        });
    }

    @Override
    public void onItemClick(Song song) {
        viewModel.playSongsFrom(albumId, albumTitle, MusicPlayerViewModel.PlaybackSourceType.ALBUM,song.getId());
    }
}