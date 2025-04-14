package com.example.spotifyclone.features.genre.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;

import com.example.spotifyclone.features.album.adapter.AlbumAdapter;
import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.album.ui.AlbumDetailFragmentDirections;
import com.example.spotifyclone.features.genre.viewmodel.GenreViewModel;
import com.example.spotifyclone.features.genre.viewmodel.GenreViewModelFactory;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class GenreDetailFragment extends Fragment {
    private ImageView genreImage;
    private TextView genreDescription;
    private TextView genreName;
    private RecyclerView same_genre;
    private AlbumAdapter albumAdapter;
    private NestedScrollView nestedScrollView;
    private Toolbar toolbar;

    // genre info
    private String genre_image;
    private String genre_name;
    private String genre_id;
    private String genre_description;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_genre_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GenreDetailFragmentArgs args=GenreDetailFragmentArgs.fromBundle(getArguments());

        if (args != null) {
//            genre_image= args.getImageUrl();
//            genre_name = args.getName();
            genre_id=args.getId();
//            genre_description=args.getDescription();

        } else {
            NavHostFragment.findNavController(this).navigateUp();
            return;
        }

        initViews(view);

//        setupUI();
        setupViewModel(view);
        setupRecyclerView(view);
        setupToolbar((AppCompatActivity) requireActivity());
        setupScrollListener();
    }

    private void setupToolbar(AppCompatActivity activity) {
        activity.setSupportActionBar(toolbar);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setTitle(genre_name);
        }

        // Use MenuProvider for handling toolbar menu events
        activity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == android.R.id.home) {
                    NavHostFragment.findNavController(GenreDetailFragment.this).navigateUp();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void setupUI() {
        Glide.with(this).load(genre_image).into(genreImage);
        genreDescription.setText(genre_description);
//        genreName.setText(genre_name);
    }

    private void initViews(View view) {
        nestedScrollView = view.findViewById(R.id.nestedScrollview);
        toolbar = view.findViewById(R.id.toolbar);

        genreImage = view.findViewById(R.id.genre_image);
        genreDescription = view.findViewById(R.id.genre_description);
        genreName = view.findViewById(R.id.genre_name);
    }

    public void setupRecyclerView(View view) {
        same_genre = view.findViewById(R.id.same_genre);
        same_genre.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false));

        // album adapter
        int widthInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 200, requireContext().getResources().getDisplayMetrics()
        );

        albumAdapter = new AlbumAdapter(requireContext(), new ArrayList<>(), album -> {
            navigateToAlbumDetail(album);
        }, widthInPx, ViewGroup.LayoutParams.WRAP_CONTENT);
        same_genre.setAdapter(albumAdapter);


    }

    private void navigateToAlbumDetail(Album album){
        NavDirections action = GenreDetailFragmentDirections.actionGenreDetailFragmentToNavAlbumDetail(
                album.getId()
        );
        Navigation.findNavController(requireView()).navigate(action);

    }
    public void setupViewModel(View view) {
        GenreViewModel genreViewModel = new ViewModelProvider(
                this,
                new GenreViewModelFactory(requireContext())
        ).get(GenreViewModel.class);
        genreViewModel.fetchGenreByID(genre_id);
        genreViewModel.getGenreById().observe(getViewLifecycleOwner(), genre -> {
            if (genre != null) {
                genre_image = genre.getImage_url();
                genre_name = genre.getName();
                genre_description = genre.getDescription();
                setupUI();
                setupGradientBackground(view);

            }
        });

        genreViewModel.getGenreAlbums().observe(getViewLifecycleOwner(), albums -> {
            albumAdapter.setData(albums);
        });

        genreViewModel.fetchGenreAlbumsByIds();// fetch album



    }
    private void setupScrollListener() {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    DominantColorExtractor.getDominantColor(requireContext(), genre_image, color -> {
                        toolbar.setBackgroundColor(color);
                    });
                } else if (scrollY == 0) {
                    toolbar.setBackground(new ColorDrawable(Color.TRANSPARENT));
                }
            }
        });
    }

    private void setupGradientBackground(View view) {
        int nightModeFlags = requireContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkMode = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES);
        int secondColor = isDarkMode ? Color.BLACK : Color.WHITE;

        DominantColorExtractor.getDominantColor(requireContext(), genre_image, color -> {
            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{color, secondColor}
            );
            gradient.setCornerRadius(0f);

            view.findViewById(R.id.imageConstraintLayout).setBackground(gradient);
        });
    }



}