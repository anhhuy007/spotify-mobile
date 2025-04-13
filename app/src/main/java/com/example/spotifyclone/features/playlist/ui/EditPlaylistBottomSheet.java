package com.example.spotifyclone.features.playlist.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.album.ui.AlbumBottomSheet;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.playlist.adapter.PlaylistSongAdapter;
import com.example.spotifyclone.features.playlist.inter.OnSongClickListner;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModel;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class EditPlaylistBottomSheet extends BottomSheetDialogFragment {
    // data
    private String playlistId;
    private String playlistName;
    private String playlistImage;
    private String playlistDescription;
    private List<Song> playlist_songs;
    private PlaylistSongAdapter removeSongAdapter;
    private PlaylistViewModel playlistViewModel;

    // UI
    private TextView playlist_name;
    private EditText playlistNameTitle;
    private ImageView playlist_image;
    private RecyclerView edit_song_recyclerview;
    private Button add_description_button;
    private TextView save_button;
    private TextView cancel_button;
    private EditText playlist_description_edittext;




    public static EditPlaylistBottomSheet newInstance(String song_image, String song_name, List<String> artists_name) {
        EditPlaylistBottomSheet fragment = new EditPlaylistBottomSheet();
        return fragment;
    }

    public EditPlaylistBottomSheet() {
        // Constructor mặc định
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            EditPlaylistBottomSheetArgs args = EditPlaylistBottomSheetArgs.fromBundle(getArguments());
            playlistId = args.getPlaylistId();
            playlistName = args.getPlaylistName();
            playlistImage = args.getPlaylistImage();
            playlistDescription=args.getPlaylistDescription();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.bottom_sheet_playlist_edit, container, false);
        playlist_name=view.findViewById(R.id.playlist_title);
        playlist_image=view.findViewById(R.id.playlist_image);
        playlistNameTitle=view.findViewById(R.id.playlist_name_title);
        cancel_button=view.findViewById(R.id.cancel_button);
        save_button=view.findViewById(R.id.save_button);
        add_description_button=view.findViewById(R.id.add_description_button);
        playlist_description_edittext=view.findViewById(R.id.playlist_description_edittext);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {// set up UI, handle event
        super.onViewCreated(view, savedInstanceState);
        int radius = 20;
        Glide.with(getContext())
                .load(playlistImage)
                .apply(new RequestOptions().transform(
                        new MultiTransformation<>(new CenterCrop(), new RoundedCorners(radius))
                ))
                .into(playlist_image);

        playlist_name.setText(playlistName);
        playlistNameTitle.setHint(playlistName);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String change_name=playlistNameTitle.getText().toString();
                String change_description=playlist_description_edittext.getText().toString();
                if(!change_name.isEmpty()&&!change_description.isEmpty()){
                    playlistViewModel.updatePlaylistInfo(playlistId,change_name, change_description);
                }
                else if(!change_description.isEmpty()&&change_name.isEmpty()){

                    playlistViewModel.updatePlaylistInfo(playlistId, playlistName,change_description);
                }
                else if(change_description.isEmpty()&&!change_name.isEmpty()){
                    playlistViewModel.updatePlaylistInfo(playlistId, change_name,playlistDescription);


                }
                dismiss();
            }

        });

        add_description_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_description_button.setVisibility(View.GONE);
                playlist_description_edittext.setVisibility(View.VISIBLE);
            }
        });

        setupViewModel();
        setupRecyclerview(view);

        removeSongAdapter=new PlaylistSongAdapter(requireContext(), playlist_songs, new OnSongClickListner() {
            @Override
            public void OnAddClickSong(Song song) {

            }

            @Override
            public void OnRemoveClickSong(Song song) {
                playlistViewModel.removeSongFromPlaylist(playlistId, song.getId());
            }
            @Override
            public void OnPlaySong(Song song){}

        }, PlaylistSongAdapter.EDIT_TYPE);
        edit_song_recyclerview.setAdapter(removeSongAdapter);
    }

    public void setupViewModel()
    {
        playlistViewModel=new ViewModelProvider(
                requireActivity(),
                new PlaylistViewModelFactory(requireActivity())).get(PlaylistViewModel.class);
        playlistViewModel.fetchPlaylistSong(playlistId);
        playlistViewModel.getPlaylistSongs().observe(getViewLifecycleOwner(),songs->{
            playlist_songs=songs;
            removeSongAdapter.setData(songs);
        });

    }
    public void setupRecyclerview(View view){
        edit_song_recyclerview=view.findViewById(R.id.edit_song_recyclerview);
        edit_song_recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));

    }
}
