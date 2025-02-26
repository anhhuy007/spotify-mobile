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
import com.example.spotifyclone.features.profile.model.apiProfileService;
import com.example.spotifyclone.features.profile.model.profile;
import com.example.spotifyclone.features.profile.model.profileRetrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class profileUI extends AppCompatActivity {

    private TextView userName, followDisc;
    private ImageView userAva;
    private ImageButton btnBack;
    private Button btnEdit;
    private profile data;

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

        int userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId <= 0) {
            Toast.makeText(this, "Invalid User ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<profile> call = apiService.getUserProfile(userId);
        call.enqueue(new Callback<profile>() {
            @Override
            public void onResponse(Call<profile> call, Response<profile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data = response.body();
                    userName.setText(data.getName());
                    followDisc.setText(data.getFollower() + " người theo dõi • Đang theo dõi " + data.getFollow());

                    Glide.with(profileUI.this)
                            .load(data.getAvatarUrl())
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(userAva);
                } else {
                    Toast.makeText(profileUI.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<profile> call, Throwable t) {
                Toast.makeText(profileUI.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openEditProfile() {
        if (data == null) {
            Toast.makeText(this, "User data is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, profileEditUI.class);
        intent.putExtra("USER_ID", data.getId());
        intent.putExtra("USER_NAME", data.getName());
        intent.putExtra("USER_PASSWORD", data.getPassword());
        intent.putExtra("USER_AVATAR", data.getAvatarUrl());

        editProfileLauncher.launch(intent);
    }
}
