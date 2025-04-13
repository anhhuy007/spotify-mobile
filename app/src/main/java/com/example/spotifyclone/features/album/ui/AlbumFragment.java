package com.example.spotifyclone.features.album.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.album.adapter.AlbumAdapter;
import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModel;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModelFactory;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {
    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private AlbumViewModel albumViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_layoutlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup NavController
        NavController navController = Navigation.findNavController(view);
        setupViewModel();
        setupRecyclerView(view);
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        albumAdapter = new AlbumAdapter(requireContext(), new ArrayList<>(), album -> {

            navigateToAlbumDetail(album);
        }, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        recyclerView.setAdapter(albumAdapter);
    }

    private void navigateToAlbumDetail(Album album) {
        NavDirections action = AlbumFragmentDirections.actionAlbumFragmentToNavAlbumDetail(
                album.getId()
//                album.getTitle(),
//                album.getArtists_name().toArray(new String[0]), // List<String> → String[]
//                album.getReleaseDate() != null ? album.getReleaseDate().getTime() : 0L, // Date → long
//                album.getCoverUrl(),
//                album.getCreatedAt() != null ? album.getCreatedAt().getTime() : 0L, // Date → long
//                album.getLike_count(),
//                album.getUpdatedAt() != null ? album.getUpdatedAt().getTime() : 0L, // Date → long
//                album.getArtist_url().get(0) // Take the first url
        );
        Navigation.findNavController(requireView()).navigate(action);

    }

    private void setupViewModel() {
        albumViewModel = new ViewModelProvider(
                this,
                new AlbumViewModelFactory(requireContext())
        ).get(AlbumViewModel.class);

        albumViewModel.fetchAlbumsByIds();
        albumViewModel.getAlbums().observe(getViewLifecycleOwner(), albums -> {
            albumAdapter.setData(albums);
        });
    }
}
