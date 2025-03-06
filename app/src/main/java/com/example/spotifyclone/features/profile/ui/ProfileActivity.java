package com.example.spotifyclone.features.profile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.profile.network.apiProfileService;
import com.example.spotifyclone.features.profile.model.Profile;
import com.example.spotifyclone.features.profile.network.profileRetrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName, followDisc;
    private ImageView userAva;
    private ImageButton btnBack;
    private Button btnEdit;
    private Profile data;

    private final ActivityResultLauncher<Intent> editProfileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    fetchUserProfile();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userName = findViewById(R.id.user_name);
        followDisc = findViewById(R.id.follow_info);
        userAva = findViewById(R.id.artist_logo);
        btnBack = findViewById(R.id.back_button);
        btnEdit = findViewById(R.id.edit_button);

        btnBack.setOnClickListener(v -> finish());
        btnEdit.setOnClickListener(v -> openEditProfile());

        fetchUserProfile();
    }

    private void fetchUserProfile() {
        Retrofit retrofit = profileRetrofit.getClient();
        apiProfileService apiService = retrofit.create(apiProfileService.class);

        String userId = getIntent().getStringExtra("USER_ID");
        if (userId == null) {
            Toast.makeText(this, "Invalid User ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<Profile> call = apiService.getUserProfile(userId);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data = response.body();
                    userName.setText(data.getName());
                    followDisc.setText(data.getFollower() +" "+getString(R.string.fler)+ " • "+ getString(R.string.fl)+" "+ data.getFollow());

                    Glide.with(ProfileActivity.this)
                            .load(data.getAvatarUrl())
                            .placeholder(R.drawable.loading)
                            .into(userAva);
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openEditProfile() {
        if (data == null) {
            Toast.makeText(this, "User data is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("USER_ID", data.getId());
        intent.putExtra("USER_NAME", data.getName());
        intent.putExtra("USER_PASSWORD", data.getPassword());
        intent.putExtra("USER_AVATAR", data.getAvatarUrl());

        editProfileLauncher.launch(intent);
    }
}
