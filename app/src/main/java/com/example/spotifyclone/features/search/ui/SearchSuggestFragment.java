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
        SearchSuggestFragment fragment = new SearchSuggestFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        searchInput.requestFocus();

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


        searchAdapter = new SearchAdapter(requireContext(), new ArrayList<>(), item -> {
            if ("song".equals(item.getType())) {
                Log.d("SearchSuggest", "Clicked on a song");
            }
            if ("album".equals(item.getType())) {
                Log.d("SearchSuggest", "Got in here album");
                navigateToAlbumDetail(item);
            }
            if ("genre".equals(item.getType())) {
                Log.d("SearchSuggest", "Clicked on a genre");
            }
            if ("artist".equals(item.getType())) {
                Log.d("SearchSuggest", "Clicked on an artist");
            }
        });
        recyclerView.setAdapter(searchAdapter);
    }
    private void navigateToAlbumDetail(SearchItem item){
        SearchSuggestFragmentDirections.ActionSearchSuggestFragmentToNavAlbumDetail action=
                SearchSuggestFragmentDirections.actionSearchSuggestFragmentToNavAlbumDetail(
                        item.get_id(),
                        item.getName(),
                        item.getArtists_name().toArray(new String[0]),  // Đúng kiểu String[]
                        0L,           // release_date (giả sử 0 nếu không có)
                        item.getImage_url(),
                        0L,           // create_at
                        0,            // like_count
                        0L,           // updatedAt
                        ""            // artist_url
                );
        Navigation.findNavController(requireView()).navigate(action);

    }
    public void setupViewModel(){
        searchViewModel=new ViewModelProvider(
                this,
                new SearchViewModelFactory(requireContext())
        ).get(SearchViewModel.class);
        searchViewModel.getItems().observe(getViewLifecycleOwner(), items -> {
            if (items == null) {
                Log.d("SearchDebug", "Search result is NULL!");
                recyclerView.setVisibility(View.GONE);
                noResultText.setVisibility(View.VISIBLE);
                searchAdapter.setData(new ArrayList<>());
                return;
            }

//            List<SearchItem> items = searchResult.getItems();
            if (items != null && !items.isEmpty()) {
                Log.d("SearchDebug", "Number of items: " + items.size());
                searchAdapter.setData(items);
                recyclerView.setVisibility(View.VISIBLE);
                noResultText.setVisibility(View.GONE);
            } else {
                Log.d("SearchDebug", "Search result is empty!");
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
