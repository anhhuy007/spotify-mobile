package com.example.spotifyclone.features.artist.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class ArtistUI extends AppCompatActivity {
    private RecyclerView rv_popular_songs,rv_albums,rv_playlists,rv_similar_artists;
    private Context context;
    private ImageButton btnBack;
    private TextView tv_artist_name,tv_artist_info;
    private ImageView img_artist_artist_detal,img_artist_cover;

    private ConstraintLayout artist_detail_info_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_detail_ui);
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

        ArtistOverallViewModel viewModel = new ViewModelProvider(this,
                new ArtistOverallViewModel.Factory(getApplication(), artistId))
                .get(ArtistOverallViewModel.class);

        viewModel.getArtist().observe(this, data -> {
            tv_artist_name = findViewById(R.id.tv_artist_name);
            tv_artist_name.setText(data.getName());
            tv_artist_info = findViewById(R.id.tv_artist_info);
            tv_artist_info.setText(data.getDescription());

            img_artist_cover = findViewById(R.id.img_artist_cover);
            Glide.with(ArtistUI.this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(img_artist_cover);
            img_artist_artist_detal = findViewById(R.id.img_artist_artist_detal);
            Glide.with(ArtistUI.this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(img_artist_artist_detal);

            artist_detail_info_container = findViewById(R.id.artist_detail_info_container);
            artist_detail_info_container.setOnClickListener(v -> {
                Intent intent = new Intent(context, ArtistOverallUI.class);
                intent.putExtra("ARTIST_ID", "1");
                startActivity(intent);
            });
        });
        viewModel.fetchArtistDetails();


    }
}