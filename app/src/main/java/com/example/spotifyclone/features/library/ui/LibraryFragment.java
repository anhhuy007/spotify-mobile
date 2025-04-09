package com.example.spotifyclone.features.library.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.artist.ui.ArtistFragment;
import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.features.library.adapter.LibraryArtistAdapter;
import com.example.spotifyclone.features.library.adapter.LibraryPlaylistAdapter;
import com.example.spotifyclone.features.library.model.LibraryArtist;
import com.example.spotifyclone.features.library.model.LibraryPlaylist;
import com.example.spotifyclone.features.library.viewModel.LibraryArtistsViewModel;
import com.example.spotifyclone.features.library.viewModel.LibraryPlaylistsViewModel;
import com.example.spotifyclone.features.library.viewModel.LikedSongsViewModel;
import com.example.spotifyclone.features.playlist.ui.PlaylistDetailFragment;
import com.example.spotifyclone.shared.model.User;

public class LibraryFragment extends Fragment  {

    private Context context;
    private User currentUser;
    private RecyclerView artistsRecyclerView, playlistsRecyclerView;
    private TextView playlistTab, artistTab, likedSongsCount;
    private TextView profileInitial;
    private ImageView profileImageI, clearFilterButton;
    private View likedSongsContainer;
    private View tabContainer;
    private boolean isFilterActive = false;
    private ConstraintLayout addArtistContainer,addPodcastContainer;

    private LibraryArtistsViewModel viewModelArtist;
    private LibraryPlaylistsViewModel viewModelPlaylist;

    private int playlistCount ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = requireContext();
        initializeViews(view);
        setupRecyclerViews();
        setupListeners();
        loadUserProfile();
        fetchData();
    }

    private void initializeViews(View view) {
        // Initialize RecyclerViews
        artistsRecyclerView = view.findViewById(R.id.artistsRecyclerView);
        playlistsRecyclerView = view.findViewById(R.id.playlistsRecyclerView);

        // Initialize tabs
        playlistTab = view.findViewById(R.id.playlistTab);
        artistTab = view.findViewById(R.id.artistTab);
        tabContainer = view.findViewById(R.id.tabContainer);

        // Initialize clear filter button (X button)
        clearFilterButton = view.findViewById(R.id.clearFilterButton);

        // Initialize user profile
        profileInitial = view.findViewById(R.id.profileInitial);
        profileImageI = view.findViewById(R.id.profileImageI);

        // Initialize liked songs container
        likedSongsContainer = view.findViewById(R.id.likedSongsContainer);
        likedSongsCount = view.findViewById(R.id.likedSongsCount);

        addArtistContainer = view.findViewById(R.id.addArtistContainer);
        addPodcastContainer = view.findViewById(R.id.addPodcastContainer);

        viewModelArtist = new ViewModelProvider(this,
                new LibraryArtistsViewModel.Factory(requireActivity().getApplication()))
                .get(LibraryArtistsViewModel.class);


        viewModelPlaylist = new ViewModelProvider(this,
                new LibraryPlaylistsViewModel.Factory(requireActivity().getApplication()))
                .get(LibraryPlaylistsViewModel.class);

        AuthRepository authRepository = new AuthRepository(requireContext());
        currentUser = authRepository.getUser();
        Glide.with(this)
                .load(currentUser.getAvatarUrl())
                .placeholder(R.drawable.loading)
                .into(profileImageI);

        viewModelArtist.getArtistsList().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null && !artists.isEmpty()) {
                LibraryArtistAdapter adapter = new LibraryArtistAdapter(context, artists,
                        artist -> navigateToArtistDetail(artist.getId()));
                artistsRecyclerView.setAdapter(adapter);
            }
        });

        viewModelPlaylist.getPlaylistsList().observe(getViewLifecycleOwner(), playlists -> {
            if (playlists != null && !playlists.isEmpty()) {
                playlistCount = playlists.size();
                LibraryPlaylistAdapter adapter = new LibraryPlaylistAdapter(context, playlists,
                        playlist -> navigateToPlaylistDetail(playlist.getId()));
                playlistsRecyclerView.setAdapter(adapter);
            }
        });

    }

    private void setupRecyclerViews() {
        // Set layout managers
        artistsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        playlistsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setupListeners() {

        addArtistContainer.setOnClickListener(v -> {

        });
        addPodcastContainer.setOnClickListener(v -> {

NewPlaylistDialog newPlaylistBottomSheet = NewPlaylistDialog.newInstance("Danh sách phát thứ "+(playlistCount+1)+ " của tôi", currentUser.getAvatarUrl());
            newPlaylistBottomSheet.setViewModel(viewModelPlaylist);
newPlaylistBottomSheet.show(getParentFragmentManager(), "NewPlaylistBottomSheet");
        });
        playlistTab.setOnClickListener(v -> {
            if (!isFilterActive || (isFilterActive && !artistsRecyclerView.isShown())) {
                // If no filter is active or if playlists are already showing,
                // activate filter and show only playlists
                activateFilter(true);
            }
        });

        artistTab.setOnClickListener(v -> {
            if (!isFilterActive || (isFilterActive && !playlistsRecyclerView.isShown())) {
                // If no filter is active or if artists are already showing,
                // activate filter and show only artists
                activateFilter(false);
            }
        });

        clearFilterButton.setOnClickListener(v -> {
            // Clear filter and show all content
            deactivateFilter();
        });

        // Default state - no filter active
        deactivateFilter();

        // Setup liked songs click listener
        likedSongsContainer.setOnClickListener(v -> {
            navigateToLikedSongs();
        });
    }

    private void activateFilter(boolean isPlaylistSelected) {
        isFilterActive = true;
        clearFilterButton.setVisibility(View.VISIBLE);

        // Set tab backgrounds - selected tab gets green background
        if (isPlaylistSelected) {
//            playlistTab.setBackgroundResource(R.drawable.green_rounded_button_background);
            artistTab.setVisibility(View.GONE);
            artistsRecyclerView.setVisibility(View.GONE);
            playlistsRecyclerView.setVisibility(View.VISIBLE);
        } else {
//            artistTab.setBackgroundResource(R.drawable.green_rounded_button_background);
            playlistTab.setVisibility(View.GONE);
            artistsRecyclerView.setVisibility(View.VISIBLE);
            playlistsRecyclerView.setVisibility(View.GONE);
        }
    }

    private void deactivateFilter() {
        isFilterActive = false;
        clearFilterButton.setVisibility(View.GONE);

        // Reset tab backgrounds and visibility
        playlistTab.setBackgroundResource(R.drawable.rounded_button_background);
        artistTab.setBackgroundResource(R.drawable.rounded_button_background);
        playlistTab.setVisibility(View.VISIBLE);
        artistTab.setVisibility(View.VISIBLE);

        // Show both content sections
        artistsRecyclerView.setVisibility(View.VISIBLE);
        playlistsRecyclerView.setVisibility(View.VISIBLE);
    }

    private void loadUserProfile() {
        // Placeholder for user profile loading
        // In a real app, this would load from user session or preferences
        String userInitial = "N"; // Get from user name
        profileInitial.setText(userInitial);

        // If user has profile image
        String profileImageUrl = null; // Get from user profile
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Glide.with(context)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.loading)
                    .into(profileImageI);
            profileInitial.setVisibility(View.GONE);
        } else {
            profileInitial.setVisibility(View.VISIBLE);
        }
    }

    private void fetchData() {
        viewModelArtist.fetchArtists();
        viewModelPlaylist.fetchPlaylists();
        fetchLikedSongsCount();
    }

    private void fetchLikedSongsCount() {
        LikedSongsViewModel viewModel = new ViewModelProvider(this,
                new LikedSongsViewModel.Factory(requireActivity().getApplication()))
                .get(LikedSongsViewModel.class);

        viewModel.getLikedSongsCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                TextView songsCountText = likedSongsContainer.findViewById(R.id.likedSongsCount);
                if (songsCountText != null) {
                    songsCountText.setText("Danh sách phát • " + count + " bài hát");
                }
            }
        });

        viewModel.fetchLikedSongsCount();
    }



    private void navigateToArtistDetail(String artistId) {
        if (artistId != null && !artistId.isEmpty()) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup) requireView().getParent()).getId(), ArtistFragment.newInstance(artistId))
                        .addToBackStack(null)
                        .commit();
            }
        } else {
            Toast.makeText(context, "Invalid Artist ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToPlaylistDetail(String playlistId) {
        if (playlistId != null && !playlistId.isEmpty()) {
            // Commented out as in original code
            // Fragment fragment = PlaylistDetailFragment.newInstance(playlistId);
            // requireActivity().getSupportFragmentManager()
            //         .beginTransaction()
            //         .replace(R.id.fragmentContainer, fragment)
            //         .addToBackStack(null)
            //         .commit();
        } else {
            Toast.makeText(context, "Invalid Playlist ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToLikedSongs() {
        // Navigate to liked songs fragment/activity
        // Commented out as in original code
        // Fragment fragment = LikedSongsFragment.newInstance();
        // requireActivity().getSupportFragmentManager()
        //         .beginTransaction()
        //         .replace(R.id.fragmentContainer, fragment)
        //         .addToBackStack(null)
        //         .commit();
    }
}