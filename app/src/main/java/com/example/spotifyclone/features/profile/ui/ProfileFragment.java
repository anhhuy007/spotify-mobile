package com.example.spotifyclone.features.profile.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.features.follow.model.FollowCountResponse;
import com.example.spotifyclone.features.follow.viewModel.FollowedArtistsCountViewModel;
import com.example.spotifyclone.features.playlist.adapter.PlaylistAdapter;
import com.example.spotifyclone.features.playlist.adapter.ProfilePlaylistAdapter;
import com.example.spotifyclone.features.playlist.model.Playlist;
import com.example.spotifyclone.features.playlist.ui.AllPlaylistFragmentDirections;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModel;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModelFactory;
import com.example.spotifyclone.features.profile.viewmodel.ProfileViewModel;
import com.example.spotifyclone.shared.model.User;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    private TextView userName;
    private TextView followInfo;
    private ImageView userAvatar;
    private ImageButton backButton;
    private Button editButton;
    private FollowedArtistsCountViewModel followedArtistsCountViewModel;
    private AuthRepository authRepository;

    private RecyclerView playlistRecyclerView; //
    private ProfileViewModel profileViewModel;
    private PlaylistViewModel playlistViewModel;
    private ProfilePlaylistAdapter playlistAdapter;
    private User currentUser;


    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeDependencies();
        initializeViews(view);
        setupObservers();
        setupListeners();
        loadUserData();
        setupRecyclerView(view);

    }

    private void initializeDependencies() {
        authRepository = new AuthRepository(requireContext());
    }

    private void initializeViews(View view) {
        userName = view.findViewById(R.id.user_name);
        followInfo = view.findViewById(R.id.follow_info);
        userAvatar = view.findViewById(R.id.artist_logo);
        backButton = view.findViewById(R.id.back_button);
        editButton = view.findViewById(R.id.edit_button);

        playlistRecyclerView=view.findViewById(R.id.playlist_recycler_view);
    }

    private void setupObservers() {
        // Initialize ViewModels
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        followedArtistsCountViewModel = new ViewModelProvider(this).get(FollowedArtistsCountViewModel.class);

        // Observe user data changes
        profileViewModel.getUser().observe(getViewLifecycleOwner(), this::updateUserInterface);

        // Observe followed artists count
        followedArtistsCountViewModel.getCount().observe(getViewLifecycleOwner(), this::updateFollowCount);

        // Playlist Viewmodel
        playlistViewModel = new ViewModelProvider(
                this,
                new PlaylistViewModelFactory(requireContext())
        ).get(PlaylistViewModel.class);

        playlistViewModel.fetchPlaylists();
        playlistViewModel.getUserPlaylist().observe(getViewLifecycleOwner(), playlists -> {
            Log.d("profilefragment","hehe");

            Log.d("profilefragment",playlists.get(0).getName());
            playlistAdapter.setData(playlists);
        });

    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> navigateBack());
        editButton.setOnClickListener(v -> openEditProfile());
    }

    private void loadUserData() {
        currentUser = authRepository.getUser();
        if (currentUser != null) {
            updateUserInterface(currentUser);
            followedArtistsCountViewModel.fetchCount(currentUser.getId());
        }
    }

    private void updateUserInterface(User user) {
        if (user != null) {
            userName.setText(user.getUsername());
            loadUserAvatar(user.getAvatarUrl());
        }
    }

    private void loadUserAvatar(String avatarUrl) {
        if (getContext() != null) {
            Glide.with(requireContext())
                    .load(avatarUrl)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.default_avatar)
                    .into(userAvatar);
        }
    }

    private void updateFollowCount(FollowCountResponse count) {
        if (count != null) {
            String followText = "0 " +getString(R.string.fler) + " • " +
                    getString(R.string.fl) + " " +
                    count.getCount();
            followInfo.setText(followText);
        }
    }

    private void navigateBack() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    private void openEditProfile() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(((ViewGroup) requireView().getParent()).getId(), EditProfileFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        }
    }


    // For playlist
    private void setupRecyclerView(View view) {
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        playlistAdapter = new ProfilePlaylistAdapter(requireContext(), new ArrayList<>(), playlist -> {
            // Chuyển đến
            navigateToPlaylistDetail(playlist);
        }, currentUser.getUsername()); // add song Id,

        playlistRecyclerView.setAdapter(playlistAdapter);
    }

    private void navigateToPlaylistDetail(Playlist playlist){
        ProfileFragmentDirections.ActionProfileFragmentToPlaylistDetailFragment action=
                ProfileFragmentDirections.actionProfileFragmentToPlaylistDetailFragment(
                        currentUser.getUsername(),
                        currentUser.getAvatarUrl(),
                        playlist.getId(),
                        playlist.getName(),
                        playlist.getCoverUrl()
                );

        Navigation.findNavController(requireView()).navigate(action);
    }


}