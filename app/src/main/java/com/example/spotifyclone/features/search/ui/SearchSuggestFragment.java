package com.example.spotifyclone.features.search.ui;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.album.adapter.AlbumAdapter;
import com.example.spotifyclone.features.album.inter.AlbumMainCallbacks;
import com.example.spotifyclone.features.album.ui.AlbumFragment;
import com.example.spotifyclone.features.search.adapter.SearchAdapter;
import com.example.spotifyclone.features.search.inter.SearchMainCallbacks;
import com.example.spotifyclone.features.search.model.SearchResult;
import com.example.spotifyclone.features.search.network.SearchService;
import com.example.spotifyclone.features.search.viewmodel.SearchViewModel;
import com.example.spotifyclone.features.search.viewmodel.SearchViewModelFactory;
import com.example.spotifyclone.shared.model.APIResponse;

import java.util.ArrayList;

import retrofit2.Call;

public class SearchSuggestFragment extends Fragment {
    // SearchSuggestFragment code here
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private SearchViewModel searchViewModel;
    private SearchMainCallbacks main;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SearchMainCallbacks) {
            main = (SearchMainCallbacks) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MainCallbacks");
        }
    }

    public static SearchSuggestFragment newInstance(String strArg) {
        Log.d("Search", "new instance");
        SearchSuggestFragment fragment = new SearchSuggestFragment();
        Log.d("Search", "new instance");
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Search", "onCreate");
        main = (SearchMainCallbacks) getActivity();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Search", "onCreateView");
        View view= inflater.inflate(R.layout.activity_search_suggest, container, false);
        if(view==null){
            Log.d("Search", "onCreateView null");
        }
        else {
            Log.d("Search", "not null");

        }
        return view;
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        Log.d("Search", "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView(view);
        setupViewModel();
    }
    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));

        searchAdapter = new SearchAdapter(requireContext(), new ArrayList<>(), item  -> {
            if (main != null) {
                main.onMsgFromFragToMain("SEARCH_SUGGEST", item);
            }
        });
        recyclerView.setAdapter(searchAdapter);
    }
    public void setupViewModel(){
        searchViewModel=new ViewModelProvider(
                this,
                new SearchViewModelFactory(requireContext())
        ).get(SearchViewModel.class);
        searchViewModel.fetchSearchResults("love", null, null, 1, 10);
        searchViewModel.getSearchResult().observe(getViewLifecycleOwner(), searchResult -> {
            searchAdapter.setData(searchResult.getItems());
        });
    }


}
