package com.example.spotifyclone.features.album.ui;

import static androidx.navigation.Navigation.findNavController;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.playlist.ui.AllPlaylistBottomSheet;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlbumBottomSheet extends BottomSheetDialogFragment {
    private LinearLayout add_to_playlist;

    public static AlbumBottomSheet newInstance(String id, String song_image, String song_name, List<String> artists_name) {
        AlbumBottomSheet fragment = new AlbumBottomSheet();
        Bundle args = new Bundle();
        args.putString("_id", id);
        args.putString("song_image", song_image);
        args.putString("song_name", song_name);
        if (artists_name == null) {
            artists_name = new ArrayList<>();
        }
        args.putStringArrayList("song_artist", new ArrayList<>(artists_name));
        fragment.setArguments(args);
        return fragment;
    }

    public AlbumBottomSheet() {
        // Constructor mặc định
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_song_option, container, false);
        TextView song_name = view.findViewById(R.id.song_name);
        ImageView song_image = view.findViewById(R.id.song_cover);
        TextView song_artist = view.findViewById(R.id.song_artist);
        add_to_playlist = view.findViewById(R.id.add_to_playlist);

        Bundle bundle = getArguments();
        String id, image, name;
        List<String> artistList;

        if (bundle == null ) {
            AlbumBottomSheetArgs args = AlbumBottomSheetArgs.fromBundle(bundle);
            id = args.getId();
            image = args.getSongImage();
            name = args.getSongName();
            artistList = Arrays.asList(args.getSongArtist());
        } else {
            id = bundle.getString("_id", "");
            image = bundle.getString("song_image", "");
            name = bundle.getString("song_name", "");
            artistList = bundle.getStringArrayList("song_artist");
            if (artistList == null) artistList = new ArrayList<>();
        }

        // Set dữ liệu
        Glide.with(this).load(image).into(song_image);
        song_name.setText(name);
        song_artist.setText(TextUtils.join(", ", artistList));

        String finalId = id;
        String finalImage = image;
        String finalName = name;

        add_to_playlist.setOnClickListener(view1 -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            if (fragmentManager == null) {
                Log.e("AlbumBottomSheet", "FragmentManager is null");
                return;
            }

            dismiss();

            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    AllPlaylistBottomSheet allPlaylistBottomSheet =
                            AllPlaylistBottomSheet.newInstance(finalId, finalName, finalImage);
                    allPlaylistBottomSheet.show(fragmentManager, "AllPlaylistBottomSheet");
                } catch (Exception e) {
                    Log.e("AlbumBottomSheet", "Error showing AllPlaylistBottomSheet", e);
                }
            });
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View parentView = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (parentView != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parentView);
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int screenHeight = displayMetrics.heightPixels;
            int peekHeight = screenHeight / 2;

            behavior.setPeekHeight(peekHeight);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            behavior.setFitToContents(true);
            behavior.setHalfExpandedRatio(0.5f);
            behavior.setHideable(false);

            behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        bottomSheet.setLayoutParams(layoutParams);
                    } else if (newState == BottomSheetBehavior.STATE_DRAGGING ||
                            newState == BottomSheetBehavior.STATE_SETTLING) {
                        behavior.setPeekHeight(peekHeight);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    // No-op
                }
            });
        }
    }
}
