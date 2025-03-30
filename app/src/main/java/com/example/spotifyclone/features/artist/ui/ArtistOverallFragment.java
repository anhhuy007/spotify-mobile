package com.example.spotifyclone.features.artist.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.artist.viewModel.ArtistOverallViewModel;

public class ArtistOverallFragment extends Fragment {

    private TextView artistName, artistDescription, postAuthor, tv_monthly_listeners;
    private ImageView artistImage, artistLogo;
    private ImageButton btnBack;
    private Context context;
    private ArtistOverallViewModel viewModel;
    private String artistId;

    public static ArtistOverallFragment newInstance(String artistId) {
        ArtistOverallFragment fragment = new ArtistOverallFragment();
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
        View view = inflater.inflate(R.layout.fragment_artist_detail, container, false);
        context = getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupBackButton();

        if (artistId == null || artistId.isEmpty()) {
            Toast.makeText(context, "Invalid Artist ID", Toast.LENGTH_SHORT).show();
            return;
        }

        loadData();
    }

    private void initViews(View view) {
        artistName = view.findViewById(R.id.artist_name_overall);
        artistDescription = view.findViewById(R.id.artist_description_overall);
        postAuthor = view.findViewById(R.id.post_author_overall);
        artistImage = view.findViewById(R.id.artist_image_overall);
        artistLogo = view.findViewById(R.id.artist_logo_overall);
        btnBack = view.findViewById(R.id.back_button_overall);
        tv_monthly_listeners = view.findViewById(R.id.monthly_listeners_overall);

        tv_monthly_listeners.setText("100000000");
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void loadData() {
        viewModel = new ViewModelProvider(this,
                new ArtistOverallViewModel.Factory(requireActivity().getApplication(), artistId))
                .get(ArtistOverallViewModel.class);

        viewModel.getArtist().observe(getViewLifecycleOwner(), data -> {
            artistName.setText(data.getName());
            artistDescription.setText(data.getDescription());
            postAuthor.setText(getString(R.string.post_by) + " " + data.getName());

            loadImage(artistImage, data.getAvatarUrl());
            loadImage(artistLogo, data.getAvatarUrl());
        });

        viewModel.fetchArtistDetails();
    }

    private void loadImage(ImageView imageView, String url) {
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.loading)
                .into(imageView);
    }
}