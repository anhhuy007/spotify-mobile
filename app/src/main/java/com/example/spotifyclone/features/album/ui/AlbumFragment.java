package com.example.spotifyclone.album.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.album.adapter.AlbumAdapter;
import com.example.spotifyclone.album.inter.AlbumMainCallbacks;
import com.example.spotifyclone.album.viewmodel.AlbumViewModel;
import com.example.spotifyclone.album.viewmodel.AlbumViewModelFactory;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {
    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private AlbumViewModel albumViewModel;
    private AlbumMainCallbacks main;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AlbumMainCallbacks) {
            main = (AlbumMainCallbacks) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MainCallbacks");
        }
    }

    public static AlbumFragment newInstance(String strArg) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        Log.d("AlbumFragment", "newInstance");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (AlbumMainCallbacks) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AlbumFragment", "onCreateView");
        return inflater.inflate(R.layout.activity_layoutlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewModel();
        setupRecyclerView(view);
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        albumAdapter = new AlbumAdapter(requireContext(), new ArrayList<>(), album  -> {
            Log.d("AlbumFragment", "Selected album: " + album.getTitle());
            if (main != null) {
                main.onMsgFromFragToMain("ALBUM_FRAGMENT", album);
            }
        }, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        recyclerView.setAdapter(albumAdapter);
    }

    private void setupViewModel() {
        AlbumViewModel albumViewModel = new ViewModelProvider(
                this,
                new AlbumViewModelFactory(requireContext())
        ).get(AlbumViewModel.class);
        albumViewModel.fetchAlbumsByIds();
        albumViewModel.getAlbums().observe(getViewLifecycleOwner(), albums -> {
            albumAdapter.setData(albums);
        });
    }
}
