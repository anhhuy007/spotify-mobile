package com.example.spotifyclone.features.search.network;

import com.example.spotifyclone.features.search.model.SearchItem;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.PaginatedResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchService {
    @GET("search")
    Call<APIResponse<PaginatedResponse<SearchItem>>>getSearchResults(
            @Query("query") String query,
            @Query("genre") String genre,
            @Query("type") String type,
            @Query("page") int page,
            @Query("limit") int limit
    );

}

