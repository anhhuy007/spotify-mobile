package com.example.spotifyclone.features.album.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.album.adapter.AlbumAdapter;
import com.example.spotifyclone.features.album.adapter.AlbumSongAdapter;
import com.example.spotifyclone.features.album.inter.AlbumMainCallbacks;
import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModel;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModelFactory;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlbumDetailFragment extends Fragment implements AlbumSongAdapter.OnItemClickListener {
    private ImageView albumImage;
    private TextView artist_name;
    private RecyclerView recyclerView;
    private RecyclerView artist_album;
    private RecyclerView related_album;
    private AlbumSongAdapter songAdapter;
    private ImageView artist_image;
    private TextView artist_name2;
    private TextView artist_album_text;
    private AlbumAdapter artist_albumAdapter;
    private MusicPlayerViewModel viewModel;
    private ImageButton playButton;

    private AlbumAdapter related_albumAdapter;
    private Toolbar toolbar;
    private NestedScrollView nestedScrollView;

    // Arguments từ navigation
    private String coverUrl;
    private String albumTitle;
    private String albumId;
    private String dayCreate;
    private List<String> artistNames;
    private String artistUrl;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_album_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            coverUrl = getArguments().getString("cover_url");
            albumTitle = getArguments().getString("title");
            albumId = getArguments().getString("_id");
            dayCreate = getArguments().getString("release_date");

            String[] artist_names_stringarray=getArguments().getStringArray("artist");
            artistNames = Arrays.asList(artist_names_stringarray);
            artistUrl=getArguments().getString("artist_url");

            if(artistNames==null){
                Log.d("AlbumDetailFragment", "Failed to fetch arristname");

            }
            else {
                Log.d("AlbumDetailFragment", String.join(" ",artistNames));

            }
        } else {
            Log.e("AlbumDetailFragment", "Arguments is null");
            NavHostFragment.findNavController(this).navigateUp();
            return;
        }

        initViews(view);        setupViewModel();

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
            viewModel.togglePlayPauseAlbum(albumId, albumTitle);
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
    }

    private void setupUI() {
//        // Set artist names
//        String artistNamesJoined = (artistNames != null && !artistNames.isEmpty())
//            ? TextUtils.join(", ", artistNames)
//            : "Unknown Artist";
//
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
        songAdapter = new AlbumSongAdapter(requireContext(), new ArrayList<>(), 3);
        recyclerView.setAdapter(songAdapter);
        songAdapter.setOnItemClickListener(this);

        // Album of the same artist
        artist_album = view.findViewById(R.id.artist_album);
        artist_album.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        int widthInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 200, requireContext().getResources().getDisplayMetrics()
        );

        artist_albumAdapter = new AlbumAdapter(requireContext(), new ArrayList<>(), album -> {
            Bundle args = createBundleFromAlbum(album);
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.nav_album_detail, args);
        }, widthInPx, ViewGroup.LayoutParams.WRAP_CONTENT);

        artist_album.setAdapter(artist_albumAdapter);

        // Related albums
        related_album = view.findViewById(R.id.related_album);
        related_album.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        related_albumAdapter = new AlbumAdapter(requireContext(), new ArrayList<>(), album -> {
            Bundle args = createBundleFromAlbum(album);
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.nav_album_detail, args);
        }, widthInPx, ViewGroup.LayoutParams.WRAP_CONTENT);

        related_album.setAdapter(related_albumAdapter);
    }

    private Bundle createBundleFromAlbum(Album album) {
        Bundle args = new Bundle();
        args.putString("cover_url", album.getCoverUrl());
        args.putString("name", album.getTitle());
        args.putString("id", album.getId());
        args.putString("day_create", album.getReleaseDate().toString());
        args.putStringArrayList("artists_name", new ArrayList<>(album.getArtists_name()));
        args.putString("artist_url", album.getCoverUrl());
        return args;
    }

    private void setupViewModel() {
        AlbumViewModel albumViewModel = new ViewModelProvider(
                this,
                new AlbumViewModelFactory(requireContext())
        ).get(AlbumViewModel.class);

        albumViewModel.getSongs().observe(getViewLifecycleOwner(), songs -> {
            Log.d("AlbumDetailFragmentSong", "Songs: " + songs);
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
    }

    private void updatePlayButton(boolean isPlaying) {
        if (isPlaying) {
            playButton.setImageResource(R.drawable.play_button);
            playButton.setTag("pause");
        } else {
            playButton.setImageResource(R.drawable.play_button);
            playButton.setTag("play");
        }
    }

    private void setupScrollListener() {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
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
        Log.d("AlbumClick", "Song" + song.toString() + "Album" + albumId);
        viewModel.playAlbumSong(albumId, albumTitle, song);
    }
}