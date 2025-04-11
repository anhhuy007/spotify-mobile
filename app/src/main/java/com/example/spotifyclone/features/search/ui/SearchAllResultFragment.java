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
import android.widget.LinearLayout;

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
import com.example.spotifyclone.features.search.viewmodel.SearchClassifyViewModel;
import com.example.spotifyclone.features.search.viewmodel.SearchClassifyViewModelFactory;
import com.example.spotifyclone.features.search.viewmodel.SearchViewModel;
import com.example.spotifyclone.features.search.viewmodel.SearchViewModelFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearchAllResultFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView classifyRecyclerView;
    private SearchAllAdapter searchAdapter;

    private ClassifyAdapter classifyAdapter;
    private SearchViewModel searchViewModel;

    private String searchQuery;
    private EditText search_input;
    private int currentPage=1;
    private int currentClassifyPage=1;
    private String classify=null;
    private boolean isFiltering = false;
    private boolean isPendingFilterRequest = false;
    private boolean isInitialLoadComplete = false;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getViewLifecycleOwnerLiveData().getValue() != null) {
            searchViewModel.getItems().removeObservers(getViewLifecycleOwnerLiveData().getValue());
        }
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
        searchQuery = SearchAllResultFragmentArgs.fromBundle(getArguments()).getSearch();

        setupRecyclerView(view);

        setupViewModel();

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

    private void navigateToAlbumDetail(SearchItem item){
        SearchAllResultFragmentDirections.ActionSearchAllResultFragmentToNavAlbumDetail action=
                SearchAllResultFragmentDirections.actionSearchAllResultFragmentToNavAlbumDetail(
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


    void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerview);
        classifyRecyclerView=view.findViewById(R.id.genre_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        classifyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false)); // Hoặc GridLayoutManager nếu muốn

        searchAdapter = new SearchAllAdapter(requireContext(), new ArrayList<>(), item  -> {

            if ("album".equals(item.getType())) {
                navigateToAlbumDetail(item);
            }

        });

        classifyAdapter = new ClassifyAdapter(requireContext(), new ArrayList<>(), type -> {

            // Prevent duplicate requests
            if (isPendingFilterRequest) {
                return;
            }

            isPendingFilterRequest = true;

            isFiltering = true;
            classify = type;
            currentClassifyPage = 1;

            // set button to green
            classifyAdapter.setSelectedType(type);


            // Clear current data
            searchAdapter.setData(new ArrayList<>());
            searchAdapter.notifyDataSetChanged();

            // Remove any existing observers to prevent duplicate callbacks
            if (isAdded() && getViewLifecycleOwner() != null) {
                searchViewModel.getItems().removeObservers(getViewLifecycleOwner());
            }

            // Fetch filtered data
            searchViewModel.fetchSearchResults(searchQuery, null, classify, currentClassifyPage, 10);

            // Set up the observer for filtered results
            if (isAdded() && getViewLifecycleOwner() != null) {
                searchViewModel.getItems().observe(getViewLifecycleOwner(), items -> {
                    searchAdapter.setData(items);
                    searchAdapter.notifyDataSetChanged();
                    isPendingFilterRequest = false;
                });
            }

        });

        recyclerView.setAdapter(searchAdapter);
        classifyRecyclerView.setAdapter(classifyAdapter);


        // add on scroll listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager=(LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItemPosition=layoutManager.findLastVisibleItemPosition();
                int totalItem=layoutManager.getItemCount();
                if(layoutManager!=null&&lastVisibleItemPosition>=totalItem-1&&!isFiltering)
                {
                    // load more data when reach the end of list
                    currentPage++;
                    new Handler().postDelayed(() -> {
                        searchViewModel.fetchSearchResults(searchQuery, null, classify, currentPage, 10);
                        if (isAdded() && getView() != null){
                            searchViewModel.getItems().observe(getViewLifecycleOwner(), searchitems -> {
                                if (!searchitems.isEmpty()) {
                                    searchAdapter.setData(searchitems);
                                    searchAdapter.notifyDataSetChanged();
                                    //                            currentPage++;
                                }
                            });
                        }


                    }, 1000);


                }
                else if(layoutManager!=null&&lastVisibleItemPosition>=totalItem-1&&isFiltering){
                    currentClassifyPage++;
                    new Handler().postDelayed(() -> {
                        searchViewModel.fetchSearchResults(searchQuery, null, classify, currentClassifyPage, 10);
                        if (isAdded() && getView() != null) {

                            searchViewModel.getItems().observe(getViewLifecycleOwner(), classifyitems -> {
                                if (!classifyitems.isEmpty() && !classifyitems.equals(searchAdapter.getCurrentData())) {
                                    searchAdapter.setData(classifyitems);
                                    searchAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }, 1000);
                }
            }
        });

    }


    public void setupViewModel() {
        searchViewModel = new ViewModelProvider(
                this,
                new SearchViewModelFactory(requireContext())
        ).get(SearchViewModel.class);

        // Only fetch initial results if not already filtering
        if (!isFiltering && !isInitialLoadComplete) {
            searchViewModel.fetchSearchResults(searchQuery, null, null, currentPage, 10);
            isInitialLoadComplete = true;

            // Set up observer for initial results
            if (isAdded() && getViewLifecycleOwner() != null) {
                // Remove previous observers to avoid duplicates
                searchViewModel.getItems().removeObservers(getViewLifecycleOwner());
                searchViewModel.getItems().observe(getViewLifecycleOwner(), items -> {
                    if (items != null && !isFiltering ) {
                        searchAdapter.setData(items);
                        searchAdapter.notifyDataSetChanged();
                    }
                });
            }

        }

        List<String> types = Arrays.asList("artist", "song", "genre", "album");
        classifyAdapter.setData(types);

        if (!isFiltering && classify == null) {
            searchViewModel.fetchSearchResults(searchQuery, null, null, currentPage, 10);
        }


    }
}
