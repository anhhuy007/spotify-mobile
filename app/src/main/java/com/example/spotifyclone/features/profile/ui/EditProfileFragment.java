package com.example.spotifyclone.features.profile.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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

public class EditProfileFragment extends Fragment {

    private EditText editName, editPassword;
    private ImageView avatar;
    private ImageButton btnClose;
    private Button btnSave;
    private String userId;
    private String userName;
    private String userPassword;
    private String userAvatar;
    private apiProfileService apiService;
    private ProfileUpdateListener listener;

    public interface ProfileUpdateListener {
        void onProfileUpdated();
    }

    public EditProfileFragment() {
        // Required empty public constructor
    }

    public static EditProfileFragment newInstance(String userId, String userName, String userPassword, String userAvatar) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString("USER_ID", userId);
        args.putString("USER_NAME", userName);
        args.putString("USER_PASSWORD", userPassword);
        args.putString("USER_AVATAR", userAvatar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Try to get listener from parent fragment or activity
        try {
            if (getParentFragment() instanceof ProfileUpdateListener) {
                listener = (ProfileUpdateListener) getParentFragment();
            } else if (context instanceof ProfileUpdateListener) {
                listener = (ProfileUpdateListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ProfileUpdateListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
            userName = getArguments().getString("USER_NAME");
            userPassword = getArguments().getString("USER_PASSWORD");
            userAvatar = getArguments().getString("USER_AVATAR");
        }

        Retrofit retrofit = profileRetrofit.getClient();
        apiService = retrofit.create(apiProfileService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupData();
        setupListeners();
    }

    private void initViews(View view) {
        editName = view.findViewById(R.id.display_name);
        editPassword = view.findViewById(R.id.display_password);
        avatar = view.findViewById(R.id.artist_logo);
        btnClose = view.findViewById(R.id.close_icon);
        btnSave = view.findViewById(R.id.btn_save);
    }

    private void setupData() {
        editName.setText(userName);
        editPassword.setText(userPassword);

        Glide.with(requireContext())
                .load(userAvatar)
                .placeholder(R.drawable.loading)
                .into(avatar);
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> updateUserProfile());
        btnClose.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void updateUserProfile() {
        String newName = editName.getText().toString().trim();
        String newPassword = editPassword.getText().toString().trim();

        Profile updatedProfile = new Profile(userId, newName,
                "https://res.cloudinary.com/dndmj9oid/image/upload/v1739458733/B_Ray640x640_downloaded_from_SpotiSongDownloader.com__cthtmp.jpg",
                newPassword);

        apiService.updateUserProfile(userId, updatedProfile).enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (isAdded() && response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onProfileUpdated();
                    }
                    if (getFragmentManager() != null) {
                        getFragmentManager().popBackStack();
                    }
                } else if (isAdded()) {
                    Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}