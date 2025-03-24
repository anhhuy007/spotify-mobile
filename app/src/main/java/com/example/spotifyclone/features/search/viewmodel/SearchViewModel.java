package com.example.spotifyclone.features.search.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifyclone.features.search.model.SearchItem;
import com.example.spotifyclone.features.search.network.SearchService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.PaginatedResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends ViewModel {
    private final SearchService searchService;
    private final MutableLiveData<List<SearchItem>> items=new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading=new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage=new MutableLiveData<>();

    public SearchViewModel(SearchService searchService) {
        this.searchService = searchService;
    }

    public MutableLiveData<List<SearchItem>> getItems() {
        return items;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void clearData() {
        items.setValue(new ArrayList<>()); // Xóa toàn bộ dữ liệu cũ
    }



    public void fetchSearchResults(String query, String genre, String type, int page, int limit) {
        isLoading.setValue(true);

        searchService.getSearchResults(query, genre, type, page, limit).enqueue(new Callback<APIResponse<PaginatedResponse<SearchItem>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<SearchItem>>> call, Response<APIResponse<PaginatedResponse<SearchItem>>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<SearchItem> newItems = response.body().getData().getItems();

                    // If it's page 1, replace the entire dataset (new filter or initial load)
                    if (page == 1) {
                        items.setValue(new ArrayList<>(newItems));
                        Log.d("SearchViewModel", "Setting new filtered data, items count: " + newItems.size());
                    } else {
                        // For pagination (page > 1), append to existing list
                        List<SearchItem> currentItems = items.getValue();
                        if (currentItems == null) {
                            currentItems = new ArrayList<>();
                        }
                        currentItems.addAll(newItems);
                        items.setValue(new ArrayList<>(currentItems)); // Creating new list to ensure LiveData triggers update
                        Log.d("SearchViewModel", "Appending data, total items now: " + currentItems.size());
                    }
                } else {
                    errorMessage.setValue("Failed to load search results");
                }
            }

            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<SearchItem>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

}


