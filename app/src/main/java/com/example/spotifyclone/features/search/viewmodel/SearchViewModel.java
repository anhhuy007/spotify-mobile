package com.example.spotifyclone.features.search.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.spotifyclone.features.search.model.SearchResult;
import com.example.spotifyclone.features.search.network.SearchService;
import com.example.spotifyclone.shared.model.APIResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends ViewModel {
    private final SearchService searchService;
    private final MutableLiveData<SearchResult> searchResult= new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading=new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage=new MutableLiveData<>();

    public SearchViewModel(SearchService searchService) {
        this.searchService = searchService;
    }

    public MutableLiveData<SearchResult> getSearchResult()
    {
        return searchResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }


    public void fetchSearchResults(String query, String genre, String type, int
            page, int limit) {
        isLoading.setValue(true);

        searchService.getSearchResults(query, genre, type, page, limit).enqueue(new Callback<APIResponse<SearchResult>>() {
            @Override
            public void onResponse(Call<APIResponse<SearchResult>> call, Response<APIResponse<SearchResult>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
//                    Log.d("SearchViewModel", "onResponse: " + response.body().getData().getItems().get(0).getName());
                    searchResult.setValue(response.body().getData());
                } else {
                    errorMessage.setValue("Failed to load search results");
                }
            }

            @Override
            public void onFailure(Call<APIResponse<SearchResult>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
                Log.d("DEBUG_SEARCH", "onFailure: " + t.getMessage());

            }
        });

    }
}


