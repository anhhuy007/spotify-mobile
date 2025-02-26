package com.example.spotifyclone.features.artist.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.artist.model.apiArtistService;
import com.example.spotifyclone.features.artist.model.artist;
import com.example.spotifyclone.features.artist.model.artistRetrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class artistDetailUI extends AppCompatActivity {

    private TextView artistName, artistDescription, postAuthor;
    private ImageView artistImage,artistLogo;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_detail);

        artistName = findViewById(R.id.artist_name);
        artistDescription = findViewById(R.id.artist_description);
        postAuthor = findViewById(R.id.post_author);
        artistImage = findViewById(R.id.artist_image);
        artistLogo = findViewById(R.id.artist_logo);
        btnBack = findViewById(R.id.back_button);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fetchItems();
    }

    private void fetchItems() {
        Retrofit retrofit = artistRetrofit.getClient();
        apiArtistService apiService = retrofit.create(apiArtistService.class);

        String artistId = getIntent().getStringExtra("ARTIST_ID");
        if (artistId == null || artistId.isEmpty()) {
            Toast.makeText(artistDetailUI.this, "Invalid Artist ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<artist> call = apiService.getArtistDetail(artistId);
        call.enqueue(new Callback<artist>() {
            @Override
            public void onResponse(Call<artist> call, Response<artist> response) {
                if (response.isSuccessful() && response.body() != null) {
                    artist data = response.body();

                    artistName.setText(data.getName());
                    artistDescription.setText(data.getDescription());
                    postAuthor.setText("Đăng bởi " + data.getName());

                    Glide.with(artistDetailUI.this)
                            .load(data.getAvatarUrl())
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(artistImage);
                    Glide.with(artistDetailUI.this)
                            .load(data.getAvatarUrl())
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(artistLogo);
                } else {
                    Toast.makeText(artistDetailUI.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<artist> call, Throwable t) {
                Toast.makeText(artistDetailUI.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
