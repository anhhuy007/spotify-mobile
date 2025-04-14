package com.example.spotifyclone.features.genre.ui;

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
import com.example.spotifyclone.features.genre.adapter.GenreAdapter;
import com.example.spotifyclone.features.genre.inter.GenreMainCallbacks;
import com.example.spotifyclone.features.genre.viewmodel.GenreViewModel;
import com.example.spotifyclone.features.genre.viewmodel.GenreViewModelFactory;

import java.util.ArrayList;

public class GenreFragment extends Fragment {
    private RecyclerView recyclerView;
    private GenreAdapter genreAdapter;
    private GenreViewModel genreViewModel;
    private GenreMainCallbacks main;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GenreMainCallbacks) {
            main = (GenreMainCallbacks) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MainCallbacks");
        }
    }


    public static GenreFragment newInstance(String strArg) {
        GenreFragment fragment = new GenreFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (GenreMainCallbacks) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_layoutlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView(view);
        setupViewModel();
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        genreAdapter = new GenreAdapter(requireContext(), new ArrayList<>(), genre -> { //override onCLickItem fron GenreAdapter
            if (main != null) {
                main.onMsgFromFragToMain("GENRE_FRAGMENT", genre);
            }
        });

        recyclerView.setAdapter(genreAdapter);
    }

    private void setupViewModel() {
        GenreViewModel genreViewModel = new ViewModelProvider(
                this,
                new GenreViewModelFactory(requireContext())
        ).get(GenreViewModel.class);
        genreViewModel.fetchGenresByIds();

        genreViewModel.getGenres().observe(getViewLifecycleOwner(), genres -> {
            genreAdapter.setData(genres);
        });

    }
}

