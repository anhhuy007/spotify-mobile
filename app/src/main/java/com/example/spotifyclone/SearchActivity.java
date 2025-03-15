package com.example.spotifyclone;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
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

//        // Create GenreFragment
//        setContentView(R.layout.activity_albumlayout);
//        Log.d("searchactivity", "on");
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.album_list_holder, SearchSuggestFragment.newInstance("instance"))
//                .commit();
//        Log.d("searchactivity", "after");
        setContentView(R.layout.item_song);

        ImageView search=findViewById(R.id.img_song_cover);
        Glide.with(SearchActivity.this)
                .load("https://res.cloudinary.com/dndmj9oid/image/upload/v1739847800/L%E1%BA%A6N_CU%E1%BB%90I_%C4%91i_b%C3%AAn_em_x%C3%B3t_xa_ng%C6%B0%E1%BB%9Di_%C6%A1i_downloaded_from_SpotiSongDownloader.com__fknf0j.jpg")
                .into(search);
    }

    @Override
    public void onMsgFromFragToMain(String from, SearchItem item) {}
    private void handleSearchResults(List<SearchItem> results) {
        for (SearchItem item : results) {
            Log.d("SearchResult", "Type: " + item.getType() + ", Name: " + item.getName());
        }
    }
}

