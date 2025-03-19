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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.search.adapter.SearchAdapter;
import com.example.spotifyclone.features.search.inter.SearchMainCallbacks;
import com.example.spotifyclone.features.search.model.SearchItem;
import com.example.spotifyclone.features.search.viewmodel.SearchViewModel;
import com.example.spotifyclone.features.search.viewmodel.SearchViewModelFactory;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SearchSuggestFragment extends Fragment {
    // SearchSuggestFragment code here
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private SearchViewModel searchViewModel;
    private EditText searchInput;
    private TextView noResultText;
    private Button allresult;
    @Override
    public void onAttach(@NonNull Context context) {
        Log.d("Search", "onAttach");

        super.onAttach(context);
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
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Search", "onCreateView");
        View view= inflater.inflate(R.layout.fragment_search_suggest, container, false);
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


        searchInput = view.findViewById(R.id.search_input);
        noResultText=view.findViewById(R.id.noResult);
        allresult=view.findViewById(R.id.allresult);
        allresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchQuery = searchInput.getText().toString().trim();
                Log.d("Search_suggest", "Onclik"+searchQuery);
                // Điều hướng và truyền dữ liệu
                SearchSuggestFragmentDirections.ActionSearchSuggestFragmentToSearchAllResultFragment action =
                        SearchSuggestFragmentDirections.actionSearchSuggestFragmentToSearchAllResultFragment(searchQuery);

                Navigation.findNavController(view).navigate(action);
            }
        });

        setupSearchListener(searchInput);

    }
    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));


        searchAdapter = new SearchAdapter(requireContext(), new ArrayList<>(), item  -> {

        });
        recyclerView.setAdapter(searchAdapter);
    }
    public void setupViewModel(){
        searchViewModel=new ViewModelProvider(
                this,
                new SearchViewModelFactory(requireContext())
        ).get(SearchViewModel.class);
//        searchViewModel.fetchSearchResults("love", null, null, 1, 10);
        searchViewModel.getSearchResult().observe(getViewLifecycleOwner(), searchResult -> {
            Log.d("SearchDebug", "Received search result: " + searchResult);

            if (searchResult != null && searchResult.getItems() != null) {
                List<SearchItem> items = searchResult.getItems();
                Log.d("SearchDebug", "Number of items: " + items.size());

                if (!items.isEmpty()) {
                    searchAdapter.setData(items);
                    recyclerView.setVisibility(View.VISIBLE);
                    noResultText.setVisibility(View.GONE);
                } else {
                    Log.d("SearchDebug", "Search result is empty!");
                    recyclerView.setVisibility(View.GONE);
                    noResultText.setVisibility(View.VISIBLE);
                    searchAdapter.setData(new ArrayList<>());
                }
            } else {
                Log.d("SearchDebug", "Search result is NULL!");
                recyclerView.setVisibility(View.GONE);
                noResultText.setVisibility(View.VISIBLE);
                searchAdapter.setData(new ArrayList<>());
            }
        });

    }

    // For debounding
    private void setupSearchListener(EditText searchInput) {
        Handler handler = new Handler(Looper.getMainLooper());
        final Runnable[] workRunnable = new Runnable[1]; // Dùng mảng 1 phần tử để tránh lỗi

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (workRunnable[0] != null) {
                    handler.removeCallbacks(workRunnable[0]); // Kiểm tra null trước khi hủy
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    workRunnable[0] = () -> searchViewModel.fetchSearchResults(query, null, null, 1, 10);
                    handler.postDelayed(workRunnable[0], 500); // Gửi request sau 500ms
                }
            }
        });
    }




}
