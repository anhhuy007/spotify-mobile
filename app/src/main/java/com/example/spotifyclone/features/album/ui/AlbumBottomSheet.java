package com.example.spotifyclone.features.album.ui;

import static androidx.navigation.Navigation.findNavController;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import java.util.List;

public class AlbumBottomSheet extends BottomSheetDialogFragment {
    private LinearLayout add_to_playlist;

    public static AlbumBottomSheet newInstance(String song_image, String song_name, List<String> artists_name) {
        AlbumBottomSheet fragment = new AlbumBottomSheet();
        Bundle args = new Bundle();
        args.putString("song_image", song_image);
        args.putString("song_name", song_name);

        fragment.setArguments(args);
        return fragment;
    }

    public AlbumBottomSheet() {
        // Constructor mặc định
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.bottom_sheet_song_option, container, false);
        TextView song_name=view.findViewById(R.id.song_name);
        ImageView song_image=view.findViewById(R.id.song_cover);
        TextView song_artist=view.findViewById(R.id.song_artist);
        add_to_playlist=view.findViewById(R.id.add_to_playlist);

        // receive safeargs
        AlbumBottomSheetArgs args=AlbumBottomSheetArgs.fromBundle(getArguments());

        Glide.with(this)
                .load(args.getSongImage())
                .into(song_image);

        song_name.setText(args.getSongName());
        song_artist.setText(String.join(" ,",args.getSongArtist()));
        add_to_playlist.setOnClickListener(view1 -> {
            final String songName = args.getSongName();
            final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            // Cũng nên kiểm tra null
            if (fragmentManager == null) {
                Log.e("AlbumBottomSheet", "FragmentManager is null");
                return;
            }

            dismiss(); // Đóng BottomSheet hiện tại

            // Sử dụng Handler với thời gian ngắn hơn
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    AllPlaylistBottomSheet allPlaylistBottomSheet = AllPlaylistBottomSheet.newInstance(args.getId(), args.getSongName());
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

        // Bottom sheet
        View parentView = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (parentView != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parentView);

            // Lấy chiều cao màn hình
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int screenHeight = displayMetrics.heightPixels;

            // Chiều cao khi mở (50% màn hình)
            int peekHeight = screenHeight / 2;

            // Thiết lập chiều cao mặc định
            behavior.setPeekHeight(peekHeight);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            // Không ép về full screen mà mở theo nội dung thực tế
            behavior.setFitToContents(true);
            behavior.setHalfExpandedRatio(0.5f);
            behavior.setHideable(false);

            // Xử lý khi kéo lên/kéo xuống
            behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        // Khi kéo lên, mở rộng theo nội dung thực tế
                        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        bottomSheet.setLayoutParams(layoutParams);
                    } else if (newState == BottomSheetBehavior.STATE_DRAGGING ||
                            newState == BottomSheetBehavior.STATE_SETTLING) {
                        // Khi kéo xuống, quay về 50% màn hình
                        behavior.setPeekHeight(peekHeight);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    // Không cần làm gì trong onSlide()
                }
            });
        }
    }



}
