package com.example.spotifyclone.genre_ids.ui;

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
import com.example.spotifyclone.genre_ids.viewmodel.GenreViewModel;
import com.example.spotifyclone.genre_ids.viewmodel.GenreViewModelFactory;
import com.example.spotifyclone.shared.network.RetrofitClient;

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
        Log.d("GenreFragment", "newInstance");
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (GenreMainCallbacks) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("GenreFragment", "onCreateView");
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
            Log.d("GenreFragment", "Selected genre: " + genre.getName());
            if (main != null) {
                main.onMsgFromFragToMain("GENRE_FRAGMENT", genre);
            }
        });

        recyclerView.setAdapter(genreAdapter);
        Log.d("GenreFragment", "RecyclerView initialized");
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

