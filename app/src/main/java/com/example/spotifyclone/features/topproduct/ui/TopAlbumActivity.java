package com.example.spotifyclone.features.topproduct.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.artist.adapter.AlbumArtistAdapter;
import com.example.spotifyclone.features.artist.adapter.ArtistPlaylistAdapter;
import com.example.spotifyclone.features.artist.adapter.ArtistSimilarAdapter;
import com.example.spotifyclone.features.artist.adapter.SongArtistAdapter;
import com.example.spotifyclone.features.artist.viewModel.ArtistOverallViewModel;
import com.example.spotifyclone.features.artist.viewModel.FansAlsoLikeViewModel;
import com.example.spotifyclone.features.artist.viewModel.ListDiscographyAlbumViewModel;
import com.example.spotifyclone.features.artist.viewModel.ListDiscographyEPViewModel;
import com.example.spotifyclone.features.artist.viewModel.PopularViewModel;
import com.example.spotifyclone.features.topproduct.apdaper.AlsoLikeAdapter;
import com.example.spotifyclone.features.topproduct.apdaper.TopAlbumAdapter;
import com.example.spotifyclone.features.topproduct.viewModel.AlsoLikeViewModel;
import com.example.spotifyclone.features.topproduct.viewModel.MostAlbumViewModel;
import com.example.spotifyclone.features.topproduct.viewModel.TopAlbumViewModel;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.atomic.AtomicInteger;

public class TopAlbumActivity extends AppCompatActivity {
    private RecyclerView rvTop, rvAlsoLike;
    private Context context;
    private ImageButton btnBack;
    private TextView tv_artist_name, tv_monthly_listeners, artist_name;
    private ImageView img_artist_artist_detail, img_artist_cover, img_album_artist_detail,btn_artist_detail_ui_background;
    private ScrollView scrollView;
    private RelativeLayout navbar_artist_UI;

    private AlsoLikeViewModel alsoLikeViewModel;
    private TopAlbumViewModel topProductViewModel;
    private TopAlbumAdapter topProductAdapter;
    private AlsoLikeAdapter alsoLikeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_product);
        context = this;

        rvTop = findViewById(R.id.rv_albums_top);
        rvAlsoLike = findViewById(R.id.rv_also_like_top);
        rvTop.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        tv_monthly_listeners = findViewById(R.id.tv_monthly_listeners_top);

        artist_name = findViewById(R.id.artist_name_top);
        tv_artist_name = findViewById(R.id.tv_artist_name_top);
        img_artist_cover = findViewById(R.id.img_artist_cover_top);
        scrollView = findViewById(R.id.artist_detail_scrollview_top);
        navbar_artist_UI = findViewById(R.id.navbar_artist_UI_top);

        btnBack = findViewById(R.id.btn_artist_detail_ui_back_top);
        btn_artist_detail_ui_background = findViewById(R.id.btn_artist_detail_ui_background_top);
        img_album_artist_detail = findViewById(R.id.img_album_artist_detail_top);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AtomicInteger sizeHide = new AtomicInteger();

        context = this;
        rvTop.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvAlsoLike.setLayoutManager(new GridLayoutManager(this, 2));
        setupBackButton();

        topProductViewModel = new ViewModelProvider(this).get(TopAlbumViewModel.class);
        topProductViewModel.getTopProduct().observe(this, artists -> {
            if (artists != null) {
                topProductAdapter = new TopAlbumAdapter(this,artists);
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

        MostAlbumViewModel mostViewModel = new ViewModelProvider(this).get(MostAlbumViewModel.class);
        mostViewModel.getArtist().observe(this, data -> {
            tv_artist_name.setText(data.getName());
            artist_name.setText(data.getName());

            Glide.with(TopAlbumActivity.this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_album_artist_detail);

            Glide.with(TopAlbumActivity.this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_artist_cover);

            tv_monthly_listeners.setText(data.getDescription() + getString(R.string.monthly_listeners));


            DominantColorExtractor.getDominantColor(context, data.getAvatarUrl(), color -> {
                navbar_artist_UI.setBackgroundColor(color);
            });
        });
        mostViewModel.fetchArtistDetails();

        setupScrollListener();
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupScrollListener() {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                // Calculate the scroll position
                int scrollY = scrollView.getScrollY();

                // Calculate the threshold when the main artist name text reaches near the top
                // 112dp transparent view + a portion of the artist name height (we want the fade to start before it's completely at the top)
                float nameHeight = tv_artist_name.getHeight();
                float threshold = (40 * getResources().getDisplayMetrics().density) + (nameHeight * 0.7f);

                // Calculate alpha based on scroll position
                float alpha = 0f;
                float alpha2 = 0.5f;
                if (scrollY > threshold) {
                    alpha = Math.min(1f, (scrollY - threshold) / (nameHeight * 0.3f));
                    alpha2 = Math.max(0f, 0.5f - (scrollY - threshold) / (nameHeight * 0.3f));

                }

                navbar_artist_UI.setAlpha(alpha);
                artist_name.setAlpha(alpha);
                btn_artist_detail_ui_background.setAlpha(alpha2);
            }
        });
    }
}