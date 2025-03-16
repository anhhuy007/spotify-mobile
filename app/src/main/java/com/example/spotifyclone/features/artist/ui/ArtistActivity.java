package com.example.spotifyclone.features.artist.ui;

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
import com.example.spotifyclone.shared.ui.DominantColorExtractor;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.atomic.AtomicInteger;

public class ArtistActivity extends AppCompatActivity {
    private RecyclerView rv_popular_songs, rv_albums, rv_playlists, rv_similar_artists;
    private Context context;
    private ImageButton btnBack;
    private TextView tv_artist_name, tv_artist_info, tv_monthly_listeners, participant_artist_detail, artist_name, tv_playlist_title_artist_detail;
    private ImageView img_artist_artist_detail, img_artist_cover, img_playlist_artist_detail, img_album_artist_detail,btn_artist_detail_ui_background;
    private ScrollView scrollView;
    private ConstraintLayout artist_detail_info_container;
    private MaterialButton btnSeeSongs, btnHideSongs, btn_see_view_discography;
    private RelativeLayout navbar_artist_UI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail_ui);
        context = this;

        String artistId = getIntent().getStringExtra("ARTIST_ID");
        if (artistId == null || artistId.isEmpty()) {
            Toast.makeText(context, "Invalid Artist ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize views
        rv_popular_songs = findViewById(R.id.rv_popular_songs);
        rv_popular_songs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        rv_albums = findViewById(R.id.rv_albums);
        rv_albums.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        rv_playlists = findViewById(R.id.rv_playlists);
        rv_playlists.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rv_similar_artists = findViewById(R.id.rv_similar_artists);
        rv_similar_artists.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        tv_monthly_listeners = findViewById(R.id.tv_monthly_listeners);
        tv_monthly_listeners.setText("100000000 " + getString(R.string.monthly_listeners));

        artist_name = findViewById(R.id.artist_name);
        tv_artist_name = findViewById(R.id.tv_artist_name);
        img_artist_cover = findViewById(R.id.img_artist_cover);
        scrollView = findViewById(R.id.artist_detail_scrollview);
        navbar_artist_UI = findViewById(R.id.navbar_artist_UI);

        btnBack = findViewById(R.id.btn_artist_detail_ui_back);
        btn_artist_detail_ui_background = findViewById(R.id.btn_artist_detail_ui_background);
        img_album_artist_detail = findViewById(R.id.img_album_artist_detail);
        btn_see_view_discography = findViewById(R.id.btn_see_view_discography);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_see_view_discography.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArtistActivity.this, DiscographyActivity.class);
                intent.putExtra("ARTIST_ID", artistId);
                startActivity(intent);
            }
        });

        AtomicInteger sizeHide = new AtomicInteger();

        PopularViewModel artistListViewModel =  new ViewModelProvider(this,
                new PopularViewModel.Factory(getApplication(), artistId))
                .get(PopularViewModel.class);
        artistListViewModel.getListDiscography().observe(this, artists -> {
            if (artists != null) {
                SongArtistAdapter rvPopularSongsAdapter = new SongArtistAdapter(context, artists);
                rv_popular_songs.setAdapter(rvPopularSongsAdapter);

                ViewGroup.LayoutParams params = rv_popular_songs.getLayoutParams();
                params.height = (int) (64 * getResources().getDisplayMetrics().density*artists.size()*11/20);
                rv_popular_songs.setLayoutParams(params);
                sizeHide.set(artists.size());
            }
        });
        artistListViewModel.fetchItems();


        ListDiscographyEPViewModel artistListViewModel2 = new ViewModelProvider(this,
                new ListDiscographyEPViewModel.Factory(getApplication(), artistId,1))
                .get(ListDiscographyEPViewModel.class);
        artistListViewModel2.getListDiscography().observe(this, artists -> {
            if (artists != null) {
                AlbumArtistAdapter rvAlbumsAdapter = new AlbumArtistAdapter(context, artists);
                rv_albums.setAdapter(rvAlbumsAdapter);
            }
        });
        artistListViewModel2.fetchItems();

        ListDiscographyAlbumViewModel artistListViewModel3 = new ViewModelProvider(this,
                new ListDiscographyAlbumViewModel.Factory(getApplication(), artistId,1))
                .get(ListDiscographyAlbumViewModel.class);
        artistListViewModel3.getListDiscography().observe(this, artists -> {
            if (artists != null) {
                ArtistPlaylistAdapter rvPlaylistsAdapter = new ArtistPlaylistAdapter(context, artists);
                rv_playlists.setAdapter(rvPlaylistsAdapter);
            }
        });
        artistListViewModel3.fetchItems();

        FansAlsoLikeViewModel artistListViewModel4 =new ViewModelProvider(this,
                new FansAlsoLikeViewModel.Factory(getApplication(), artistId))
                .get(FansAlsoLikeViewModel.class);
        artistListViewModel4.getListDiscography().observe(this, artists -> {
            if (artists != null) {

                ArtistSimilarAdapter rvSimilarArtistsAdapter = new ArtistSimilarAdapter(context, artists);
                rv_similar_artists.setAdapter(rvSimilarArtistsAdapter);
            }
        });
        artistListViewModel4.fetchItems();

        ArtistOverallViewModel artistViewModel = new ViewModelProvider(this,
                new ArtistOverallViewModel.Factory(getApplication(), artistId))
                .get(ArtistOverallViewModel.class);
        artistViewModel.getArtist().observe(this, data -> {
            tv_artist_name.setText(data.getName());
            artist_name.setText(data.getName());
            tv_artist_info = findViewById(R.id.tv_artist_info);
            tv_artist_info.setText(data.getDescription());
            participant_artist_detail = findViewById(R.id.participant_artist_detail);
            participant_artist_detail.setText(getString(R.string.participant_text) + data.getName());

            Glide.with(ArtistActivity.this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_album_artist_detail);

            Glide.with(ArtistActivity.this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_artist_cover);

            img_artist_artist_detail = findViewById(R.id.img_artist_artist_detail);
            Glide.with(ArtistActivity.this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_artist_artist_detail);

            artist_detail_info_container = findViewById(R.id.artist_detail_info_container);
            artist_detail_info_container.setOnClickListener(v -> {
                Intent intent = new Intent(ArtistActivity.this, ArtistOverallActivity.class);
                intent.putExtra("ARTIST_ID", data.getId());
                startActivity(intent);
            });

            DominantColorExtractor.getDominantColor(context, data.getAvatarUrl(), color -> {
                navbar_artist_UI.setBackgroundColor(color);
            });
        });
        artistViewModel.fetchArtistDetails();

        ArtistOverallViewModel playlistArtist = new ViewModelProvider(this,
                new ArtistOverallViewModel.Factory(getApplication(), artistId))
                .get(ArtistOverallViewModel.class);
        playlistArtist.getArtist().observe(this, data -> {
            tv_playlist_title_artist_detail = findViewById(R.id.tv_playlist_title_artist_detail);
            img_playlist_artist_detail = findViewById(R.id.img_playlist_artist_detail);
            Glide.with(ArtistActivity.this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_playlist_artist_detail);
            tv_playlist_title_artist_detail.setText(data.getName());
        });
        playlistArtist.fetchArtistDetails();

        btnSeeSongs = findViewById(R.id.btn_see_all_songs);
        btnHideSongs = findViewById(R.id.btn_hide_songs);

        // Set up See More/Hide functionality
        btnSeeSongs.setOnClickListener(v -> {
            ViewGroup.LayoutParams params = rv_popular_songs.getLayoutParams();
            params.height = (int) (64 * getResources().getDisplayMetrics().density*sizeHide.get());
            rv_popular_songs.setLayoutParams(params);

            btnSeeSongs.setVisibility(View.GONE);
            btnHideSongs.setVisibility(View.VISIBLE);
        });

        btnHideSongs.setOnClickListener(v -> {
            ViewGroup.LayoutParams params = rv_popular_songs.getLayoutParams();
            params.height = (int) (64 * getResources().getDisplayMetrics().density*sizeHide.get()*11/20);
            rv_popular_songs.setLayoutParams(params);

            btnSeeSongs.setVisibility(View.VISIBLE);
            btnHideSongs.setVisibility(View.GONE);
        });




        // Set up scroll listener for fading in the top bar artist name
        setupScrollListener();
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