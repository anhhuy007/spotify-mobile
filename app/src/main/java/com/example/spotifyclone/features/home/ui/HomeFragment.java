package com.example.spotifyclone.features.home.ui;

import android.os.Bundle;
import android.util.Log;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.artist.model.Artist;
import com.example.spotifyclone.features.authentication.viewmodel.AuthVMFactory;
import com.example.spotifyclone.features.authentication.viewmodel.AuthViewModel;
import com.example.spotifyclone.features.home.adapter.AlbumAdapter;
import com.example.spotifyclone.features.home.adapter.ArtistAdapter;
import com.example.spotifyclone.features.home.viewmodel.HomeVMFactory;
import com.example.spotifyclone.features.home.viewmodel.HomeViewModel;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.home.adapter.SongAdapter;
import com.example.spotifyclone.features.home.adapter.SongItemType;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements SongAdapter.OnSongClickListener, AlbumAdapter.OnAlbumClickListener, ArtistAdapter.OnArtistClickListener {
    private RecyclerView newSongsRecyclerView, popularSongsRecyclerView, latestAlbumsRecylerView, popularAlbumsRecyclerView, popularArtistsRecyclerView;
    private SongAdapter newSongsAdapter, popularSongsAdapter;
    private AlbumAdapter popularAlbumsAdapter, latestAlbumsAdapter;
    private ArtistAdapter popularArtistAdapter;
    private MusicPlayerViewModel musicPlayerViewModel;
    private HomeViewModel homeViewModel;

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
        newSongsRecyclerView = view.findViewById(R.id.rv_top_songs);
        newSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        newSongsAdapter = new SongAdapter(new ArrayList<>(), SongItemType.VERTICAL, this);
        newSongsRecyclerView.setAdapter(newSongsAdapter);

        popularSongsRecyclerView = view.findViewById(R.id.rv_popular_songs);
        popularSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularSongsAdapter = new SongAdapter(new ArrayList<>(), SongItemType.HORIZONTAL, this);
        popularSongsRecyclerView.setAdapter(popularSongsAdapter);

        popularAlbumsRecyclerView = view.findViewById(R.id.rv_popular_albums);
        popularAlbumsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularAlbumsAdapter = new AlbumAdapter(new ArrayList<>(), this);
        popularAlbumsRecyclerView.setAdapter(popularAlbumsAdapter);

        latestAlbumsRecylerView = view.findViewById(R.id.rv_latest_albums);
        latestAlbumsRecylerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        latestAlbumsAdapter = new AlbumAdapter(new ArrayList<>(), this);
        latestAlbumsRecylerView.setAdapter(latestAlbumsAdapter);

        popularArtistsRecyclerView = view.findViewById(R.id.rv_popular_artists);
        popularArtistsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularArtistAdapter = new ArtistAdapter(new ArrayList<>(), this);
        popularArtistsRecyclerView.setAdapter(popularArtistAdapter);
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
//        musicPlayerViewModel.getTopSongs().observe(getViewLifecycleOwner(), songs -> {
//            if (songs != null && !songs.isEmpty()) {
//                topSongsAdapter.setSongs(songs);
//                popularSongsAdapter.setSongs(songs);
//            }
//        });
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

//        musicPlayerViewModel.getError().observe(getViewLifecycleOwner(), errorMessage ->
//                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show());

//        homeViewModel.getLatestAlbums().observe(getViewLifecycleOwner(), albums -> {
//            if(albums != null) {
//                latestAlbumsAdapter.setAlbums(albums);
//            }
//        });
        homeViewModel.getPopularAlbums().observe(getViewLifecycleOwner(), albums -> {
            if(albums != null) {
                popularAlbumsAdapter.setAlbums(albums);
            }
        });
//        homeViewModel.getNewSongs().observe(getViewLifecycleOwner(), songs -> {
//            if(songs != null) {
//                newSongsAdapter.setSongs(songs);
//            }
//        });
//        homeViewModel.getPopularSongs().observe(getViewLifecycleOwner(), songs -> {
//            if(songs != null) {
//                popularSongsAdapter.setSongs(songs);
//            }
//        });
//        homeViewModel.getPopularArtists().observe(getViewLifecycleOwner(), artists -> {
//            if(artists != null){
//                popularArtistAdapter.setArtists(artists);
//            }
//        });

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
        Bundle args = new Bundle();

        Log.d("Album", "Click Album" + album);

        args.putString("cover_url", album.getCoverUrl());
        args.putString("name", album.getTitle());
        args.putString("id", album.getId());
        args.putString("day_create", album.getReleaseDate().toString());
        List<String> artistNamesList = album.getArtists_name() != null ? album.getArtists_name() : new ArrayList<>();
        String[] artistNamesArray = artistNamesList.toArray(new String[0]);
        args.putStringArray("artists_name", artistNamesArray);
        args.putString("artist_url", album.getCoverUrl());

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.nav_album_detail, args);
    }

    @Override
    public void onArtistClick(Artist artist) {

    }
}
