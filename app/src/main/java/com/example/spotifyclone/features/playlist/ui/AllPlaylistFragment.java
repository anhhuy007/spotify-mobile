package com.example.spotifyclone.features.playlist.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.album.adapter.AlbumAdapter;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModel;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModelFactory;
import com.example.spotifyclone.features.playlist.adapter.PlaylistAdapter;
import com.example.spotifyclone.features.playlist.model.Playlist;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModel;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModelFactory;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public class AllPlaylistFragment extends Fragment {
    private RecyclerView recyclerView;
    private PlaylistAdapter playlistAdapter;
    private PlaylistViewModel playlistViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_playlists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        setupViewModel();
        setupRecyclerView(view);

    }
//
    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        playlistAdapter = new PlaylistAdapter(requireContext(), new ArrayList<>(), playlist -> {
            // Chuyển đến
            navigateToPlaylistDetail(playlist);
        });

        recyclerView.setAdapter(playlistAdapter);
    }

    private void setupViewModel() {
        playlistViewModel = new ViewModelProvider(
                this,
                new PlaylistViewModelFactory(requireContext())
        ).get(PlaylistViewModel.class);

        playlistViewModel.fetchPlaylists();
        playlistViewModel.getUserPlaylist().observe(getViewLifecycleOwner(), playlists -> {
            playlistAdapter.setData(playlists);
        });
    }

    private void navigateToPlaylistDetail(Playlist playlist){
        AllPlaylistFragmentDirections.ActionAllPlaylistFragmentToPlaylistDetailFragment action=
                AllPlaylistFragmentDirections.actionAllPlaylistFragmentToPlaylistDetailFragment(
                        "",
                        "",
                        playlist.getId(),
                        playlist.getName(),
                        playlist.getCoverUrl()
                );

        Navigation.findNavController(requireView()).navigate(action);
    }


}
