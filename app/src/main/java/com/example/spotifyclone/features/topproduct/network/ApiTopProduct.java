package com.example.spotifyclone.features.topproduct.network;

import com.example.spotifyclone.features.artist.model.Item;
import com.example.spotifyclone.features.topproduct.model.AlsoLike;
import com.example.spotifyclone.features.topproduct.model.TopAlbum;
import com.example.spotifyclone.features.topproduct.model.TopArtist;
import com.example.spotifyclone.features.topproduct.model.TopSong;
import com.example.spotifyclone.shared.model.APIResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiTopProduct {
        @GET("album/topAlbum")
        Call<APIResponse<List<TopAlbum>>> getTopAlbum();

        @GET("song/topSong")
        Call<APIResponse<List<TopSong>>> getTopSong();

        @GET("artist/topArtist")
        Call<APIResponse<List<TopArtist>>> getTopArtist();

        @GET("album/mostAlbum")
        Call<APIResponse<TopAlbum>> getMostAlbum();

        @GET("song/mostSong")
        Call<APIResponse<Item>> getMostSong();

        @GET("artist/mostArtist")
        Call<APIResponse<Item>> getMostArtist();

        @GET("album/alsoLike")
        Call<APIResponse<List<AlsoLike>>> getAlsoLike();




}
