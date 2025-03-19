package com.example.spotifyclone.features.search.network;

import com.example.spotifyclone.features.search.model.SearchResult;
import com.example.spotifyclone.shared.model.APIResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchService {
    @GET("search")
    Call<APIResponse<SearchResult>>getSearchResults(
            @Query("query") String query,
            @Query("genre") String genre,
            @Query("type") String type,
            @Query("page") int page,
            @Query("limit") int limit
    );

}

