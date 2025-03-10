package com.example.spotifyclone.features.profile.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.profile.viewmodel.ProfileVMFactory;
import com.example.spotifyclone.features.profile.viewmodel.ProfileViewModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView txtName, txtEmail, txtIsPremium, txtTheme;
    private Button btnLogout;

    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profile_image);
        txtName = findViewById(R.id.tv_username);
        txtEmail = findViewById(R.id.tv_email);
        txtIsPremium = findViewById(R.id.tv_premium);
        txtTheme = findViewById(R.id.tv_theme);
        btnLogout = findViewById(R.id.btn_logout);

        profileViewModel = new ViewModelProvider(this, new ProfileVMFactory(this)).get(ProfileViewModel.class);
        profileViewModel.getUserLiveData().observe(this, user -> {

            if (user == null) {
                finish();
                return;
            }

            txtName.setText(user.getUsername());
            txtEmail.setText(user.getEmail());
            txtIsPremium.setText(user.isPremium() ? "Premium" : "Free");
            txtTheme.setText(user.getTheme());

            // Set profile image
            Glide.with(this)
                    .load(user.getAvatarUrl())
                    .placeholder(R.drawable.profile_placeholder)
                    .into(profileImage);
        });

        btnLogout.setOnClickListener(v -> {
            // Logout user
            profileViewModel.logout();
            finish();
        });

    }
}