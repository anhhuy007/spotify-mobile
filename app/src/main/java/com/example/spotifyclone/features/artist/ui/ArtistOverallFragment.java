package com.example.spotifyclone.features.artist.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.artist.viewModel.ArtistOverallViewModel;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;

public class ArtistOverallFragment extends Fragment {
    private LinearLayout navbar_artist_UI;

    private TextView artistName, artistDescription, postAuthor, tv_monthly_listeners;
    private ImageView artistImage, artistLogo;
    private ImageButton btnBack;
    private Context context;
    private ArtistOverallViewModel viewModel;
    private String artistId;

    private View view;

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
        view = inflater.inflate(R.layout.fragment_artist_detail, container, false);
        context = getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupBackButton();

        if (artistId == null || artistId.isEmpty()) {
            return;
        }

        loadData();
    }

    private void applyDynamicBackground(String avatarUrl) {
        DominantColorExtractor.getDominantColor(context, avatarUrl, color -> {
            int baseColor = ContextCompat.getColor(context, R.color.white);
            int adjustedColor = adjustAlpha(color, 0.8f);

            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{adjustedColor, baseColor}
            );
            gradient.setCornerRadius(0.3f);
            navbar_artist_UI.setBackground(gradient);
        });
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(255 * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    private void initViews(View view) {
        artistName = view.findViewById(R.id.artist_name_overall);
        artistDescription = view.findViewById(R.id.artist_description_overall);
        postAuthor = view.findViewById(R.id.post_author_overall);
        artistImage = view.findViewById(R.id.artist_image_overall);
        artistLogo = view.findViewById(R.id.artist_logo_overall);
        btnBack = view.findViewById(R.id.back_button_overall);
        navbar_artist_UI = view.findViewById(R.id.navbar_artist_UI_tt);

        tv_monthly_listeners = view.findViewById(R.id.monthly_listeners_overall);

        int randomMonthlyListeners = (int) (Math.random() * 1000000);
        tv_monthly_listeners.setText(formatListeners(randomMonthlyListeners));
//        tv_monthly_listeners.setText("100000000");

        artistLogo.setOnClickListener(v -> {
            if (view != null) {
                // Get the NavController from the rootView
                NavController navController = Navigation.findNavController(view);

                // Create the navigation action with the required argument
                Bundle args = new Bundle();
                args.putString("ARTIST_ID", artistId);

                // Navigate to the ArtistFragment
                navController.navigate(R.id.artistFragment, args);
            }
        });


    }

    private static String formatListeners(int listeners) {
        // add dot for every 3 digits
        StringBuilder formatted = new StringBuilder();
        String str = String.valueOf(listeners);
        int length = str.length();
        for (int i = 0; i < length; i++) {
            formatted.append(str.charAt(i));
            if ((length - i - 1) % 3 == 0 && i != length - 1) {
                formatted.append(".");
            }
        }

        return formatted.toString();
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
            applyDynamicBackground(data.getAvatarUrl());


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