package com.example.spotifyclone.features.home.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.MainActivity;
import com.example.spotifyclone.R;
import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.artist.model.Artist;
import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.features.home.adapter.AlbumAdapter;
import com.example.spotifyclone.features.home.adapter.ArtistAdapter;
import com.example.spotifyclone.features.home.viewmodel.HomeVMFactory;
import com.example.spotifyclone.features.home.viewmodel.HomeViewModel;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.home.adapter.SongAdapter;
import com.example.spotifyclone.features.home.adapter.SongItemType;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.example.spotifyclone.shared.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements SongAdapter.OnSongClickListener, AlbumAdapter.OnAlbumClickListener, ArtistAdapter.OnArtistClickListener {
    private RecyclerView newSongsRecyclerView, popularSongsRecyclerView, latestAlbumsRecylerView, popularAlbumsRecyclerView, popularArtistsRecyclerView;
    private SongAdapter newSongsAdapter, popularSongsAdapter;
    private AlbumAdapter popularAlbumsAdapter, latestAlbumsAdapter;
    private ArtistAdapter popularArtistAdapter;
    private MusicPlayerViewModel musicPlayerViewModel;
    private HomeViewModel homeViewModel;
    private ImageView userAvatarImage;
    private TextView userNameText;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initCurrentUser();
        initUI(view);
        setupViewModel();
        setupListeners();
        observeViewModel();
        return view;
    }

    private void setupListeners() {
        userAvatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).openDrawer();
            }
        });

    }

    private void initCurrentUser() {
        AuthRepository authRepository = new AuthRepository(getContext());
        currentUser = authRepository.getUser();
    }

    private void initUI(View view) {
        int spacing = 20; // dp spacing
        boolean includeEdge = true;

        newSongsRecyclerView = view.findViewById(R.id.rv_top_songs);
        newSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        newSongsAdapter = new SongAdapter(new ArrayList<>(), SongItemType.VERTICAL, this);
        newSongsRecyclerView.setAdapter(newSongsAdapter);
        newSongsRecyclerView.setNestedScrollingEnabled(false);

        popularSongsRecyclerView = view.findViewById(R.id.rv_popular_songs);
        popularSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularSongsAdapter = new SongAdapter(new ArrayList<>(), SongItemType.HORIZONTAL, this);
        popularSongsRecyclerView.setAdapter(popularSongsAdapter);
        popularSongsRecyclerView.addItemDecoration(new SpacingItemDecoration(spacing, includeEdge)); // Add spacing

        popularAlbumsRecyclerView = view.findViewById(R.id.rv_popular_albums);
        popularAlbumsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularAlbumsAdapter = new AlbumAdapter(new ArrayList<>(), this);
        popularAlbumsRecyclerView.setAdapter(popularAlbumsAdapter);
        popularAlbumsRecyclerView.addItemDecoration(new SpacingItemDecoration(spacing, includeEdge)); // Add spacing

        latestAlbumsRecylerView = view.findViewById(R.id.rv_latest_albums);
        latestAlbumsRecylerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        latestAlbumsAdapter = new AlbumAdapter(new ArrayList<>(), this);
        latestAlbumsRecylerView.setAdapter(latestAlbumsAdapter);
        latestAlbumsRecylerView.addItemDecoration(new SpacingItemDecoration(spacing, includeEdge)); // Add spacing

        popularArtistsRecyclerView = view.findViewById(R.id.rv_popular_artists);
        popularArtistsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularArtistAdapter = new ArtistAdapter(new ArrayList<>(), this);
        popularArtistsRecyclerView.setAdapter(popularArtistAdapter);
        popularArtistsRecyclerView.addItemDecoration(new SpacingItemDecoration(spacing, includeEdge)); // Add spacing

        userAvatarImage = view.findViewById(R.id.iv_user_avatar);
        userNameText = view.findViewById(R.id.tv_user_name);

        Picasso.get().load(currentUser.getAvatarUrl()).into(userAvatarImage);
        userNameText.setText(currentUser.getUsername());
    }

    private void setupViewModel() {
        SpotifyCloneApplication app = SpotifyCloneApplication.getInstance();
        musicPlayerViewModel = new ViewModelProvider(new ViewModelStoreOwner() {
            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                return app.getAppViewModelStore();
            }
        }, app.getMusicPlayerViewModelFactory()).get(MusicPlayerViewModel.class);

        homeViewModel = new ViewModelProvider(this, new HomeVMFactory(getContext())).get(HomeViewModel.class);
    }

    private void observeViewModel() {
        musicPlayerViewModel.getPlaybackState().observe(getViewLifecycleOwner(), playbackState -> {
            if (playbackState != null) {
                musicPlayerViewModel.getCurrentSong().observe(getViewLifecycleOwner(), song -> {
                    if (song != null) {
                        newSongsAdapter.updateUI(playbackState, song);
                        popularSongsAdapter.updateUI(playbackState, song);
                    }
                });
            }
        });

        musicPlayerViewModel.getError().observe(getViewLifecycleOwner(), errorMessage ->
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show());

        homeViewModel.getLatestAlbums().observe(getViewLifecycleOwner(), albums -> {
            if(albums != null) {
                latestAlbumsAdapter.setAlbums(albums);
            }
        });
        homeViewModel.getPopularAlbums().observe(getViewLifecycleOwner(), albums -> {
            if(albums != null) {
                popularAlbumsAdapter.setAlbums(albums);
            }
        });
        homeViewModel.getNewSongs().observe(getViewLifecycleOwner(), songs -> {
            if(songs != null) {
                newSongsAdapter.setSongs(songs);
            }
        });
        homeViewModel.getPopularSongs().observe(getViewLifecycleOwner(), songs -> {
            if(songs != null) {
                popularSongsAdapter.setSongs(songs);
            }
        });
        homeViewModel.getPopularArtists().observe(getViewLifecycleOwner(), artists -> {
            if(artists != null){
                popularArtistAdapter.setArtists(artists);
            }
        });

    }

    @Override
    public void onSongClick(Song song) {
        musicPlayerViewModel.playSong(song);
    }

    @Override
    public void onPlayClick(Song song) {
        musicPlayerViewModel.togglePlayPause(song);
    }

    @Override
    public void onAlbumClick(Album album) {

    }

    @Override
    public void onArtistClick(Artist artist) {

    }
}
