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
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.genre.model.Genre;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
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
    private List<SearchItem> searchResult = new ArrayList<>();

    //music
    private MusicPlayerViewModel musicPlayerViewModel;


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
                        item.get_id()

                );
        Navigation.findNavController(requireView()).navigate(action);

    }

    private void navigateToGenreDetail(SearchItem item) {
        SearchAllResultFragmentDirections.ActionSearchAllToGenreDetailFragment action =
                SearchAllResultFragmentDirections.actionSearchAllToGenreDetailFragment(
                        item.get_id()
                );

        Navigation.findNavController(requireView()).navigate(action);
    }


    private void navigateToArtistDetail(SearchItem item, View view){
        if (view != null) {
            // Get the NavController from the rootView
            NavController navController = Navigation.findNavController(view);

            // Create the navigation action with the required argument
            Bundle args = new Bundle();
            args.putString("ARTIST_ID", item.get_id());

            // Navigate to the ArtistFragment
            navController.navigate(R.id.artistFragment, args);
        }

    }




    public void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerview);
        classifyRecyclerView=view.findViewById(R.id.genre_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        classifyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false)); // Hoặc GridLayoutManager nếu muốn

        searchAdapter = new SearchAllAdapter(requireContext(), new ArrayList<>(), item  -> {
            if("song".equals(item.getType())){
                //
                List<String> searchIds=new ArrayList<>();
                for (SearchItem searchItem:searchResult ){
                    if("song".equals(searchItem.getType()))
                    {
                        searchIds.add(searchItem.get_id());
                    }
                }
                musicPlayerViewModel.playSearchSongs(searchIds, item.get_id(), searchQuery);

            }
            if ("album".equals(item.getType())) {
                navigateToAlbumDetail(item);
            }
            if("genre".equals(item.getType())){
                navigateToGenreDetail(item);

            }
            if("artist".equals(item.getType())){
                navigateToArtistDetail(item, view);
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
                requireActivity(),
                new SearchViewModelFactory(requireContext())
        ).get(SearchViewModel.class);

        // Only fetch initial results if not already filtering
        if (!isFiltering && !isInitialLoadComplete) {
            // chỉ fetch nếu chưa có dữ liệu
            searchViewModel.fetchSearchResults(searchQuery, null, null, currentPage, 10);
            isInitialLoadComplete = true;


            // Set up observer for initial results
            if (isAdded() && getViewLifecycleOwner() != null) {
                // Remove previous observers to avoid duplicates
                searchViewModel.getItems().removeObservers(getViewLifecycleOwner());
                searchViewModel.getItems().observe(getViewLifecycleOwner(), items -> {
                    if (items != null && !isFiltering ) {
                        searchResult=items;
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


        SpotifyCloneApplication app = SpotifyCloneApplication.getInstance();
        musicPlayerViewModel = new ViewModelProvider(new ViewModelStoreOwner() {
            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                return app.getAppViewModelStore();
            }
        }, app.getMusicPlayerViewModelFactory()).get(MusicPlayerViewModel.class);





    }
}
