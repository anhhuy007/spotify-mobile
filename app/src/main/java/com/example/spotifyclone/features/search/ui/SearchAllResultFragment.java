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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.search.adapter.ClassifyAdapter;
import com.example.spotifyclone.features.search.adapter.SearchAdapter;
import com.example.spotifyclone.features.search.adapter.SearchAllAdapter;
import com.example.spotifyclone.features.search.model.SearchItem;
import com.example.spotifyclone.features.search.viewmodel.SearchViewModel;
import com.example.spotifyclone.features.search.viewmodel.SearchViewModelFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearchAllResultFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<SearchItem> items;
    private RecyclerView classifyRecyclerView;
    private SearchAllAdapter searchAdapter;

    private ClassifyAdapter classifyAdapter;
    private SearchViewModel searchViewModel;
    private EditText search_input;
    @Override
    public void onAttach(@NonNull Context context) {
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

        setupRecyclerView(view);
        setupViewModel(searchQuery);
        search_input=view.findViewById(R.id.search_input);
        search_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(SearchAllResultFragmentDirections.actionSearchAllResultFragmentToSearchSuggestFragment());
            }
        });

    }

    @NonNull
    @Override
    public LayoutInflater onGetLayoutInflater(@Nullable Bundle savedInstanceState) {
        return super.onGetLayoutInflater(savedInstanceState);
    }

    void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerview);
        classifyRecyclerView=view.findViewById(R.id.genre_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        classifyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false)); // Hoặc GridLayoutManager nếu muốn

        searchAdapter = new SearchAllAdapter(requireContext(), new ArrayList<>(), item  -> {

        });
        classifyAdapter = new ClassifyAdapter(requireContext(), new ArrayList<>(), type -> {
            Log.d("ClassifyAdapter", "Clicked on: " + type);

            if (items == null || items.isEmpty()) {
                Log.e("ClassifyAdapter", "No items to filter!");
                return;
            }

            List<SearchItem> filterSearch = items.stream()
                    .filter(item -> item.getType().equals(type))
                    .collect(Collectors.toList());

            if (filterSearch.isEmpty()) {
                Log.e("ClassifyAdapter", "No items match the filter: " + type);
            } else {
                Log.d("ClassifyAdapter", "Filtered items count: " + filterSearch.size());
            }

            searchAdapter.setData(filterSearch);
            searchAdapter.notifyDataSetChanged();
        });
        recyclerView.setAdapter(searchAdapter);
        classifyRecyclerView.setAdapter(classifyAdapter);
    }


    public void setupViewModel(String searchQuery){
        searchViewModel=new ViewModelProvider(
                this,
                new SearchViewModelFactory(requireContext())
        ).get(SearchViewModel.class);
        searchViewModel.fetchSearchResults(searchQuery, null, null, 1, 10);
        searchViewModel.getSearchResult().observe(getViewLifecycleOwner(), searchResult -> {
            items=searchResult.getItems();
            searchAdapter.setData(items);
        });

        List<String> types = Arrays.asList("artist", "song", "genre", "album");
        classifyAdapter.setData(types);
    }
}
