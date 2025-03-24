package com.example.spotifyclone.features.playlist.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.playlist.adapter.PlaylistAdapter;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModel;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class AllPlaylistBottomSheet extends BottomSheetDialogFragment {
    private String songName;
    private String songId;
    private Button newPlaylistButton;
    private RecyclerView recyclerView;
    private PlaylistAdapter playlistAdapter;
    private PlaylistViewModel playlistViewModel;

    public static AllPlaylistBottomSheet newInstance(String songId, String songName ) {
        AllPlaylistBottomSheet fragment = new AllPlaylistBottomSheet();
        Bundle args = new Bundle();
        args.putString("song_id", songId);
        args.putString("song_name", songName);
        fragment.setArguments(args); // Đặt dữ liệu vào arguments
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songId=getArguments().getString("song_id");
            songName = getArguments().getString("song_name");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.bottom_sheet_all_playlist, container, false);
        newPlaylistButton = view.findViewById(R.id.newPlaylistButton);

        setupViewModel();
        setupRecyclerView(view);
        newPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                // Cũng nên kiểm tra null
                if (fragmentManager == null) {
                    Log.e("AllPlaylistBottomSheet", "FragmentManager is null");
                    return;
                }

                dismiss(); // Đóng BottomSheet hiện tại

                // Sử dụng Handler với thời gian ngắn hơn
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        NewPlaylistBottomSheet newPlaylistBottomSheet = NewPlaylistBottomSheet.newInstance(songId, songName);
                        newPlaylistBottomSheet.show(fragmentManager, "NewPlaylistBottomSheet");
                    } catch (Exception e) {
                        Log.e("AllPlaylistBottomSheet", "Error showing NewPlaylistBottomSheet", e);
                    }
                });

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Đảm bảo dialog không bị lỗi hiển thị
        if (getDialog() != null) {
            View parentView = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (parentView != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parentView);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }

    }
    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.playlist_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        playlistAdapter = new PlaylistAdapter(requireContext(), new ArrayList<>(), playlist -> {

        });
        recyclerView.setAdapter(playlistAdapter);
    }

    private void setupViewModel(){
        playlistViewModel = new ViewModelProvider(
                this,
                new PlaylistViewModelFactory(requireContext())
        ).get(PlaylistViewModel.class);

        playlistViewModel.fetchPlaylists();
        playlistViewModel.getUserPlaylist().observe(getViewLifecycleOwner(), playlists -> {
            playlistAdapter.setData(playlists);
        });

    }
}
