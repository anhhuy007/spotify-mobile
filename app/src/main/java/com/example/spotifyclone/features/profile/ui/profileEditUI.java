package com.example.spotifyclone.features.profile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class profileEditUI extends AppCompatActivity {

    private EditText editName, editPassword;
    private ImageView avatar;
    private ImageButton btnClose;
    private Button btnSave;
    private String userId;
    private apiProfileService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editName = findViewById(R.id.display_name);
        editPassword = findViewById(R.id.display_password);
        avatar = findViewById(R.id.artist_logo);
        btnClose = findViewById(R.id.close_icon);
        btnSave = findViewById(R.id.btn_save);

        userId = getIntent().getStringExtra("USER_ID");
        editName.setText(getIntent().getStringExtra("USER_NAME"));
        editPassword.setText(getIntent().getStringExtra("USER_PASSWORD"));

        Glide.with(this)
                .load(getIntent().getStringExtra("USER_AVATAR"))
                .placeholder(R.drawable.loading)
                .into(avatar);

        Retrofit retrofit = profileRetrofit.getClient();
        apiService = retrofit.create(apiProfileService.class);

        btnSave.setOnClickListener(v -> updateUserProfile());
        btnClose.setOnClickListener(v -> finish());
    }

    private void updateUserProfile() {
        String newName = editName.getText().toString().trim();
        String newPassword = editPassword.getText().toString().trim();

        profile updatedProfile = new profile(userId, newName, "https://res.cloudinary.com/dndmj9oid/image/upload/v1739458733/B_Ray640x640_downloaded_from_SpotiSongDownloader.com__cthtmp.jpg", newPassword);

        apiService.updateUserProfile(userId, updatedProfile).enqueue(new Callback<profile>() {
            @Override
            public void onResponse(Call<profile> call, Response<profile> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(profileEditUI.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(profileEditUI.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<profile> call, Throwable t) {
                Toast.makeText(profileEditUI.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}