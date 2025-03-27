package com.example.spotifyclone.features.playlist.ui;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.album.adapter.AlbumSongAdapter;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModel;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModelFactory;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaylistDetailFragment extends Fragment {
    private String user_name;
    private String user_image;
    private String playlist_id;
    private String playlistUrl;
    private String playlistTitle;
    private PlaylistViewModel playlistViewModel;
    private AlbumSongAdapter songAdapter;
    private List<Song> playlist_songs=new ArrayList<>();


    // UI component
    private ImageView playlist_image;
    private Toolbar toolbar;
    private NestedScrollView nestedScrollView;
    private RecyclerView song_recyclerview;
    //
    private ImageView playlistImage;
    private ImageView userImage;
    private TextView userName;






    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist_detail, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Take arguments from previous fragment
        PlaylistDetailFragmentArgs args = PlaylistDetailFragmentArgs.fromBundle(getArguments());
        user_name = args.getUserName();
        user_image = args.getUserImage();
        playlist_id=args.getPlaylistId();
        playlistTitle=args.getPlaylistName();
        playlistUrl=args.getPlaylistImage();



        initViews(view);
        setupUI();
        setupViewModel();
        setupRecyclerView(view);
        setupToolbar((AppCompatActivity) requireActivity());
        setupScrollListener(); //
        setupGradientBackground(view); //

    }

    private void setupUI() {
        // Load playlist image
        Glide.with(requireContext())
                .load(playlistUrl)
                .into(playlist_image);

        // User info
        Glide.with(requireContext())
                .load(user_image)
                .into(userImage);

        userName.setText(user_name);


    }

    private  void setupRecyclerView(View view){
        song_recyclerview=view.findViewById(R.id.song_recyclerview);
        song_recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        songAdapter = new AlbumSongAdapter(getContext(), playlist_songs, 3, (songId, songImage, songTitle,  authorNames, view1) -> {
        });
        song_recyclerview.setAdapter(songAdapter);
    }
    private void setupToolbar(AppCompatActivity activity) {
        activity.setSupportActionBar(toolbar);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setTitle(playlistTitle);
        }

        // Use MenuProvider for handling toolbar menu events
        activity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // Không cần thêm menu items
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == android.R.id.home) {
                    // Sử dụng Navigation Component để quay lại
                    NavHostFragment.findNavController(PlaylistDetailFragment.this).navigateUp();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }


    private void initViews(View view){
        playlist_image = view.findViewById(R.id.playlist_image);

        toolbar = view.findViewById(R.id.toolbar);
        userImage=view.findViewById(R.id.user_image);
        userName=view.findViewById(R.id.user_name);




        nestedScrollView = view.findViewById(R.id.nestedScrollview);
    }
    private void setupViewModel(){
        playlistViewModel=new ViewModelProvider(
                this,
                new PlaylistViewModelFactory(requireContext())).get(PlaylistViewModel.class);
        playlistViewModel.fetchPlaylistSong(playlist_id);
        playlistViewModel.getPlaylistSongs().observe(getViewLifecycleOwner(),songs->{
                playlist_songs=songs;
                songAdapter.setData(songs);

        });
    }

    private void setupScrollListener() {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    DominantColorExtractor.getDominantColor(requireContext(), playlistUrl, color -> {
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

        DominantColorExtractor.getDominantColor(requireContext(), playlistUrl, color -> {
            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{color, secondColor}
            );
            gradient.setCornerRadius(0f);

            view.findViewById(R.id.imageConstraintLayout).setBackground(gradient);
        });
    }





}
