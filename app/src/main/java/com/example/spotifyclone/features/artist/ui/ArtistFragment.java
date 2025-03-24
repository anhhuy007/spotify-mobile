package com.example.spotifyclone.features.artist.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
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

public class ArtistFragment extends Fragment {
    private RecyclerView rv_popular_songs, rv_albums, rv_playlists, rv_similar_artists;
    private Context context;
    private ImageButton btnBack;
    private TextView tv_artist_name, tv_artist_info, tv_monthly_listeners, participant_artist_detail, artist_name, tv_playlist_title_artist_detail;
    private ImageView img_artist_artist_detail, img_artist_cover, img_playlist_artist_detail, img_album_artist_detail, btn_artist_detail_ui_background;
    private ScrollView scrollView;
    private ConstraintLayout artist_detail_info_container;
    private MaterialButton btnSeeSongs, btnHideSongs, btn_see_view_discography;
    private RelativeLayout navbar_artist_UI;
    private String artistId;
    private View fix;

    public static ArtistFragment newInstance(String artistId) {
        ArtistFragment fragment = new ArtistFragment();
        Bundle args = new Bundle();
        args.putString("ARTIST_ID", artistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistId = getArguments().getString("ARTIST_ID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_detail_ui, container, false);
        context = getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (artistId == null || artistId.isEmpty()) {
            Toast.makeText(context, "Invalid Artist ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize views
        initViews(view);
        fix = view.findViewById(R.id.fix_detailUI);

        fix.setOnClickListener(v -> {
            Toast.makeText(context, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", Toast.LENGTH_SHORT).show();

            try {
                Navigation.findNavController(v).navigateUp();
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }

        });
        setupListeners();
        setupRecyclerViews();
        loadData();
    }

    private void initViews(View view) {
        rv_popular_songs = view.findViewById(R.id.rv_popular_songs);
        rv_albums = view.findViewById(R.id.rv_albums);
        rv_playlists = view.findViewById(R.id.rv_playlists);
        rv_similar_artists = view.findViewById(R.id.rv_similar_artists);

        tv_monthly_listeners = view.findViewById(R.id.tv_monthly_listeners);
        tv_monthly_listeners.setText("100000000 " + getString(R.string.monthly_listeners));

        artist_name = view.findViewById(R.id.artist_name);
        tv_artist_name = view.findViewById(R.id.tv_artist_name);
        img_artist_cover = view.findViewById(R.id.img_artist_cover);
        scrollView = view.findViewById(R.id.artist_detail_scrollview);
        navbar_artist_UI = view.findViewById(R.id.navbar_artist_UI);

        btnBack = view.findViewById(R.id.btn_artist_detail_ui_back);
        btn_artist_detail_ui_background = view.findViewById(R.id.btn_artist_detail_ui_background);
        img_album_artist_detail = view.findViewById(R.id.img_album_artist_detail);
        btn_see_view_discography = view.findViewById(R.id.btn_see_view_discography);

        btnSeeSongs = view.findViewById(R.id.btn_see_all_songs);
        btnHideSongs = view.findViewById(R.id.btn_hide_songs);
    }

    private void setupListeners() {

        btn_see_view_discography.setOnClickListener(v -> {
            // Navigate to DiscographyFragment
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup) getView().getParent()).getId(), DiscographyFragment.newInstance(artistId))
                        .addToBackStack(null)
                        .commit();
            }
        });

        setupScrollListener();
    }

    private void setupRecyclerViews() {
        rv_popular_songs.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rv_albums.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rv_playlists.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rv_similar_artists.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }

    private void loadData() {
        AtomicInteger sizeHide = new AtomicInteger();

        // Load popular songs
        PopularViewModel artistListViewModel = new ViewModelProvider(this,
                new PopularViewModel.Factory(requireActivity().getApplication(), artistId))
                .get(PopularViewModel.class);
        artistListViewModel.getListDiscography().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) {
                SongArtistAdapter rvPopularSongsAdapter = new SongArtistAdapter(context, artists);
                rv_popular_songs.setAdapter(rvPopularSongsAdapter);

                ViewGroup.LayoutParams params = rv_popular_songs.getLayoutParams();
                params.height = (int) (64 * getResources().getDisplayMetrics().density * artists.size() * 11 / 20);
                rv_popular_songs.setLayoutParams(params);
                sizeHide.set(artists.size());
            }
        });
        artistListViewModel.fetchItems();

        // Load EPs
        ListDiscographyEPViewModel artistListViewModel2 = new ViewModelProvider(this,
                new ListDiscographyEPViewModel.Factory(requireActivity().getApplication(), artistId, 1))
                .get(ListDiscographyEPViewModel.class);
        artistListViewModel2.getListDiscography().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) {
                AlbumArtistAdapter rvAlbumsAdapter = new AlbumArtistAdapter(context, artists);
                rv_albums.setAdapter(rvAlbumsAdapter);
            }
        });
        artistListViewModel2.fetchItems();

        // Load albums
        ListDiscographyAlbumViewModel artistListViewModel3 = new ViewModelProvider(this,
                new ListDiscographyAlbumViewModel.Factory(requireActivity().getApplication(), artistId, 1))
                .get(ListDiscographyAlbumViewModel.class);
        artistListViewModel3.getListDiscography().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) {
                ArtistPlaylistAdapter rvPlaylistsAdapter = new ArtistPlaylistAdapter(context, artists);
                rv_playlists.setAdapter(rvPlaylistsAdapter);
            }
        });
        artistListViewModel3.fetchItems();

        // Load similar artists
        FansAlsoLikeViewModel artistListViewModel4 = new ViewModelProvider(this,
                new FansAlsoLikeViewModel.Factory(requireActivity().getApplication(), artistId))
                .get(FansAlsoLikeViewModel.class);
        artistListViewModel4.getListDiscography().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) {
                ArtistSimilarAdapter rvSimilarArtistsAdapter = new ArtistSimilarAdapter(context, artists);
                rv_similar_artists.setAdapter(rvSimilarArtistsAdapter);
            }
        });
        artistListViewModel4.fetchItems();

        // Load artist details
        ArtistOverallViewModel artistViewModel = new ViewModelProvider(this,
                new ArtistOverallViewModel.Factory(requireActivity().getApplication(), artistId))
                .get(ArtistOverallViewModel.class);
        artistViewModel.getArtist().observe(getViewLifecycleOwner(), data -> {
            tv_artist_name.setText(data.getName());
            artist_name.setText(data.getName());
            tv_artist_info = getView().findViewById(R.id.tv_artist_info);
            tv_artist_info.setText(data.getDescription());
            participant_artist_detail = getView().findViewById(R.id.participant_artist_detail);
            participant_artist_detail.setText(getString(R.string.participant_text) + data.getName());

            Glide.with(this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_album_artist_detail);

            Glide.with(this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_artist_cover);

            img_artist_artist_detail = getView().findViewById(R.id.img_artist_artist_detail);
            Glide.with(this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_artist_artist_detail);

            artist_detail_info_container = getView().findViewById(R.id.artist_detail_info_container);
            artist_detail_info_container.setOnClickListener(v -> {
                // Navigate to ArtistOverallFragment
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(((ViewGroup) getView().getParent()).getId(), ArtistOverallFragment.newInstance(data.getId()))
                            .addToBackStack(null)
                            .commit();
                }
            });

//            DominantColorExtractor.getDominantColor(context, data.getAvatarUrl(), color -> {
//                navbar_artist_UI.setBackgroundColor(color);
//            });

            DominantColorExtractor.getDominantColor(context, data.getAvatarUrl(), color -> {
                // Lấy màu trắng làm màu nền
                int baseColor = ContextCompat.getColor(context, R.color.white);

                // Làm cho gradient nhẹ hơn bằng cách điều chỉnh alpha của màu chính
                int adjustedColor = adjustAlpha(color, 0.8f);

                // Tạo GradientDrawable với chuyển đổi nhẹ nhàng
                GradientDrawable gradient = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{adjustedColor, baseColor}
                );

                gradient.setCornerRadius(0.3f);

                // Áp dụng gradient vào navbar thay vì màu đơn
                navbar_artist_UI.setBackground(gradient);
            });


        });
        artistViewModel.fetchArtistDetails();

        // Load playlist artist details
        ArtistOverallViewModel playlistArtist = new ViewModelProvider(this,
                new ArtistOverallViewModel.Factory(requireActivity().getApplication(), artistId))
                .get(ArtistOverallViewModel.class);
        playlistArtist.getArtist().observe(getViewLifecycleOwner(), data -> {
            tv_playlist_title_artist_detail = getView().findViewById(R.id.tv_playlist_title_artist_detail);
            img_playlist_artist_detail = getView().findViewById(R.id.img_playlist_artist_detail);
            Glide.with(this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_playlist_artist_detail);
            tv_playlist_title_artist_detail.setText(data.getName());
        });
        playlistArtist.fetchArtistDetails();

        // Set up See More/Hide functionality
        btnSeeSongs.setOnClickListener(v -> {
            ViewGroup.LayoutParams params = rv_popular_songs.getLayoutParams();
            params.height = (int) (64 * getResources().getDisplayMetrics().density * sizeHide.get());
            rv_popular_songs.setLayoutParams(params);

            btnSeeSongs.setVisibility(View.GONE);
            btnHideSongs.setVisibility(View.VISIBLE);
        });

        btnHideSongs.setOnClickListener(v -> {
            ViewGroup.LayoutParams params = rv_popular_songs.getLayoutParams();
            params.height = (int) (64 * getResources().getDisplayMetrics().density * sizeHide.get() * 11 / 20);
            rv_popular_songs.setLayoutParams(params);

            btnSeeSongs.setVisibility(View.VISIBLE);
            btnHideSongs.setVisibility(View.GONE);
        });
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(255 * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    private void setupScrollListener() {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (!isAdded() || getContext() == null) {
                    return;
                }
                int scrollY = scrollView.getScrollY();

                // Calculate the threshold when the main artist name text reaches near the top
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