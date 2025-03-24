package com.example.spotifyclone.features.artist.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.artist.adapter.ArtistAdapter;
import com.example.spotifyclone.features.artist.viewModel.ArtistListViewModel;

public class ArtistListFragment extends Fragment {
    private RecyclerView recyclerView;
    private Context context;
    private ImageButton btnBack;
    private ArtistAdapter artistAdapter;
    private ArtistListViewModel artistListViewModel;
    private View rootView;

    public static ArtistListFragment newInstance() {
        return new ArtistListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_artist_list, container, false);
        context = getContext();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupBackButton();
        loadData();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.artist_recycler);
        btnBack = view.findViewById(R.id.back_button_artist_list);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void loadData() {
        artistListViewModel = new ViewModelProvider(this).get(ArtistListViewModel.class);
        artistListViewModel.getArtists().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) {
                artistAdapter = new ArtistAdapter(context, artists);
                artistAdapter.setRootView(rootView); // Pass the root view for navigation
                recyclerView.setAdapter(artistAdapter);
            }
        });
        artistListViewModel.fetchItems();
    }
}