package com.example.spotifyclone.features.search.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.search.adapter.SearchAdapter;
import com.example.spotifyclone.features.search.model.SearchItem;
import com.example.spotifyclone.features.search.viewmodel.SearchViewModel;
import com.example.spotifyclone.features.search.viewmodel.SearchViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class SearchAllResultFragment extends Fragment {
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private SearchViewModel searchViewModel;
    private EditText search_input;
    @Override
    public void onAttach(@NonNull Context context) {
        Log.d("Search", "onAttach");
        super.onAttach(context);
    }

    public static SearchAllResultFragment newInstance(String strArg) {
        SearchAllResultFragment fragment = new SearchAllResultFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_search_allresult, container, false);
        return view;
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        String searchQuery = SearchAllResultFragmentArgs.fromBundle(getArguments()).getSearch();

        setupViewModel(searchQuery);
        setupRecyclerView(view);
        search_input=view.findViewById(R.id.search_input);
        search_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(SearchAllResultFragmentDirections.actionSearchAllResultFragmentToSearchSuggestFragment());
            }
        });



    }
    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));

        searchAdapter = new SearchAdapter(requireContext(), new ArrayList<>(), item  -> {

        });
        recyclerView.setAdapter(searchAdapter);
    }
    public void setupViewModel(String searchQuery){
        searchViewModel=new ViewModelProvider(
                this,
                new SearchViewModelFactory(requireContext())
        ).get(SearchViewModel.class);
        searchViewModel.fetchSearchResults(searchQuery, null, null, 1, 10);
        searchViewModel.getSearchResult().observe(getViewLifecycleOwner(), searchResult -> {
            searchAdapter.setData(searchResult.getItems());

        });
    }




}
