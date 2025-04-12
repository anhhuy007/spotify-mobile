package com.example.spotifyclone.features.artist.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.artist.adapter.AlbumArtistAdapter;
import com.example.spotifyclone.features.artist.adapter.ArtistAdapter;
import com.example.spotifyclone.features.artist.adapter.ArtistPlaylistAdapter;
import com.example.spotifyclone.features.artist.adapter.ArtistSimilarAdapter;
import com.example.spotifyclone.features.artist.adapter.SongArtistAdapter;
import com.example.spotifyclone.features.artist.model.PopularSong;
import com.example.spotifyclone.features.artist.viewModel.ArtistOverallViewModel;
import com.example.spotifyclone.features.artist.viewModel.FansAlsoLikeViewModel;
import com.example.spotifyclone.features.artist.viewModel.ListDiscographyAlbumViewModel;
import com.example.spotifyclone.features.artist.viewModel.ListDiscographyEPViewModel;
import com.example.spotifyclone.features.artist.viewModel.PopularViewModel;
import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.features.follow.model.Follow;
import com.example.spotifyclone.features.follow.viewModel.AddFollowerViewModel;
import com.example.spotifyclone.features.follow.viewModel.CheckFollowerViewModel;
import com.example.spotifyclone.features.follow.viewModel.DeleteFollowerViewModel;
import com.example.spotifyclone.features.follow.viewModel.FollowedArtistsCountViewModel;
import com.example.spotifyclone.features.player.model.playlist.ShuffleMode;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.example.spotifyclone.shared.model.User;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;
import com.google.android.material.button.MaterialButton;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ArtistFragment extends Fragment implements SongArtistAdapter.OnSongClickListener {
    // View components
    private MaterialButton btn_follow;
    private RecyclerView rv_popular_songs, rv_albums, rv_playlists, rv_similar_artists;
    private Context context;
    private ImageButton btnBack, btnPlay, btnShuffle;
    private TextView tv_artist_name, tv_artist_info, tv_monthly_listeners,
            participant_artist_detail, artist_name;
    private ImageView img_artist_artist_detail, img_artist_cover,
            img_album_artist_detail,
            btn_artist_detail_ui_background;
    private ScrollView scrollView;
    private ConstraintLayout artist_detail_info_container;
    private MaterialButton btnSeeSongs, btnHideSongs, btn_see_view_discography;
    private RelativeLayout navbar_artist_UI;
    private View rootView, fix;

    // Data and state variables
    private String artistId;
    private String artistName = "Test name";

    // ViewModels
    private MusicPlayerViewModel viewModel;
    private CheckFollowerViewModel checkFollowerViewModel;
    private AddFollowerViewModel addFollowerViewModel;
    private DeleteFollowerViewModel deleteFollowerViewModel;
    private AuthRepository authRepository;
    private String currentUserId;

    public static ArtistFragment newInstance(String artistId) {
        ArtistFragment fragment = new ArtistFragment();
        Bundle args = new Bundle();
        args.putString("ARTIST_ID", artistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistId = getArguments().getString("ARTIST_ID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_artist_detail_ui, container, false);
        context = getContext();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (artistId == null || artistId.isEmpty()) {
            Toast.makeText(context, "Invalid Artist ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize views
        initViews(view);
        initFollowComponents();
        setupListeners();
        setupViewModel();
        setupRecyclerViews();
        loadData();
    }

    @SuppressLint("SetTextI18n")
    private void initViews(View view) {
        // Initialize all view components
        rv_popular_songs = view.findViewById(R.id.rv_popular_songs);
        rv_albums = view.findViewById(R.id.rv_albums);
        rv_playlists = view.findViewById(R.id.rv_playlists);
        rv_similar_artists = view.findViewById(R.id.rv_similar_artists);

        tv_monthly_listeners = view.findViewById(R.id.tv_monthly_listeners);

        // set random text for monthly listeners
        int randomMonthlyListeners = (int) (Math.random() * 1000000);
        tv_monthly_listeners.setText(formatListeners(randomMonthlyListeners) + " " + getString(R.string.monthly_listeners));
        artist_name = view.findViewById(R.id.artist_name);
        tv_artist_name = view.findViewById(R.id.tv_artist_name);
        img_artist_cover = view.findViewById(R.id.img_artist_cover);
        scrollView = view.findViewById(R.id.artist_detail_scrollview);
        navbar_artist_UI = view.findViewById(R.id.navbar_artist_UI);

        btnBack = view.findViewById(R.id.btn_artist_detail_ui_back);
        btn_artist_detail_ui_background = view.findViewById(R.id.btn_artist_detail_ui_background);
        img_album_artist_detail = view.findViewById(R.id.img_album_artist_detail);
        btn_see_view_discography = view.findViewById(R.id.btn_see_view_discography);

        btnSeeSongs = view.findViewById(R.id.btn_see_all_songs);
        btnHideSongs = view.findViewById(R.id.btn_hide_songs);
        btn_follow = view.findViewById(R.id.btn_follow);

        btnPlay = view.findViewById(R.id.btn_play_artist_detail);
        btnShuffle = view.findViewById(R.id.btn_shuffle_artist_detail);
        fix = view.findViewById(R.id.fix_detailUI);
    }

    private void initFollowComponents() {
        // Initialize authentication and follow-related components
        authRepository = new AuthRepository(requireContext());
        User currentUser = authRepository.getUser();

        if (currentUser != null) {
            currentUserId = currentUser.getId();

            // Initialize ViewModels for follow functionality
            checkFollowerViewModel = new ViewModelProvider(this).get(CheckFollowerViewModel.class);
            addFollowerViewModel = new ViewModelProvider(this).get(AddFollowerViewModel.class);
            deleteFollowerViewModel = new ViewModelProvider(this).get(DeleteFollowerViewModel.class);

            // Setup follow button listeners and observers
            setupFollowButton();
        }
    }

    private void setupFollowButton() {
        // Follow button click listener
        btn_follow.setOnClickListener(v -> toggleFollowStatus());

        // Observe follow status
        checkFollowerViewModel.getIsFollowing().observe(getViewLifecycleOwner(), follow -> {
            updateFollowButtonState(follow != null && follow.getId() != null);
        });

        // Observe add follower response
        addFollowerViewModel.getAddedFollow().observe(getViewLifecycleOwner(), follow -> {
            if (follow != null) {
                updateFollowButtonState(true);
                Toast.makeText(requireContext(), "Followed successfully", Toast.LENGTH_SHORT).show();
            }
        });

        // Observe delete follower response
        deleteFollowerViewModel.getIsDeleted().observe(getViewLifecycleOwner(), isDeleted -> {
            if (isDeleted) {
                updateFollowButtonState(false);
                Toast.makeText(requireContext(), "Unfollowed successfully", Toast.LENGTH_SHORT).show();
            }
        });

        checkFollowStatus();
    }

    private void checkFollowStatus() {
        if (artistId != null && currentUserId != null) {
            Follow followCheck = new Follow();
            followCheck.setUserId(currentUserId);
            followCheck.setArtistId(artistId);

            checkFollowerViewModel.checkFollower(followCheck);
        }
    }

    private void toggleFollowStatus() {
        if (artistId != null && currentUserId != null) {
            Follow follow = new Follow();
            follow.setUserId(currentUserId);
            follow.setArtistId(artistId);

            // Check current state and perform appropriate action
            checkFollowerViewModel.getIsFollowing().observe(getViewLifecycleOwner(), existingFollow -> {
                if (existingFollow != null && existingFollow.getId() != null) {
                    // Currently following, so unfollow
                    deleteFollowerViewModel.deleteFollower(existingFollow.getId());
                } else {
                    // Not following, so follow
                    addFollowerViewModel.addFollower(follow);
                }
            });

            // Trigger the check
            checkFollowerViewModel.checkFollower(follow);
        } else {
            // Handle case where artistId or currentUserId is null
            Toast.makeText(requireContext(),
                    "Unable to perform follow action. Please try again.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFollowButtonState(boolean isFollowing) {
        if (isFollowing) {
            btn_follow.setText(getString(R.string.unfollow));
            btn_follow.setBackgroundColor(getResources().getColor(R.color.gray));
        } else {
            btn_follow.setText(getString(R.string.follow));
            btn_follow.setBackgroundColor(getResources().getColor(R.color.spotify_green));
        }
    }

    private void setupListeners() {
        // Back button listener
        btnBack.setOnClickListener(v -> {
            Navigation.findNavController(rootView).navigateUp();
        });

        // Fix button listener
        fix.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).navigateUp();
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        // Discography button listener
        btn_see_view_discography.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup) getView().getParent()).getId(), DiscographyFragment.newInstance(artistId,artistName))
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Play button listener
        btnPlay.setOnClickListener(v -> {
            Log.d("ArtistId", artistId + " " + artistName);
            viewModel.togglePlayPause(artistId, artistName, MusicPlayerViewModel.PlaybackSourceType.ARTIST);
        });
        btnShuffle.setOnClickListener(v -> {
            viewModel.cycleShuffleMode();
        });

        setupScrollListener();
    }

    private void setupViewModel() {
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
            btnShuffle.setImageResource(R.drawable.ic_shuffle_on);
            btnShuffle.setTag("shuffle_on");
            Log.d("ShuffleMode", shuffleMode.toString());
        } else {
            btnShuffle.setImageResource(R.drawable.ic_shuffle_off);
            Log.d("ShuffleMode", shuffleMode.toString());
            btnShuffle.setTag("shuffle_off");
        }
    }

    private void updatePlayButton(boolean isPlaying) {
        if (isPlaying && viewModel.getCurrentArtistId() != null && Objects.equals(viewModel.getCurrentArtistId().getValue(), artistId) && Objects.equals(viewModel.getPlayType().getValue(), MusicPlayerViewModel.PlaybackSourceType.ARTIST)) {
            btnPlay.setImageResource(R.drawable.ic_pause_circle);
            btnPlay.setTag("pause");
        } else {
            btnPlay.setImageResource(R.drawable.ic_play_circle);
            btnPlay.setTag("play");
        }
    }

    private void setupRecyclerViews() {
        rv_popular_songs.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rv_albums.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rv_playlists.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rv_similar_artists.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }

    @SuppressLint("SetTextI18n")
    private void loadData() {
        AtomicInteger sizeHide = new AtomicInteger();

        // Load popular songs
        PopularViewModel artistListViewModel = new ViewModelProvider(this,
                new PopularViewModel.Factory(requireActivity().getApplication(), artistId))
                .get(PopularViewModel.class);

        artistListViewModel.getListDiscography().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) {
                SongArtistAdapter rvPopularSongsAdapter = new SongArtistAdapter(getContext(), artists, this);
                rv_popular_songs.setAdapter(rvPopularSongsAdapter);

                ViewGroup.LayoutParams params = rv_popular_songs.getLayoutParams();
                params.height = (int) (64 * getResources().getDisplayMetrics().density * artists.size() * 11 / 20);
                rv_popular_songs.setLayoutParams(params);
                sizeHide.set(artists.size());
            }
        });
        artistListViewModel.fetchItems();

        // Load artist details and setup dynamic UI
        ArtistOverallViewModel artistViewModel = new ViewModelProvider(this,
                new ArtistOverallViewModel.Factory(requireActivity().getApplication(), artistId))
                .get(ArtistOverallViewModel.class);
        artistViewModel.getArtist().observe(getViewLifecycleOwner(), data -> {
            // Update artist details
            artistId = data.getId();
            artistName = data.getName();
            tv_artist_name.setText(data.getName());
            artist_name.setText(data.getName());

            // Set description and participant text
            tv_artist_info = rootView.findViewById(R.id.tv_artist_info);
            tv_artist_info.setText(data.getDescription());
            participant_artist_detail = rootView.findViewById(R.id.participant_artist_detail);
            participant_artist_detail.setText(getString(R.string.fans_also_like) + " " +  data.getName());

            // Load artist images
            loadArtistImages(data.getAvatarUrl());

            // Setup artist detail container click listener
            artist_detail_info_container = getView().findViewById(R.id.artist_detail_info_container);
            artist_detail_info_container.setOnClickListener(v -> navigateToArtistOverallFragment(data.getId()));

            // Apply dynamic background color
            applyDynamicBackground(data.getAvatarUrl());
        });
        artistViewModel.fetchArtistDetails();

        // Load other data (EPs, Albums, Playlists, Similar Artists)
        loadAdditionalArtistData();

        // Setup See More/Hide functionality for songs
        setupSongVisibilityControls(sizeHide);
    }

    private void loadArtistImages(String avatarUrl) {
        // Load artist images using Glide
        Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.loading)
                .into(img_album_artist_detail);

        Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.loading)
                .into(img_artist_cover);

        img_artist_artist_detail = getView().findViewById(R.id.img_artist_artist_detail);
        Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.loading)
                .into(img_artist_artist_detail);
    }

    private void navigateToArtistOverallFragment(String artistId) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(((ViewGroup) getView().getParent()).getId(), ArtistOverallFragment.newInstance(artistId))
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void applyDynamicBackground(String avatarUrl) {
        DominantColorExtractor.getDominantColor(context, avatarUrl, color -> {
            int baseColor = ContextCompat.getColor(context, R.color.white);
            int adjustedColor = adjustAlpha(color, 0.8f);

            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{adjustedColor, baseColor}
            );
            gradient.setCornerRadius(0.3f);
            navbar_artist_UI.setBackground(gradient);
        });
    }

    private void loadAdditionalArtistData() {
        // Load EPs
        ListDiscographyEPViewModel epViewModel = new ViewModelProvider(this,
                new ListDiscographyEPViewModel.Factory(requireActivity().getApplication(), artistId, 1))
                .get(ListDiscographyEPViewModel.class);
        epViewModel.getListDiscography().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) {
                AlbumArtistAdapter albumAdapter = new AlbumArtistAdapter(context, artists);
                albumAdapter.setRootView(rootView);
                rv_albums.setAdapter(albumAdapter);
            }
        });
        epViewModel.fetchItems();

        // Load albums and playlists
        ListDiscographyAlbumViewModel albumViewModel = new ViewModelProvider(this,
                new ListDiscographyAlbumViewModel.Factory(requireActivity().getApplication(), artistId, 1))
                .get(ListDiscographyAlbumViewModel.class);
        albumViewModel.getListDiscography().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) {
                ArtistPlaylistAdapter playlistAdapter = new ArtistPlaylistAdapter(context, artists);
                rv_playlists.setAdapter(playlistAdapter);
            }
        });
        albumViewModel.fetchItems();

        // Load similar artists
        FansAlsoLikeViewModel similarArtistsViewModel = new ViewModelProvider(this,
                new FansAlsoLikeViewModel.Factory(requireActivity().getApplication(), artistId))
                .get(FansAlsoLikeViewModel.class);
        similarArtistsViewModel.getListDiscography().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) {
                ArtistSimilarAdapter similarArtistsAdapter = new ArtistSimilarAdapter(context, getActivity(), artists);
                similarArtistsAdapter.setRootView(rootView);

                rv_similar_artists.setAdapter(similarArtistsAdapter);
            }
        });
        similarArtistsViewModel.fetchItems();
    }

    private void setupSongVisibilityControls(AtomicInteger sizeHide) {
        btnSeeSongs.setOnClickListener(v -> {
            ViewGroup.LayoutParams params = rv_popular_songs.getLayoutParams();
            params.height = (int) (64 * getResources().getDisplayMetrics().density * sizeHide.get());
            rv_popular_songs.setLayoutParams(params);

            btnSeeSongs.setVisibility(View.GONE);
            btnHideSongs.setVisibility(View.VISIBLE);
        });

        btnHideSongs.setOnClickListener(v -> {
            ViewGroup.LayoutParams params = rv_popular_songs.getLayoutParams();
            params.height = (int) (64 * getResources().getDisplayMetrics().density * sizeHide.get() * 11 / 20);
            rv_popular_songs.setLayoutParams(params);

            btnSeeSongs.setVisibility(View.VISIBLE);
            btnHideSongs.setVisibility(View.GONE);
        });
    }

    private void setupScrollListener() {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (!isAdded() || getContext() == null) {
                return;
            }

            int scrollY = scrollView.getScrollY();
            float nameHeight = tv_artist_name.getHeight();
            float threshold = (40 * getResources().getDisplayMetrics().density) + (nameHeight * 0.7f);

            float alpha = 0f;
            float alpha2 = 0.5f;
            if (scrollY > threshold) {
                alpha = Math.min(1f, (scrollY - threshold) / (nameHeight * 0.3f));
                alpha2 = Math.max(0f, 0.5f - (scrollY - threshold) / (nameHeight * 0.3f));
            }

            navbar_artist_UI.setAlpha(alpha);
            artist_name.setAlpha(alpha);
            btn_artist_detail_ui_background.setAlpha(alpha2);
        });
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(255 * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    public void onSongClick(PopularSong song) {
        viewModel.playSongsFrom(artistId, artistName, MusicPlayerViewModel.PlaybackSourceType.ARTIST,song.getId());
    }

    private static String formatListeners(int listeners) {
        // add dot for every 3 digits
        StringBuilder formatted = new StringBuilder();
        String str = String.valueOf(listeners);
        int length = str.length();
        for (int i = 0; i < length; i++) {
            formatted.append(str.charAt(i));
            if ((length - i - 1) % 3 == 0 && i != length - 1) {
                formatted.append(".");
            }
        }

        return formatted.toString();
    }
}