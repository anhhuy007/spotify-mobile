package com.example.spotifyclone.features.topproduct.ui;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.topproduct.apdaper.AlsoLikeAdapter;
import com.example.spotifyclone.features.topproduct.apdaper.TopAlbumAdapter;
import com.example.spotifyclone.features.topproduct.viewModel.AlsoLikeViewModel;
import com.example.spotifyclone.features.topproduct.viewModel.MostAlbumViewModel;
import com.example.spotifyclone.features.topproduct.viewModel.TopAlbumViewModel;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;

import java.util.concurrent.atomic.AtomicInteger;

public class TopAlbumFragment extends Fragment {
    private RecyclerView rvTop, rvAlsoLike;
    private Context context;
    private ImageButton btnBack;
    private TextView tv_artist_name, tv_monthly_listeners, artist_name;
    private ImageView img_artist_artist_detail, img_artist_cover, btn_artist_detail_ui_background;
    private ScrollView scrollView;
    private RelativeLayout navbar_artist_UI;

    private AlsoLikeViewModel alsoLikeViewModel;
    private TopAlbumViewModel topProductViewModel;
    private TopAlbumAdapter topProductAdapter;
    private AlsoLikeAdapter alsoLikeAdapter;
    private View rootView;

    public static TopAlbumFragment newInstance() {
        return new TopAlbumFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_top_product, container, false);
        context = getContext();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = requireContext();

        initViews(view);
        setupRecyclerViews();
        setupBackButton();
        setupViewModels();
        setupScrollListener();
    }

    private void initViews(View view) {
        rvTop = view.findViewById(R.id.rv_albums_top);
        rvAlsoLike = view.findViewById(R.id.rv_also_like_top);
        tv_monthly_listeners = view.findViewById(R.id.tv_monthly_listeners_top);
        artist_name = view.findViewById(R.id.artist_name_top);
        tv_artist_name = view.findViewById(R.id.tv_artist_name_top);
        img_artist_cover = view.findViewById(R.id.img_artist_cover_top);
        scrollView = view.findViewById(R.id.artist_detail_scrollview_top);
        navbar_artist_UI = view.findViewById(R.id.navbar_artist_UI_top);
        btnBack = view.findViewById(R.id.btn_artist_detail_ui_back_top);
        btn_artist_detail_ui_background = view.findViewById(R.id.btn_artist_detail_ui_background_top);
    }

    private void setupRecyclerViews() {
        rvTop.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvAlsoLike.setLayoutManager(new GridLayoutManager(context, 2));
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupViewModels() {
        topProductViewModel = new ViewModelProvider(this).get(TopAlbumViewModel.class);
        topProductViewModel.getTopProduct().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) {
                topProductAdapter = new TopAlbumAdapter(context, artists);
                topProductAdapter.setRootView(rootView);
                rvTop.setAdapter(topProductAdapter);

                ViewGroup.LayoutParams params = rvTop.getLayoutParams();
                params.height = (int) (114 * getResources().getDisplayMetrics().density * artists.size() );
                rvTop.setLayoutParams(params);
            }
        });
        topProductViewModel.fetchItems();

        alsoLikeViewModel = new ViewModelProvider(this).get(AlsoLikeViewModel.class);
        alsoLikeViewModel.getTopProduct().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) {
                alsoLikeAdapter = new AlsoLikeAdapter(context, artists);
                alsoLikeAdapter.setRootView(rootView);
                rvAlsoLike.setAdapter(alsoLikeAdapter);
            }
        });
        alsoLikeViewModel.fetchItems();

        MostAlbumViewModel mostViewModel = new ViewModelProvider(this).get(MostAlbumViewModel.class);
        mostViewModel.getArtist().observe(getViewLifecycleOwner(), data -> {
            tv_artist_name.setText(data.getName());
            artist_name.setText(data.getName());


            Glide.with(this)
                    .load(data.getAvatarUrl())
                    .placeholder(R.drawable.loading)
                    .into(img_artist_cover);

            tv_monthly_listeners.setText(data.getDescription()+" " + getString(R.string.play_count));

            DominantColorExtractor.getDominantColor(context, data.getAvatarUrl(), color -> {
                navbar_artist_UI.setBackgroundColor(color);
            });
        });
        mostViewModel.fetchArtistDetails();
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
                float threshold = (40 * requireContext().getResources().getDisplayMetrics().density) + (nameHeight * 0.7f);

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