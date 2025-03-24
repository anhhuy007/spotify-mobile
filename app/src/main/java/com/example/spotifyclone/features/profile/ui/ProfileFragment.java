package com.example.spotifyclone.features.profile.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.MainActivity;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.features.profile.model.Profile;
import com.example.spotifyclone.features.profile.network.ProfileService;
import com.example.spotifyclone.shared.model.User;
import com.example.spotifyclone.shared.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileFragment extends Fragment implements EditProfileFragment.ProfileUpdateListener {

    private TextView userName, followDisc;
    private ImageView userAva;
    private ImageButton btnBack;
    private Button btnEdit;
    private Profile data;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("USER_ID", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupListeners();
        fetchUserProfile();
    }

    private void initViews(View view) {
        userName = view.findViewById(R.id.user_name);
        followDisc = view.findViewById(R.id.follow_info);
        userAva = view.findViewById(R.id.artist_logo);
        btnBack = view.findViewById(R.id.back_button);
        btnEdit = view.findViewById(R.id.edit_button);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnEdit.setOnClickListener(v -> openEditProfile());
    }

    private void fetchUserProfile() {

        Retrofit retrofit = RetrofitClient.getClient(getActivity());
        ProfileService apiService = retrofit.create(ProfileService.class);


        AuthRepository authRepo = new AuthRepository(getActivity());
        User user = authRepo.getUser();

        data.setId(user.getId());
        data.setName(user.getUsername());
        data.setAvatarUrl(user.getAvatarUrl());


    }

    private void openEditProfile() {


        // Create the bundle with the required arguments
        Bundle args = new Bundle();
        args.putString("USER_ID", data.getId());
        args.putString("USER_NAME", data.getName());
        args.putString("USER_PASSWORD", data.getPassword());
        args.putString("USER_AVATAR", data.getAvatarUrl());

        // Navigate using the Navigation Component
        androidx.navigation.Navigation.findNavController(requireView())
                .navigate(R.id.action_profileFragment_to_editProfileFragment2, args);
    }

    @Override
    public void onProfileUpdated() {
        fetchUserProfile();
    }
}