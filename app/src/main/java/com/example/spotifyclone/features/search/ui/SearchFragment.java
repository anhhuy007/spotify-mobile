package com.example.spotifyclone.features.search.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.album.adapter.AlbumAdapter;
import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.album.ui.AlbumFragmentDirections;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModel;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModelFactory;
import com.example.spotifyclone.features.genre.adapter.GenreAdapter;
import com.example.spotifyclone.features.genre.model.Genre;
import com.example.spotifyclone.features.genre.viewmodel.GenreViewModel;
import com.example.spotifyclone.features.genre.viewmodel.GenreViewModelFactory;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private EditText search_input;
    private GenreAdapter genreAdapter;
    private GenreViewModel genreViewModel;

    private NavController navController;


    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        search_input=view.findViewById(R.id.search_input);

        // setup NavController
        navController = Navigation.findNavController(view);

        // set up recyclerview, viewmodel
        setupViewModel();
        setupRecyclerView(view);

    }
    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.genre_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        //Handle click on search bar
        search_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("SearchBar", "Search bar clicked!");
                Navigation.findNavController(view).navigate(R.id.action_nav_search_to_searchSuggestFragment);
            }
        });

        // Handle clicking on genre
        genreAdapter = new GenreAdapter(requireContext(), new ArrayList<>(), genre -> {
            // Chuyển đến genreDetailFragment
            Log.d("Search", "to genre");
            navigateToGenreDetail(genre);

        });


        recyclerView.setAdapter(genreAdapter);
    }

    private void navigateToGenreDetail(Genre genre){
        SearchFragmentDirections.ActionNavSearchToGenreDetailFragment action=
                SearchFragmentDirections.actionNavSearchToGenreDetailFragment(
                        genre.get_id(),
                        genre.getName(),
                        genre.getDescription(),
                        genre.getImage_url(),
                        genre.getCreate_at().getTime(),
                        genre.getCreate_at().getTime()
                );

        Navigation.findNavController(requireView()).navigate(action);

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