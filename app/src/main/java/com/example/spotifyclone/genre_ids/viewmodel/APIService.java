package com.example.spotifyclone.genre_ids.viewmodel;

import com.example.spotifyclone.genre_ids.model.Genre;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIService {
    Gson gson=new GsonBuilder().setDateFormat("dd-MM-yyyy").create();

    @GET("genres")
    Call<List<Genre>> getGenres();

}
