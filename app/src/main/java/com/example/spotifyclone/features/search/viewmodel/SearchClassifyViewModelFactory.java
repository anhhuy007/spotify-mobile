package com.example.spotifyclone.features.search.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.features.search.network.SearchService;
import com.example.spotifyclone.shared.network.RetrofitClient;

public class SearchClassifyViewModelFactory implements ViewModelProvider.Factory{
    private final Context context;

    public SearchClassifyViewModelFactory(Context context) {
        this.context = context;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SearchClassifyViewModel.class)) {
            SearchService searchService = RetrofitClient.getClient(context).create(SearchService.class);
            return (T) new SearchClassifyViewModel(searchService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
