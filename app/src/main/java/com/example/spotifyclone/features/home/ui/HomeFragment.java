package com.example.spotifyclone.features.home.ui;

import android.annotation.SuppressLint;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.example.spotifyclone.features.player.ui.PlayerBottomSheetFragment;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.example.spotifyclone.shared.model.User;
import com.example.spotifyclone.shared.ui.BaseOnlineFragment;
import com.example.spotifyclone.shared.utils.NetworkStatusLiveData;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends BaseOnlineFragment implements AlbumAdapter.OnAlbumClickListener, ArtistAdapter.OnArtistClickListener, SongAdapter.OnSongClickListener {
    private RecyclerView localSongRecyclerView, popularAlbumsRecyclerView, latestAlbumsRecylerView, popularArtistsRecyclerView;
    private SongAdapter localSongsAdapter;
    private ImageView chatbotImage;
    private AlbumAdapter popularAlbumsAdapter, latestAlbumsAdapter;
    private ArtistAdapter popularArtistAdapter;
    private MusicPlayerViewModel musicPlayerViewModel;
    private User currentUser;
    private ImageView userAvatarImage;
    private TextView userNameText, tvPopularArtists, tvLatestAlbums, tvPopularAlbums, tvLocalSongs, tvLocal, tvRanking;
    private CardView localSongsCardView, cardViewArtistRanking, cardViewSongRanking, cardViewAlbumRanking;
    private ImageButton playLocalSongsButton;
    private HomeViewModel homeViewModel;
    private NetworkStatusLiveData networkStatusLiveData;

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

        localSongsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action = HomeFragmentDirections.actionHomeFragmentToLocalSongListFragment();
                Navigation.findNavController(requireView()).navigate(action);
            }
        });

        playLocalSongsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action = HomeFragmentDirections.actionHomeFragmentToLocalSongListFragment();
                Navigation.findNavController(requireView()).navigate(action);
            }
        });
    }

    private void initUser() {
        AuthRepository authRepository = new AuthRepository(getContext());
        currentUser = authRepository.getUser();

        Log.d("HomeFragment", "initUser: " + currentUser.getAvatarUrl());
    }
    private void initUI(View view) {
        int spacing = 20; // dp spacing
        boolean includeEdge = true;


        // CardView for ranking
        cardViewArtistRanking = view.findViewById(R.id.cardViewArtistRanking);
        cardViewSongRanking = view.findViewById(R.id.cardViewSongRanking);
        cardViewAlbumRanking = view.findViewById(R.id.cardViewAlbumRanking);

        cardViewArtistRanking.setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionNavHomeToTopArtist();
            Navigation.findNavController(requireView()).navigate(action);
        });

        cardViewSongRanking.setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionNavHomeToTopSong();
            Navigation.findNavController(requireView()).navigate(action);
        });

        cardViewAlbumRanking.setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionNavHomeToTopAlbum();
            Navigation.findNavController(requireView()).navigate(action);
        });

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

        localSongRecyclerView = view.findViewById(R.id.rv_local_songs);
        localSongRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        localSongsAdapter = new SongAdapter(new ArrayList<>(), SongItemType.VERTICAL, this);
        localSongRecyclerView.setAdapter(localSongsAdapter);
        localSongRecyclerView.addItemDecoration(new SpacingItemDecoration(spacing, includeEdge)); // Add spacing

        userAvatarImage = view.findViewById(R.id.iv_user_avatar);
        TextView userNameText = view.findViewById(R.id.tv_user_name);

        Picasso.get().load(currentUser.getAvatarUrl()).into(userAvatarImage);
        userNameText.setText(currentUser.getUsername());

        localSongsCardView = view.findViewById(R.id.cardViewLocalSongList);
        playLocalSongsButton = view.findViewById(R.id.btnPlay);

        // navigate to Chatbotfragment
        chatbotImage=view.findViewById(R.id.ic_chatbot);
        chatbotImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToChatbotFragment();
            }
        });

        tvPopularArtists = view.findViewById(R.id.tv_popular_artist);
        tvLatestAlbums = view.findViewById(R.id.tv_latest_albums);
        tvPopularAlbums = view.findViewById(R.id.tv_popular_albums);
        tvLocalSongs = view.findViewById(R.id.tv_local_songs);
        tvLocal = view.findViewById(R.id.tv_offline);
        tvRanking = view.findViewById(R.id.tv_top);
    }
    public void navigateToChatbotFragment() {
        NavDirections action = HomeFragmentDirections.actionNavHomeToChatbotFragment();
        Navigation.findNavController(requireView()).navigate(action);
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
        networkStatusLiveData = new NetworkStatusLiveData(requireContext());
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

        musicPlayerViewModel.getPlaybackState().observe(getViewLifecycleOwner(), playbackState -> {
            if (playbackState != null) {
                musicPlayerViewModel.getPlayType().observe(getViewLifecycleOwner(), type -> {
                    if(type != null) {
                        if (type == MusicPlayerViewModel.PlaybackSourceType.ALBUM) {
                          musicPlayerViewModel.getCurrentSong().observe(getViewLifecycleOwner(), song -> {
                                if (song != null) {
                                    localSongsAdapter.updateUI(playbackState, song);
                                }
                            });
                        }
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

        homeViewModel.getLocalSongs().observe(getViewLifecycleOwner(), songs -> {
            if(!songs.isEmpty()) {
                localSongsAdapter.setSongs(songs);
            } else {
                localSongRecyclerView.setVisibility(View.GONE);
                tvLocalSongs.setVisibility(View.GONE);
            }
        });

        networkStatusLiveData.observe(getViewLifecycleOwner(), isConnected -> {
            if (isConnected) {
                // Update UI online
                Log.d("HomeFragment", "Online state detected");
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
                updateOnlineOfflineUI(true);
            } else {
                updateOnlineOfflineUI(false);
                homeViewModel.getLocalSongs().observe(getViewLifecycleOwner(), songs -> {
                    if(songs != null) {
                        localSongsAdapter.setSongs(songs);
                    }
                });
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void updateOnlineOfflineUI(boolean isConnected) {
        if (isConnected) {
            Log.d("HomeFragment", "updateOnlineOfflineUI: Online");
            tvLocal.setVisibility(View.GONE);
            tvLocalSongs.setVisibility(View.GONE);
            localSongRecyclerView.setVisibility(View.GONE);
            localSongsCardView.setVisibility(View.GONE);

//            popularAlbumsRecyclerView.setVisibility(View.VISIBLE);
            latestAlbumsRecylerView.setVisibility(View.VISIBLE);
            popularArtistsRecyclerView.setVisibility(View.VISIBLE);
            tvPopularArtists.setVisibility(View.VISIBLE);
            tvLatestAlbums.setVisibility(View.VISIBLE);
            tvPopularAlbums.setVisibility(View.VISIBLE);
            chatbotImage.setVisibility(View.VISIBLE);
            cardViewAlbumRanking.setVisibility(View.VISIBLE);
            cardViewArtistRanking.setVisibility(View.VISIBLE);
            cardViewSongRanking.setVisibility(View.VISIBLE);
            tvRanking.setVisibility(View.VISIBLE);
        } else {
            if(currentUser != null && currentUser.isPremium()) {
                tvLocalSongs.setVisibility(View.VISIBLE);
                tvLocal.setVisibility(View.VISIBLE);
                localSongRecyclerView.setVisibility(View.VISIBLE);
                localSongsCardView.setVisibility(View.VISIBLE);
            } else {
                tvLocal.setVisibility(View.GONE);
                tvLocalSongs.setVisibility(View.GONE);
                localSongRecyclerView.setVisibility(View.GONE);
                localSongsCardView.setVisibility(View.GONE);

                tvLocalSongs.setVisibility(View.VISIBLE);
                tvLocalSongs.setText("Hãy nâng cấp lên tài khoản Premium để nghe nhạc offline");
            }
            popularAlbumsRecyclerView.setVisibility(View.GONE);
            latestAlbumsRecylerView.setVisibility(View.GONE);
            popularArtistsRecyclerView.setVisibility(View.GONE);
            tvPopularArtists.setVisibility(View.GONE);
            tvLatestAlbums.setVisibility(View.GONE);
            tvPopularAlbums.setVisibility(View.GONE);
            chatbotImage.setVisibility(View.GONE);
            cardViewAlbumRanking.setVisibility(View.GONE);
            cardViewArtistRanking.setVisibility(View.GONE);
            cardViewSongRanking.setVisibility(View.GONE);
            tvRanking.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAlbumClick(Album album) {
        NavDirections action = HomeFragmentDirections.actionNavHomeToNavAlbumDetail(
                album.getId()
//                album.getTitle(),
//                album.getArtists_name().toArray(new String[0]), // List<String> → String[]
//                album.getReleaseDate() != null ? album.getReleaseDate().getTime() : 0L, // Date → long
//                album.getCoverUrl(),
//                album.getCreatedAt() != null ? album.getCreatedAt().getTime() : 0L, // Date → long
//                album.getLike_count(),
//                album.getUpdatedAt() != null ? album.getUpdatedAt().getTime() : 0L, // Date → long
//                album.getArtist_url().get(0)// Take the first url

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

    @Override
    public void onSongClick(Song currentSong) {
        if (currentSong != null) {
            PlayerBottomSheetFragment playerSheet = PlayerBottomSheetFragment.newInstance(currentSong);
            playerSheet.show(requireActivity().getSupportFragmentManager(), PlayerBottomSheetFragment.TAG);
        }
    }

    @Override
    public void onPlayClick(Song song) {
        musicPlayerViewModel.playLocalSongs(song);
    }
}

