package com.example.spotifyclone.features.playlist.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class NewPlaylistBottomSheet extends BottomSheetDialogFragment {
    private String songId;
    private String songName;
    private EditText playlistNameEditText;
    private Button createButton;
    private PlaylistViewModel playlistViewModel;


    public static NewPlaylistBottomSheet newInstance(String songId, String songName ) {
        NewPlaylistBottomSheet fragment = new NewPlaylistBottomSheet();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.bottom_sheet_new_playlist, container, false);
        playlistNameEditText=view.findViewById(R.id.playlist_name_edittext);
        createButton=view.findViewById(R.id.create_button);

        // set up viewmodel
        setupViewModel();


        // Handle click create new playlist
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playlistName=playlistNameEditText.getText().toString();
                if(playlistName==null||playlistName.isEmpty()){
                    Log.d("NewPlaylistBottomSheet", songId+songName);
                    playlistViewModel.createPlaylist(songId, songName);
                }
                else{
                    Log.d("NewPlaylistBottomSheet", songId+" Playlistname"+songName);
                    playlistViewModel.createPlaylist(songId, songName);
                }
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


    private void setupViewModel() {
        playlistViewModel = new ViewModelProvider(
                this,
                new PlaylistViewModelFactory(requireContext())
        ).get(PlaylistViewModel.class);
    }




}
