package com.example.spotifyclone.features.playlist.ui;

import static androidx.navigation.fragment.FragmentKt.findNavController;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModel;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class PlaylistBottomSheet extends BottomSheetDialogFragment
{
    private LinearLayout option_playlist_eliminate;
    private PlaylistViewModel playlistViewModel;

    public static PlaylistBottomSheet newInstance(String song_image, String song_name, List<String> artists_name) {
        PlaylistBottomSheet fragment = new PlaylistBottomSheet();
        Bundle args = new Bundle();
        args.putString("song_image", song_image);
        args.putString("song_name", song_name);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaylistBottomSheet() {
        // Constructor mặc định
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.bottom_sheet_playlist_moreoption, container, false);
        TextView playlist_name=view.findViewById(R.id.playlist_name);
        ImageView playlist_image=view.findViewById(R.id.playlist_cover);
        TextView usernameText=view.findViewById(R.id.usernameText);
        option_playlist_eliminate=view.findViewById(R.id.option_playlist_eliminate);

        // receive safeargs
        PlaylistBottomSheetArgs args=PlaylistBottomSheetArgs.fromBundle(getArguments());

        Glide.with(this)
                .load(args.getPlaylistImage())
                .into(playlist_image);

        playlist_name.setText(args.getPlaylistName());
        usernameText.setText(args.getUserName());

        //
        createViewmodel();

        option_playlist_eliminate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistViewModel.removePlaylist(args.getPlaylistId());
//                Snackbar.make(view, "Playlist deleted successfully", Snackbar.LENGTH_SHORT).show();
                // Delay 2 giây trước khi quay về
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NavController navController = NavHostFragment.findNavController(PlaylistBottomSheet.this);
                        navController.popBackStack(R.id.profileFragment, false);
                    }
                }, 1000); // 2000 milliseconds = 2 seconds

            }
        });


        return view;
    }
    public void createViewmodel(){
        playlistViewModel=new ViewModelProvider(
                requireActivity(),
                new PlaylistViewModelFactory(requireContext())).get(PlaylistViewModel.class);


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
