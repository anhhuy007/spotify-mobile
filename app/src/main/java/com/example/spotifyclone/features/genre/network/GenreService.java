package com.example.spotifyclone.features.genre.network;

import com.example.spotifyclone.features.genre.model.Album;
import com.example.spotifyclone.features.genre.model.Genre;
import com.example.spotifyclone.shared.model.APIResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GenreService {
    Gson gson=new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
    @GET("genres")
    Call <APIResponse<List<Genre>>> getGenres();

    @GET("albums")
    Call <APIResponse<List<Album>>> getGenreAlbums();
}
