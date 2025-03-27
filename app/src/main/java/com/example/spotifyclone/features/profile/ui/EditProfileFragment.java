package com.example.spotifyclone.features.profile.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.features.profile.viewmodel.ProfileViewModel;
import com.example.spotifyclone.shared.model.User;

public class EditProfileFragment extends Fragment {

    private EditText editName, editPassword, editOldPassword;
    private ImageView avatar;
    private ImageButton btnClose;
    private ImageView btnEditAvatar;
    private Button btnSave, btnSavePass;
    private ProfileViewModel profileViewModel;
    private Uri selectedImageUri;

    private ActivityResultLauncher<Intent> galleryLauncher;

    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            // TODO: Implement avatar upload logic
                            Glide.with(requireContext())
                                    .load(selectedImageUri)
                                    .placeholder(R.drawable.loading)
                                    .into(avatar);
                        }
                    }
                }
        );
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
        editPassword = view.findViewById(R.id.display_new_password);
        avatar = view.findViewById(R.id.artist_logo);
        btnClose = view.findViewById(R.id.close_icon);
        btnSave = view.findViewById(R.id.btn_save);
        btnEditAvatar = view.findViewById(R.id.edit_avatar);
        btnSavePass = view.findViewById(R.id.btn_save_pass);
    }

    private void setupData() {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        AuthRepository authRepo = new AuthRepository(getContext());
        User user = authRepo.getUser();

        if (user != null) {
            editName.setText(user.getUsername());
            Glide.with(requireContext())
                    .load(user.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(avatar);
        }

        profileViewModel.getUser().observe(getViewLifecycleOwner(), updatedUser -> {
            if (updatedUser != null) {
                editName.setText(updatedUser.getUsername());
                Glide.with(requireContext())
                        .load(updatedUser.getAvatarUrl())
                        .placeholder(R.drawable.loading)
                        .into(avatar);
            }
        });
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> {
            String newName = editName.getText().toString().trim();
            if (TextUtils.isEmpty(newName)) {
                editName.setError("Tên không được để trống");
                return;
            }

            // Use the current avatar URL or the newly selected image URI
            String avatarUrl = selectedImageUri != null ? selectedImageUri.toString() : null;
            profileViewModel.updateProfileInfo(newName, avatarUrl);
        });

        btnSavePass.setOnClickListener(v -> {
            String oldPassword = editOldPassword.getText().toString().trim();
            String newPassword = editPassword.getText().toString().trim();

            if (TextUtils.isEmpty(oldPassword)) {
                editOldPassword.setError("Vui lòng nhập mật khẩu cũ");
                return;
            }

            if (TextUtils.isEmpty(newPassword)) {
                editPassword.setError("Vui lòng nhập mật khẩu mới");
                return;
            }

            profileViewModel.updatePassword(oldPassword, newPassword);
        });

        btnClose.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnEditAvatar.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(galleryIntent);
        });
    }
}