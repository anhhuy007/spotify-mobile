package com.example.spotifyclone.features.topproduct.ui;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.artist.adapter.ArtistAdapter;
import com.example.spotifyclone.features.artist.viewModel.ArtistListViewModel;
import com.example.spotifyclone.features.topproduct.apdaper.AlsoLikeAdapter;
import com.example.spotifyclone.features.topproduct.apdaper.TopSongAdapter;
import com.example.spotifyclone.features.topproduct.viewModel.AlsoLikeViewModel;
import com.example.spotifyclone.features.topproduct.viewModel.TopSongViewModel;

public class TopSongActivity extends AppCompatActivity {
    private RecyclerView rvTop, rvAlsoLike;
    private Context context;
    private ImageView btnBack;
    private TopSongAdapter topProductAdapter;
    private AlsoLikeAdapter alsoLikeAdapter;
    private TopSongViewModel topProductViewModel;
    private AlsoLikeViewModel alsoLikeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_product);

        context = this;
        initViews();
        rvTop.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvAlsoLike.setLayoutManager(new GridLayoutManager(this, 2));
        setupBackButton();

        topProductViewModel = new ViewModelProvider(this).get(TopSongViewModel.class);
        topProductViewModel.getTopProduct().observe(this, artists -> {
            if (artists != null) {
                topProductAdapter = new TopSongAdapter(this,artists);
                rvTop.setAdapter(topProductAdapter);
            }
        });
        topProductViewModel.fetchItems();

        alsoLikeViewModel = new ViewModelProvider(this).get(AlsoLikeViewModel.class);
        alsoLikeViewModel.getTopProduct().observe(this, artists -> {
            if (artists != null) {
                alsoLikeAdapter = new AlsoLikeAdapter(this,artists);
                rvAlsoLike.setAdapter(alsoLikeAdapter);
            }
        });
        alsoLikeViewModel.fetchItems();
    }

    private void initViews() {
        rvTop = findViewById(R.id.rv_albums_top);
        rvAlsoLike = findViewById(R.id.rv_also_like_top);
        btnBack = findViewById(R.id.btn_artist_detail_ui_back_top);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }
}