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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.MainActivity;
import com.example.spotifyclone.R;
import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.album.ui.AlbumFragmentDirections;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements AlbumAdapter.OnAlbumClickListener, ArtistAdapter.OnArtistClickListener {
    private RecyclerView newSongsRecyclerView, popularSongsRecyclerView, latestAlbumsRecylerView, popularAlbumsRecyclerView, popularArtistsRecyclerView;
//    private SongAdapter newSongsAdapter, popularSongsAdapter;
    private AlbumAdapter popularAlbumsAdapter, latestAlbumsAdapter;
    private ArtistAdapter popularArtistAdapter;
    private MusicPlayerViewModel musicPlayerViewModel;
    private User currentUser;
    private ImageView userAvatarImage;
    private TextView userNameText;
    private HomeViewModel homeViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initUser();
        initUI(view);
        setupListeners();
        setupViewModel();
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

    private void initUser() {
        AuthRepository authRepository = new AuthRepository(getContext());
        currentUser = authRepository.getUser();
    }
    private void initUI(View view) {
        int spacing = 20; // dp spacing
        boolean includeEdge = true;

//        newSongsRecyclerView = view.findViewById(R.id.rv_top_songs);
//        newSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//        newSongsAdapter = new SongAdapter(new ArrayList<>(), SongItemType.VERTICAL, this);
//        newSongsRecyclerView.setAdapter(newSongsAdapter);
//        newSongsRecyclerView.setNestedScrollingEnabled(false);
//
//        popularSongsRecyclerView = view.findViewById(R.id.rv_popular_songs);
//        popularSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        popularSongsAdapter = new SongAdapter(new ArrayList<>(), SongItemType.HORIZONTAL, this);
//        popularSongsRecyclerView.setAdapter(popularSongsAdapter);
//        popularSongsRecyclerView.addItemDecoration(new SpacingItemDecoration(spacing, includeEdge)); // Add spacing

        // Popular albums with horizontal layout
        popularAlbumsRecyclerView = view.findViewById(R.id.rv_popular_albums);
        popularAlbumsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularAlbumsAdapter = new AlbumAdapter(new ArrayList<>(), AlbumAdapter.AlbumItemType.HORIZONTAL, this);
        popularAlbumsRecyclerView.setAdapter(popularAlbumsAdapter);
        popularAlbumsRecyclerView.addItemDecoration(new SpacingItemDecoration(spacing, includeEdge)); // Add spacing

        // Latest albums with vertical layout
        latestAlbumsRecylerView = view.findViewById(R.id.rv_latest_albums);
        latestAlbumsRecylerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        latestAlbumsAdapter = new AlbumAdapter(new ArrayList<>(), AlbumAdapter.AlbumItemType.VERTICAL, this);
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
                musicPlayerViewModel.getCurrentAlbumId().observe(getViewLifecycleOwner(), albumId -> {
                    if (albumId != null) {
                        latestAlbumsAdapter.updateUI(playbackState, albumId);
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

        homeViewModel.getPopularArtists().observe(getViewLifecycleOwner(), artists -> {
            if(artists != null){
                popularArtistAdapter.setArtists(artists);
            }
        });

    }

//    @Override
//    public void onSongClick(Song song) {
//        musicPlayerViewModel.playSong(song);
//    }
//
//    @Override
//    public void onPlayClick(Song song) {
//        musicPlayerViewModel.togglePlayPause(song);
//    }

    @Override
    public void onAlbumClick(Album album) {
        NavDirections action = HomeFragmentDirections.actionNavHomeToNavAlbumDetail(
                album.getId(),
                album.getTitle(),
                album.getArtists_name().toArray(new String[0]), // List<String> → String[]
                album.getReleaseDate() != null ? album.getReleaseDate().getTime() : 0L, // Date → long
                album.getCoverUrl(),
                album.getCreatedAt() != null ? album.getCreatedAt().getTime() : 0L, // Date → long
                album.getLike_count(),
                album.getUpdatedAt() != null ? album.getUpdatedAt().getTime() : 0L, // Date → long
                album.getArtist_url().get(0)// Take the first url

        );
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onPlayClick(Album album) {
        musicPlayerViewModel.togglePlayPause(album.getId(), album.getTitle(), MusicPlayerViewModel.PlaybackSourceType.ALBUM);
    }

    @Override
    public void onArtistClick(Artist artist) {
        Bundle args = new Bundle();
        args.putString("ARTIST_ID", artist.getId());

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_homeFragment_to_artistDetailFragment, args);
    }
}

