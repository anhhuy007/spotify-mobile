package com.example.spotifyclone.features.artist.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.spotifyclone.features.artist.viewModel.ArtistListViewModel;
import com.example.spotifyclone.features.artist.viewModel.ArtistOverallViewModel;
import com.google.android.material.button.MaterialButton;

public class ArtistActivity extends AppCompatActivity {
    private RecyclerView rv_popular_songs,rv_albums,rv_playlists,rv_similar_artists;
    private Context context;
    private ImageButton btnBack;
    private TextView tv_artist_name,tv_artist_info,tv_monthly_listeners,participant_artist_detail,artist_name,tv_playlist_title_artist_detail;
    private ImageView img_artist_artist_detal,img_artist_cover,img_playlist_artist_detail,img_album_artist_detail;

    private ConstraintLayout artist_detail_info_container;
    private MaterialButton btnSeeSongs, btnHideSongs;

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

        rv_popular_songs = findViewById(R.id.rv_popular_songs);
        rv_popular_songs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        rv_albums = findViewById(R.id.rv_albums);
        rv_albums.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        rv_playlists = findViewById(R.id.rv_playlists);
        rv_playlists.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rv_similar_artists = findViewById(R.id.rv_similar_artists);
        rv_similar_artists.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tv_monthly_listeners = findViewById(R.id.tv_monthly_listeners);
        tv_monthly_listeners.setText("100000000 "+getString(R.string.monthly_listeners));


        btnBack = findViewById(R.id.btn_artist_detail_ui_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ArtistListViewModel artistListViewModel = new ViewModelProvider(this).get(ArtistListViewModel.class);
        artistListViewModel.getArtists().observe(this, artists -> {
            if (artists != null) {
                SongArtistAdapter rvPopularSongsAdapter = new SongArtistAdapter(context, artists);
                rv_popular_songs.setAdapter(rvPopularSongsAdapter);
                AlbumArtistAdapter rvAlbumsAdapter = new AlbumArtistAdapter(context, artists);
                rv_albums.setAdapter(rvAlbumsAdapter);
                ArtistPlaylistAdapter rvPlaylistsAdapter = new ArtistPlaylistAdapter(context, artists);
                rv_playlists.setAdapter(rvPlaylistsAdapter);
                ArtistSimilarAdapter rvSimilarArtistsAdapter = new ArtistSimilarAdapter(context, artists);
                rv_similar_artists.setAdapter(rvSimilarArtistsAdapter);
            }
        });
        artistListViewModel.fetchItems();

        ArtistOverallViewModel artistViewModel = new ViewModelProvider(this,
                new ArtistOverallViewModel.Factory(getApplication(), artistId))
                .get(ArtistOverallViewModel.class);
        artistViewModel.getArtist().observe(this, data -> {
            tv_artist_name = findViewById(R.id.tv_artist_name);
            tv_artist_name.setText(data.getName());
            artist_name = findViewById(R.id.artist_name);
            artist_name.setText(data.getName());
            tv_artist_info = findViewById(R.id.tv_artist_info);
            tv_artist_info.setText(data.getDescription());
            participant_artist_detail = findViewById(R.id.participant_artist_detail);
            participant_artist_detail.setText(getString(R.string.participant_text)+data.getName());
            img_album_artist_detail = findViewById(R.id.img_album_artist_detail);
            Glide.with(ArtistActivity.this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_album_artist_detail);
            img_artist_cover = findViewById(R.id.img_artist_cover);
            Glide.with(ArtistActivity.this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_artist_cover);
            img_artist_artist_detal = findViewById(R.id.img_artist_artist_detail);
            Glide.with(ArtistActivity.this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_artist_artist_detal);

            artist_detail_info_container = findViewById(R.id.artist_detail_info_container);
            artist_detail_info_container.setOnClickListener(v -> {
                Intent intent = new Intent(ArtistActivity.this, ArtistOverallActivity.class);
                intent.putExtra("ARTIST_ID", data.getId());
                startActivity(intent);
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
//            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = (int) (650 * getResources().getDisplayMetrics().density);

            rv_popular_songs.setLayoutParams(params);

            btnSeeSongs.setVisibility(View.GONE);
            btnHideSongs.setVisibility(View.VISIBLE);
        });

        btnHideSongs.setOnClickListener(v -> {
            ViewGroup.LayoutParams params = rv_popular_songs.getLayoutParams();
            // Hardcoded height of 280dp converted to pixels
            params.height = (int) (300 * getResources().getDisplayMetrics().density);
            rv_popular_songs.setLayoutParams(params);

            btnSeeSongs.setVisibility(View.VISIBLE);
            btnHideSongs.setVisibility(View.GONE);
        });

    }
}


