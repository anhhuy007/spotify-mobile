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

public class SearchClassifyViewModel extends ViewModel {
    private final SearchService searchService;
    private final MutableLiveData<List<SearchItem>> items=new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading=new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage=new MutableLiveData<>();

    public SearchClassifyViewModel(SearchService searchService) {
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


    public void fetchSearchResults(String query, String genre, String type,int page, int limit) {
        isLoading.setValue(true);

        searchService.getSearchResults(query, genre, type, page, limit).enqueue(new Callback<APIResponse<PaginatedResponse<SearchItem>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<SearchItem>>> call, Response<APIResponse<PaginatedResponse<SearchItem>>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<SearchItem> newItems = response.body().getData().getItems();

                    List<SearchItem> currentItems = items.getValue();
                    if (currentItems == null) {
                        currentItems = new ArrayList<>();
                    }
                    currentItems.addAll(newItems);
                    items.setValue(currentItems);
                } else {
                    errorMessage.setValue("Failed to load search results");
                }
            }

            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<SearchItem>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });

    }

}
