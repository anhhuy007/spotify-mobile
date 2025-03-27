package com.example.spotifyclone.features.profile.ui;

import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.features.follow.model.FollowCountResponse;
import com.example.spotifyclone.features.follow.viewModel.FollowedArtistsCountViewModel;
import com.example.spotifyclone.features.profile.viewmodel.ProfileViewModel;
import com.example.spotifyclone.shared.model.User;

public class ProfileFragment extends Fragment {
    private TextView userName;
    private TextView followInfo;
    private ImageView userAvatar;
    private ImageButton backButton;
    private Button editButton;

    private ProfileViewModel profileViewModel;
    private FollowedArtistsCountViewModel followedArtistsCountViewModel;
    private AuthRepository authRepository;

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
    }

    private void setupObservers() {
        // Initialize ViewModels
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        followedArtistsCountViewModel = new ViewModelProvider(this).get(FollowedArtistsCountViewModel.class);

        // Observe user data changes
        profileViewModel.getUser().observe(getViewLifecycleOwner(), this::updateUserInterface);

        // Observe followed artists count
        followedArtistsCountViewModel.getCount().observe(getViewLifecycleOwner(), this::updateFollowCount);
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> navigateBack());
        editButton.setOnClickListener(v -> openEditProfile());
    }

    private void loadUserData() {
        User currentUser = authRepository.getUser();
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
}