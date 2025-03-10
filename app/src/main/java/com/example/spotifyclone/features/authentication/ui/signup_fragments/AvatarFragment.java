package com.example.spotifyclone.features.authentication.ui.signup_fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.MainActivity;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.model.UploadAvatarResponse;
import com.example.spotifyclone.features.authentication.ui.SignupActivity;
import com.example.spotifyclone.features.authentication.viewmodel.AuthVMFactory;
import com.example.spotifyclone.features.authentication.viewmodel.AuthViewModel;
import com.example.spotifyclone.features.authentication.viewmodel.SignupVMFactory;
import com.example.spotifyclone.features.authentication.viewmodel.SignupViewModel;
import com.example.spotifyclone.features.profile.network.ProfileService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import java.io.File;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class AvatarFragment extends Fragment {
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 101;
    private CircleImageView avatarPreview;
    private Uri imageUri;
    private TextView txtSkip;

    ActivityResultLauncher<Intent> imageResultLauncher;

    public AvatarFragment() {
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            pickImage(); // If permission is granted, open image picker
        } else {
            Toast.makeText(getContext(), "Permission denied to read external storage", Toast.LENGTH_SHORT).show();
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_avatar, container, false);
        FrameLayout avatarContainer = view.findViewById(R.id.profileImageContainer);
        avatarPreview = view.findViewById(R.id.profileImage);
        Button chooseImageBtn = view.findViewById(R.id.btnChooseImage);
        Button takePhotoBtn = view.findViewById(R.id.btnTakePhoto);
        Button btnContinue = view.findViewById(R.id.btnContinue);
        ImageView backBtn = view.findViewById(R.id.btnBack);
        ImageView closeBtn = view.findViewById(R.id.btnClose);
        txtSkip = view.findViewById(R.id.tvSkip);

        registerImageResultLauncher();

        avatarContainer.setOnClickListener(v -> {
            checkStoragePermissionAndGetImage();
        });

        chooseImageBtn.setOnClickListener(v -> {
            checkStoragePermissionAndGetImage();
        });

        btnContinue.setOnClickListener(v -> {
            ((SignupActivity) requireActivity()).signupViewModel.setAvatarUri(imageUri != null ? imageUri.toString() : "");
            ((SignupActivity) requireActivity()).nextStep();
        });

        backBtn.setOnClickListener(v -> {
            ((SignupActivity) requireActivity()).previousStep();
        });

        closeBtn.setOnClickListener(v -> {
            ((SignupActivity) requireActivity()).closeSignup();
        });

        txtSkip.setOnClickListener(v -> {
            ((SignupActivity) requireActivity()).signupViewModel.setAvatarUri("");
            ((SignupActivity) requireActivity()).nextStep();
        });

        return view;
    }

    private void registerImageResultLauncher() {
        imageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                try {
                    imageUri = o.getData().getData();
                    avatarPreview.setImageURI(imageUri);
                } catch (Exception e) {
                    Log.e("ERROR", "Failed to load image: " + e.getMessage());
                }
            }
        });
    }

    private void pickImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        imageResultLauncher.launch(galleryIntent);
    }

    private void checkStoragePermissionAndGetImage() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            pickImage();
        }
    }
}
