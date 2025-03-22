package com.example.spotifyclone.features.artist.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
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
import com.example.spotifyclone.shared.ui.DominantColorExtractor;
import com.google.android.material.button.MaterialButton;

public class ArtistFragment extends Fragment {
    private RecyclerView rv_popular_songs, rv_albums, rv_playlists, rv_similar_artists;
    private Context context;
    private ImageButton btnBack;
    private TextView tv_artist_name, tv_artist_info, tv_monthly_listeners, participant_artist_detail, artist_name, tv_playlist_title_artist_detail;
    private ImageView img_artist_artist_detail, img_artist_cover, img_playlist_artist_detail, img_album_artist_detail, btn_artist_detail_ui_background;
    private ScrollView scrollView;
    private ConstraintLayout artist_detail_info_container;
    private MaterialButton btnSeeSongs, btnHideSongs;
    private RelativeLayout navbar_artist_UI;
    private String artistId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        return inflater.inflate(R.layout.fragment_artist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       if(getArguments() != null) {
           artistId = getArguments().getString("artistId");
       }

         // Initialize views
        initializeViews(view);
        setupListeners();
        setupViewModels();
        setupScrollListener();
    }

    private void initializeViews(View view) {
        rv_popular_songs = view.findViewById(R.id.rv_popular_songs);
        rv_popular_songs.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        rv_albums = view.findViewById(R.id.rv_albums);
        rv_albums.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        rv_playlists = view.findViewById(R.id.rv_playlists);
        rv_playlists.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        rv_similar_artists = view.findViewById(R.id.rv_similar_artists);
        rv_similar_artists.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        tv_monthly_listeners = view.findViewById(R.id.tv_monthly_listeners);
        tv_monthly_listeners.setText("100000000 " + getString(R.string.monthly_listeners));

        artist_name = view.findViewById(R.id.artist_name);
        tv_artist_name = view.findViewById(R.id.tv_artist_name);
        tv_artist_info = view.findViewById(R.id.tv_artist_info);
        participant_artist_detail = view.findViewById(R.id.participant_artist_detail);

        img_artist_cover = view.findViewById(R.id.img_artist_cover);
        img_artist_artist_detail = view.findViewById(R.id.img_artist_artist_detail);
        img_playlist_artist_detail = view.findViewById(R.id.img_playlist_artist_detail);
        img_album_artist_detail = view.findViewById(R.id.img_album_artist_detail);

        scrollView = view.findViewById(R.id.artist_detail_scrollview);
        navbar_artist_UI = view.findViewById(R.id.navbar_artist_UI);

        btnBack = view.findViewById(R.id.btn_artist_detail_ui_back);
        btn_artist_detail_ui_background = view.findViewById(R.id.btn_artist_detail_ui_background);

        btnSeeSongs = view.findViewById(R.id.btn_see_all_songs);
        btnHideSongs = view.findViewById(R.id.btn_hide_songs);

        artist_detail_info_container = view.findViewById(R.id.artist_detail_info_container);
        tv_playlist_title_artist_detail = view.findViewById(R.id.tv_playlist_title_artist_detail);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Set up See More/Hide functionality
        btnSeeSongs.setOnClickListener(v -> {
            ViewGroup.LayoutParams params = rv_popular_songs.getLayoutParams();
            params.height = (int) (650 * getResources().getDisplayMetrics().density);
            rv_popular_songs.setLayoutParams(params);

            btnSeeSongs.setVisibility(View.GONE);
            btnHideSongs.setVisibility(View.VISIBLE);
        });

        btnHideSongs.setOnClickListener(v -> {
            ViewGroup.LayoutParams params = rv_popular_songs.getLayoutParams();
            // Hardcoded height of 300dp converted to pixels
            params.height = (int) (300 * getResources().getDisplayMetrics().density);
            rv_popular_songs.setLayoutParams(params);

            btnSeeSongs.setVisibility(View.VISIBLE);
            btnHideSongs.setVisibility(View.GONE);
        });
    }

    private void setupViewModels() {
        // Artist List ViewModel for song, album, playlist and similar artists
        ArtistListViewModel artistListViewModel = new ViewModelProvider(this).get(ArtistListViewModel.class);
        artistListViewModel.getArtists().observe(getViewLifecycleOwner(), artists -> {
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

        // Artist Overall ViewModel for main artist details
        ArtistOverallViewModel artistViewModel = new ViewModelProvider(requireActivity(),
                new ArtistOverallViewModel.Factory(requireActivity().getApplication(), artistId))
                .get(ArtistOverallViewModel.class);

        artistViewModel.getArtist().observe(getViewLifecycleOwner(), data -> {
            tv_artist_name.setText(data.getName());
            artist_name.setText(data.getName());
            tv_artist_info.setText(data.getDescription());
            participant_artist_detail.setText(getString(R.string.participant_text) + data.getName());

            Glide.with(this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_album_artist_detail);

            Glide.with(this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_artist_cover);

            Glide.with(this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_artist_artist_detail);

            artist_detail_info_container.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), ArtistOverallActivity.class);
                intent.putExtra("ARTIST_ID", data.getId());
                startActivity(intent);
            });

            DominantColorExtractor.getDominantColor(context, data.getAvatarUrl(), color -> {
                navbar_artist_UI.setBackgroundColor(color);
            });
        });
        artistViewModel.fetchArtistDetails();

        // Artist Overall ViewModel for playlist details
        ArtistOverallViewModel playlistArtist = new ViewModelProvider(requireActivity(),
                new ArtistOverallViewModel.Factory(requireActivity().getApplication(), artistId))
                .get(ArtistOverallViewModel.class);

        playlistArtist.getArtist().observe(getViewLifecycleOwner(), data -> {
            Glide.with(this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_playlist_artist_detail);
            tv_playlist_title_artist_detail.setText(data.getName());
        });
        playlistArtist.fetchArtistDetails();
    }

    private void setupScrollListener() {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (!isAdded()) return;

                int scrollY = scrollView.getScrollY();
                float nameHeight = tv_artist_name.getHeight();

                Context context = getContext();
                if (context != null) {
                    float threshold = (40 * context.getResources().getDisplayMetrics().density) + (nameHeight * 0.7f);

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
            }
        });
    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (scrollView != null) {
//            scrollView.getViewTreeObserver().removeOnScrollChangedListener(scrollListener);
//        }
//    }

}