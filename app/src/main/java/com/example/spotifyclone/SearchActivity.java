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

//        // Create GenreFragment
//        setContentView(R.layout.activity_albumlayout);
//        Log.d("searchactivity", "on");
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.album_list_holder, SearchSuggestFragment.newInstance("instance"))
//                .commit();
//        Log.d("searchactivity", "after");
// Make sure you have this permission in your AndroidManifest.xml
// <uses-permission android:name="android.permission.INTERNET" />

// In your activity or fragment
        setContentView(R.layout.item_song);
        ImageView imageView = findViewById(R.id.img_song_cover);

// Load with error handling
        Glide.with(this)
                .load("https://res.cloudinary.com/dndmj9oid/image/upload/v1739847800/L%C3%82N_CU%E1%BB%90I_%C4%91i_b%C3%AAn_em_x%C3%B3t_xa_ng%C6%B0%E1%BB%9Di_%C6%A1i_downloaded_from_SpotiSongDownloader.com__fknf0j.jpg")
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        Log.e("GLIDE_ERROR", "Lỗi tải ảnh: ", e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("GLIDE_SUCCESS", "Tải ảnh thành công!");
                        return false;
                    }
                })
                .error(R.drawable.ic_add_circle)
                .placeholder(R.drawable.ic_search)
                .into(imageView);
    }

    @Override
    public void onMsgFromFragToMain(String from, SearchItem item) {}
    private void handleSearchResults(List<SearchItem> results) {
        for (SearchItem item : results) {
            Log.d("SearchResult", "Type: " + item.getType() + ", Name: " + item.getName());
        }
    }
}

