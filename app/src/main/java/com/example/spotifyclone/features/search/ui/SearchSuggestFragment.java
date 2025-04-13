package com.example.spotifyclone.features.search.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.SpotifyCloneApplication;
import com.example.spotifyclone.features.genre.model.Genre;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.example.spotifyclone.features.search.adapter.SearchAdapter;
import com.example.spotifyclone.features.search.inter.SearchMainCallbacks;
import com.example.spotifyclone.features.search.model.SearchItem;
import com.example.spotifyclone.features.search.viewmodel.SearchViewModel;
import com.example.spotifyclone.features.search.viewmodel.SearchViewModelFactory;
import com.google.android.material.chip.Chip;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchSuggestFragment extends Fragment {
    // SearchSuggestFragment code here
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private SearchViewModel searchViewModel;
    private MusicPlayerViewModel musicPlayerViewModel;

    private EditText searchInput;
    private TextView noResultText;
    private Chip allresult;
    private ImageButton searchByVoice;
    private TextView cancelTextView;
    private List<SearchItem> searchResult;
    private String searchQuery;
    private static final int REQUEST_CODE_SPEECH_INPUT = 101;

    @Override
    public void onAttach(@NonNull Context context) {
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
        return view;
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView(view);
        setupViewModel();


        cancelTextView=view.findViewById(R.id.cancel_textView);
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Quay lại fragment trước đó
                NavDirections action=SearchSuggestFragmentDirections.actionSearchSuggestFragmentToNavSearch();
                Navigation.findNavController(view).navigate(action);
            }
        });

        searchInput = view.findViewById(R.id.search_input);
        searchInput.requestFocus();

        searchByVoice=view.findViewById(R.id.search_by_voice);
        searchByVoice.setOnClickListener(view1 -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hãy nói điều bạn muốn tìm...");

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireContext(), "Thiết bị không hỗ trợ giọng nói", Toast.LENGTH_SHORT).show();
            }
        });


        noResultText=view.findViewById(R.id.noResult);
        allresult=view.findViewById(R.id.allresult);
        allresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchQuery = searchInput.getText().toString().trim();
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

                // id, title, image_url
                //->naviagte tới song
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
            if ("genre".equals(item.getType())) {
                navigateToGenreDetail(item);
            }
            if ("artist".equals(item.getType())) {

                navigateToArtistDetail(item, view);
            }
        });
        recyclerView.setAdapter(searchAdapter);
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


    private void navigateToGenreDetail(SearchItem item) {
        SearchSuggestFragmentDirections.ActionSearchSuggestToGenreDetailFragment action =
                SearchSuggestFragmentDirections.actionSearchSuggestToGenreDetailFragment(
                        item.get_id()
                );

        Navigation.findNavController(requireView()).navigate(action);
    }


    private void navigateToAlbumDetail(SearchItem item){
        SearchSuggestFragmentDirections.ActionSearchSuggestFragmentToNavAlbumDetail action=
                SearchSuggestFragmentDirections.actionSearchSuggestFragmentToNavAlbumDetail(
                        item.get_id());
        Navigation.findNavController(requireView()).navigate(action);

    }
    public void setupViewModel(){
        searchViewModel=new ViewModelProvider(
                this,
                new SearchViewModelFactory(requireContext())
        ).get(SearchViewModel.class);
        searchViewModel.getItems().observe(getViewLifecycleOwner(), items -> {
            if (items == null) {
                recyclerView.setVisibility(View.GONE);
                noResultText.setVisibility(View.VISIBLE);
                searchAdapter.setData(new ArrayList<>());

                return;
            }

//            List<SearchItem> items = searchResult.getItems();
            if (items != null && !items.isEmpty()) {
                searchResult=items;
                searchAdapter.setData(items);
                recyclerView.setVisibility(View.VISIBLE);
                noResultText.setVisibility(View.GONE);
                allresult.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.GONE);
                noResultText.setVisibility(View.VISIBLE);
                allresult.setVisibility(View.GONE);

                searchAdapter.setData(new ArrayList<>());
            }
        });


        SpotifyCloneApplication app = SpotifyCloneApplication.getInstance();
        musicPlayerViewModel = new ViewModelProvider(new ViewModelStoreOwner() {
            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                return app.getAppViewModelStore();
            }
        }, app.getMusicPlayerViewModelFactory()).get(MusicPlayerViewModel.class);


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
                    searchQuery=query;
                    workRunnable[0] = () -> searchViewModel.fetchSearchResults(query, null, null, 1, 10);
                    handler.postDelayed(workRunnable[0], 500); // Gửi request sau 500ms
                }
            }


        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);
                searchQuery=spokenText;
                searchInput.setText(spokenText);
                searchInput.setSelection(spokenText.length()); // Đưa con trỏ về cuối chuỗi
            }
        }
    }





}
