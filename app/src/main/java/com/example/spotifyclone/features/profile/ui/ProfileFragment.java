package com.example.spotifyclone.features.profile.ui;

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
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.profile.network.apiProfileService;
import com.example.spotifyclone.features.profile.model.Profile;
import com.example.spotifyclone.features.profile.network.profileRetrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileFragment extends Fragment {

    private TextView userName, followDisc;
    private ImageView userAva;
    private ImageButton btnBack;
    private Button btnEdit;
    private Profile data;
    private String userId;

    private ActivityResultLauncher<Intent> editProfileLauncher;

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
        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
        }

        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == requireActivity().RESULT_OK) {
                        fetchUserProfile();
                    }
                });
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
        if (userId == null) {
            Toast.makeText(requireContext(), "Invalid User ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = profileRetrofit.getClient();
        apiProfileService apiService = retrofit.create(apiProfileService.class);

        Call<Profile> call = apiService.getUserProfile(userId);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    data = response.body();
                    userName.setText(data.getName());
                    followDisc.setText(data.getFollower() + " " + getString(R.string.fler) + " • "
                            + getString(R.string.fl) + " " + data.getFollow());

                    Glide.with(requireContext())
                            .load(data.getAvatarUrl())
                            .placeholder(R.drawable.loading)
                            .into(userAva);
                } else if (isAdded()) {
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void openEditProfile() {
        if (data == null) {
            Toast.makeText(requireContext(), "User data is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(requireContext(), EditProfileActivity.class);
        intent.putExtra("USER_ID", data.getId());
        intent.putExtra("USER_NAME", data.getName());
        intent.putExtra("USER_PASSWORD", data.getPassword());
        intent.putExtra("USER_AVATAR", data.getAvatarUrl());

        editProfileLauncher.launch(intent);
    }
}