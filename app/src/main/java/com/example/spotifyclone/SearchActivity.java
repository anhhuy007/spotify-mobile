package com.example.spotifyclone;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.spotifyclone.features.search.inter.SearchMainCallbacks;
import com.example.spotifyclone.features.search.model.SearchItem;
import com.example.spotifyclone.features.search.network.SearchService;
import com.example.spotifyclone.features.search.ui.SearchFragment;
import com.example.spotifyclone.features.search.ui.SearchSuggestFragment;
import com.example.spotifyclone.features.search.viewmodel.SearchViewModel;
import com.example.spotifyclone.features.search.viewmodel.SearchViewModelFactory;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchMainCallbacks {
    private SearchViewModel searchViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search);
//
//        SearchService searchService;
//        SearchViewModel searchViewModel = new ViewModelProvider(
//                this,
//                new SearchViewModelFactory(SearchActivity.this)
//        ).get(SearchViewModel.class);
//
//        // Gọi API tìm kiếm
//        searchViewModel.fetchSearchResults("love", null, null, 1, 10);

        // Create GenreFragment
        setContentView(R.layout.activity_albumlayout);
        Log.d("searchactivity", "on");
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.album_list_holder, SearchSuggestFragment.newInstance("instance"))
//                .commit();
//        Log.d("searchactivity", "after");


    }

    @Override
    public void onMsgFromFragToMain(String from, SearchItem item) {}
    private void handleSearchResults(List<SearchItem> results) {
        for (SearchItem item : results) {
            Log.d("SearchResult", "Type: " + item.getType() + ", Name: " + item.getName());
        }
    }
}

