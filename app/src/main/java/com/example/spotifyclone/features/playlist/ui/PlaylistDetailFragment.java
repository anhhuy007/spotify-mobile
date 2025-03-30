package com.example.spotifyclone.features.playlist.ui;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.album.adapter.AlbumAdapter;
import com.example.spotifyclone.features.album.adapter.AlbumSongAdapter;
import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModel;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModelFactory;
import com.example.spotifyclone.features.home.adapter.SongAdapter;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.playlist.adapter.PlaylistSongAdapter;
import com.example.spotifyclone.features.playlist.inter.OnSongClickListner;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModel;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModelFactory;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaylistDetailFragment extends Fragment {
    private String user_name;
    private String user_image;
    private String playlist_id;
    private String playlistUrl;
    private String playlistTitle;
    private String playlistDescription;
    private PlaylistViewModel playlistViewModel;
    private AlbumViewModel albumViewModel;
    private AlbumSongAdapter songAdapter;
    private AlbumAdapter albumAdapter;
    private PlaylistSongAdapter addSongAdapter;
    private List<Song> playlist_songs=new ArrayList<>();

    private List<Song> recommend_songs=new ArrayList<>();
    private List<Album> recommend_albums;

    private Chip add_chip;
    private Chip edit_chip;


    // UI component
    private ImageView playlist_image;
    private Toolbar toolbar;
    private NestedScrollView nestedScrollView;
    private RecyclerView song_recyclerview;
    private RecyclerView song_recommend_recyclerview;
    private RecyclerView recommend_albums_recyclerview;
    private Button new_button;
    //
    private ImageView userImage;
    private TextView userName;

    private TextView playlist_description;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Take arguments from previous fragment
        PlaylistDetailFragmentArgs args = PlaylistDetailFragmentArgs.fromBundle(getArguments());
        user_name = args.getUserName();
        user_image = args.getUserImage();
        playlist_id=args.getPlaylistId();
        playlistTitle=args.getPlaylistName();
        playlistUrl=args.getPlaylistImage();



        initViews(view);
        setupViewModel();
        setupUI();
        setupRecyclerView(view);
        setupToolbar((AppCompatActivity) requireActivity());
        setupScrollListener(); //
        setupGradientBackground(view); //

    }

    private void setupUI() {
        // Load playlist image
        Glide.with(requireContext())
                .load(playlistUrl)
                .into(playlist_image);

        // User info
        Glide.with(requireContext())
                .load(user_image)
                .apply(RequestOptions.circleCropTransform()) // Làm tròn ảnh
                .into(userImage);

        userName.setText(user_name);

        edit_chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // navigate to edit
                PlaylistDetailFragmentDirections.ActionPlaylistDetailFragmentToEditPlaylistBottomSheet action =
                        PlaylistDetailFragmentDirections.actionPlaylistDetailFragmentToEditPlaylistBottomSheet(
                                playlist_id,
                                playlistTitle,
                                playlistUrl,
                                playlistDescription
                        );
                NavController navController = Navigation.findNavController(view);
                navController.navigate(action);
            }
        });

        new_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistViewModel.fetchPopularSongs(playlist_id);
            }
        });


    }

    private  void setupRecyclerView(View view){
        song_recyclerview=view.findViewById(R.id.song_recyclerview);
        song_recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        song_recommend_recyclerview=view.findViewById(R.id.song_recommend_recyclerview);
        song_recommend_recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        recommend_albums_recyclerview=view.findViewById(R.id.recommend_albums);
        recommend_albums_recyclerview.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        songAdapter = new AlbumSongAdapter(getContext(), playlist_songs, 3, (songId, songImage, songTitle,  authorNames, view1) -> {

        });

        addSongAdapter = new PlaylistSongAdapter(requireContext(), recommend_songs, new OnSongClickListner() {
            @Override
            public void OnAddClickSong(Song song) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Thêm bài hát vào Playlist")
                        .setMessage("Bạn có chắc muốn thêm không?")
                        .setPositiveButton("Thêm", (dialog, which) -> {
                            playlistViewModel.addSongToPlaylist(playlist_id, song.getId());

                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }

            @Override
            public void OnRemoveClickSong(Song song) {
                // Không làm gì cả vì adapter này chỉ dùng để thêm bài hát
            }
        }, PlaylistSongAdapter.ADD_TYPE);

        albumAdapter=new AlbumAdapter(requireContext(),recommend_albums, album->{

        }, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        song_recyclerview.setAdapter(songAdapter);
        song_recommend_recyclerview.setAdapter(addSongAdapter);
        recommend_albums_recyclerview.setAdapter(albumAdapter);
    }
    private void setupToolbar(AppCompatActivity activity) {
        activity.setSupportActionBar(toolbar);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setTitle(playlistTitle);
        }

        // Use MenuProvider for handling toolbar menu events
        activity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // Không cần thêm menu items
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == android.R.id.home) {
                    // Sử dụng Navigation Component để quay lại
                    NavHostFragment.findNavController(PlaylistDetailFragment.this).navigateUp();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }


    private void initViews(View view){
        playlist_image = view.findViewById(R.id.playlist_image);

        toolbar = view.findViewById(R.id.toolbar);
        userImage=view.findViewById(R.id.user_image);
        userName=view.findViewById(R.id.user_name);
        nestedScrollView = view.findViewById(R.id.nestedScrollview);
        edit_chip=view.findViewById(R.id.edit_chip);
        add_chip=view.findViewById(R.id.add_chip);
        new_button=view.findViewById(R.id.new_button);
        playlist_description=view.findViewById(R.id.playlist_description);
    }
    private void setupViewModel(){
        playlistViewModel=new ViewModelProvider(
                requireActivity(),
                new PlaylistViewModelFactory(requireContext())).get(PlaylistViewModel.class);
        playlistViewModel.fetchPlaylistSong(playlist_id);
        playlistViewModel.getPlaylistSongs().observe(getViewLifecycleOwner(),songs->{
                playlist_songs=new ArrayList<>(songs);
                songAdapter.setData(playlist_songs);

                if(songs!=null){//only when have result finish previous request
                    playlistViewModel.fetchPopularSongs(playlist_id);// fetch playlist xong rồi mới gọi popular
                }

        });

        playlistViewModel.fetchPlaylistById(playlist_id);
        playlistViewModel.getPlaylistById().observe(getViewLifecycleOwner(), playlist -> {
            if (playlist != null&&playlist.getId()!=null&&playlist.getName()!=null) {
                playlistTitle = playlist.getName();
                playlistDescription = playlist.getDescription();
                CollapsingToolbarLayout collapsingToolbar = requireView().findViewById(R.id.collapsing_toolbar);
                collapsingToolbar.setTitle(playlistTitle);
                playlist_description.setText(playlistDescription);
            }
        });
//        playlistViewModel.fetchPopularSongs(playlist_id)
        playlistViewModel.getPopularSongs().observe(getViewLifecycleOwner(),songs->{
            recommend_songs=songs;
            addSongAdapter.setData(recommend_songs);
        });


        albumViewModel=new ViewModelProvider(
                this,
                new AlbumViewModelFactory(requireContext())).get(AlbumViewModel.class);
        albumViewModel.fetchAlbumsByIds();
        albumViewModel.getAlbums().observe(getViewLifecycleOwner(),albums->{
            recommend_albums=albums;
            albumAdapter.setData(albums);
        });
    }

    private void setupScrollListener() {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    DominantColorExtractor.getDominantColor(requireContext(), playlistUrl, color -> {
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

        DominantColorExtractor.getDominantColor(requireContext(), playlistUrl, color -> {
            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{color, secondColor}
            );
            gradient.setCornerRadius(0f);

            view.findViewById(R.id.imageConstraintLayout).setBackground(gradient);
        });

    }





}
