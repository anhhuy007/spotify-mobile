package com.example.spotifyclone.features.album.ui;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.album.model.Song;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AlbumBottomSheet extends BottomSheetDialogFragment {

    public static AlbumBottomSheet newInstance(Song song) {
        AlbumBottomSheet fragment = new AlbumBottomSheet();
        Bundle args = new Bundle();
        args.putString("song_image", song.getImg_url());
        args.putString("song_name", song.getTitle());
        fragment.setArguments(args);
        return fragment;
    }

    public AlbumBottomSheet() {
        // Constructor mặc định
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.bottomsheet_album, container, false);
        TextView song_name=view.findViewById(R.id.song_name);
        ImageView song_image=view.findViewById(R.id.song_cover);

        Glide.with(this)  // 'this' refers to the current context (Activity or Fragment)
                .load(getArguments().getString("song_image")) // The URL of the image to load
                .into(song_image); // The ImageView to load the image into

        song_name.setText(getArguments().getString("song_name"));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View parentView = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (parentView != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parentView);

            // Lấy chiều cao màn hình
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int screenHeight = displayMetrics.heightPixels;

            // Đặt chiều cao của BottomSheet = 50% màn hình
            parentView.getLayoutParams().height = screenHeight / 2;

            // Đặt trạng thái mở rộng
            behavior.setPeekHeight(screenHeight / 2);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }



}
