package com.example.spotifyclone.features.artist.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.artist.model.artistDetail;
import com.example.spotifyclone.features.artist.viewModel.ArtistOverallViewModel;

public class ArtistOverallUI extends AppCompatActivity {

    private TextView artistName, artistDescription, postAuthor;
    private ImageView artistImage, artistLogo;
    private ImageButton btnBack;
    private Context context;
    private ArtistOverallViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_detail);

        context = this;
        initViews();
        setupBackButton();

        String artistId = getIntent().getStringExtra("ARTIST_ID");
        if (artistId == null || artistId.isEmpty()) {
            Toast.makeText(context, "Invalid Artist ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this,
                new ArtistOverallViewModel.Factory(getApplication(), artistId))
                .get(ArtistOverallViewModel.class);

        viewModel.getArtist().observe(this, this::updateUI);
        viewModel.fetchArtistDetails();
    }

    private void initViews() {
        artistName = findViewById(R.id.artist_name);
        artistDescription = findViewById(R.id.artist_description);
        postAuthor = findViewById(R.id.post_author);
        artistImage = findViewById(R.id.artist_image);
        artistLogo = findViewById(R.id.artist_logo);
        btnBack = findViewById(R.id.back_button);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void updateUI(artistDetail data) {
        if (data == null) return;

        artistName.setText(data.getName());
        artistDescription.setText(data.getDescription());
        postAuthor.setText(getString(R.string.post_by) + data.getName());

        loadImage(artistImage, data.getAvatarUrl());
        loadImage(artistLogo, data.getAvatarUrl());
    }

    private void loadImage(ImageView imageView, String url) {
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageView);
    }
}