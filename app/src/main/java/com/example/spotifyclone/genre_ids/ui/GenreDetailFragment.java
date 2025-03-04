package com.example.spotifyclone.genre_ids.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;

import com.example.spotifyclone.genre_ids.model.Genre;
import com.example.spotifyclone.genre_ids.viewmodel.GenreViewModel;
import com.example.spotifyclone.genre_ids.viewmodel.GenreViewModelFactory;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;

import java.util.ArrayList;

public class GenreDetailFragment extends Fragment {
    private ImageView genreImage;
    private TextView genreDescription;
    private TextView genreName;
    private RecyclerView same_genre;

    private AlbumAdapter albumAdapter;
    private GenreMainCallbacks callback;
    private Button backButton;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GenreMainCallbacks) {
            callback = (GenreMainCallbacks) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MainCallbacks");
        }
    }

    public static GenreDetailFragment newInstance(Genre genre) {
        GenreDetailFragment fragment = new GenreDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("image_url", genre.getImg_url());
        bundle.putString("description", genre.getDescription());
        bundle.putString("name", genre.getName());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.genre_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        genreImage = view.findViewById(R.id.genre_image);
        genreDescription = view.findViewById(R.id.genre_description);
        genreName = view.findViewById(R.id.genre_name);
        backButton=view.findViewById(R.id.backButton);

        //         extract color from picture
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkMode = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES);

        int secondColor = isDarkMode ? Color.BLACK : Color.WHITE; // Đổi màu theo theme

        DominantColorExtractor.getDominantColor(getContext(), getArguments().getString("image_url"), color -> {
            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{color, secondColor} // Dùng màu tùy theo theme
            );

            gradient.setCornerRadius(0f);

            // Tìm ConstraintLayout và đặt background
            ConstraintLayout imageConstraintLayout = view.findViewById(R.id.imageConstraintLayout);
            imageConstraintLayout.setBackground(gradient);
        });

        if (getArguments() != null) {
            Glide.with(this).load(getArguments().getString("image_url")).into(genreImage);
            genreDescription.setText(getArguments().getString("description", "No description available"));
            genreName.setText(getArguments().getString("name", "Unknown Genre"));
        }

        setupViewModel();
        setupRecyclerView(view);

        // Handle back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callback!=null){
                    callback.onMsgFromFragToMain("GENRE DETAIL", null);
                }
            }
        });

    }


    private void setupRecyclerView(View view) {
        same_genre = view.findViewById(R.id.recyclerview1);
        same_genre.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false));
        albumAdapter=new AlbumAdapter(requireContext(), new ArrayList<>());
        same_genre.setAdapter(albumAdapter);
    }

    private void setupViewModel() {
        GenreViewModel genreViewModel = new ViewModelProvider(
                this,
                new GenreViewModelFactory(requireContext())
        ).get(GenreViewModel.class);

        genreViewModel.getGenreAlbums().observe(getViewLifecycleOwner(), albums -> {
            albumAdapter.setData(albums);
        });

        genreViewModel.fetchGenreAlbumsByIds();// fetch album
    }

}
