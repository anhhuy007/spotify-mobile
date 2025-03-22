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
import com.example.spotifyclone.features.profile.model.Profile;
import com.example.spotifyclone.features.profile.model.PasswordUpdate;
import com.example.spotifyclone.features.profile.model.ProfileUpdate;
import com.example.spotifyclone.features.profile.network.ProfileService;
import com.example.spotifyclone.shared.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditProfileFragment extends Fragment {

    private EditText editName, editPassword, editOldPassword;
    private ImageView avatar;
    private ImageButton btnClose;
       private     ImageView btnEditAvatar;
    private Button btnSave;
    private String userId;
    private String userName;
    private String userAvatar;
    private ProfileService apiService;
    private ProfileUpdateListener listener;

    public interface ProfileUpdateListener {
        void onProfileUpdated();
    }

    public EditProfileFragment() {
        // Required empty public constructor
    }

    public static EditProfileFragment newInstance(String userId, String userName, String userAvatar) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString("USER_ID", userId);
        args.putString("USER_NAME", userName);
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
            userAvatar = getArguments().getString("USER_AVATAR");
        }

        Retrofit retrofit = RetrofitClient.getClient(getActivity());
        apiService = retrofit.create(ProfileService.class);
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
        editOldPassword = view.findViewById(R.id.display_old_password);
        editPassword = view.findViewById(R.id.display_password);
        avatar = view.findViewById(R.id.artist_logo);
        btnClose = view.findViewById(R.id.close_icon);
        btnSave = view.findViewById(R.id.btn_save);
        btnEditAvatar = view.findViewById(R.id.edit_avatar);
    }

    private void setupData() {
        editName.setText(userName);

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

        btnEditAvatar.setOnClickListener(v -> {
            // Handle avatar selection
            // This would typically launch an image picker
        });
    }

    private void updateUserProfile() {
        String newName = editName.getText().toString().trim();
        String oldPassword = editOldPassword.getText().toString().trim();
        String newPassword = editPassword.getText().toString().trim();

        // Update profile info (username and avatar)
        updateProfileInfo(newName);

        // Update password if provided
        if (!oldPassword.isEmpty() && !newPassword.isEmpty()) {
            updatePassword(oldPassword, newPassword);
        }
    }

    private void updateProfileInfo(String newName) {
        ProfileUpdate profileUpdate = new ProfileUpdate(newName, userAvatar);

        apiService.updateProfile(profileUpdate).enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (isAdded() && response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onProfileUpdated();
                    }
                } else if (isAdded()) {
                    Toast.makeText(requireContext(), "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
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

    private void updatePassword(String oldPassword, String newPassword) {
        PasswordUpdate passwordUpdate = new PasswordUpdate(oldPassword, newPassword);

        apiService.updatePassword(passwordUpdate).enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (isAdded() && response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
                } else if (isAdded()) {
                    Toast.makeText(requireContext(), "Cập nhật mật khẩu thất bại", Toast.LENGTH_SHORT).show();
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