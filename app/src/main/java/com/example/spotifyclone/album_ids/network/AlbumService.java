package com.example.spotifyclone.album_ids.network;

import com.example.spotifyclone.album_ids.model.Album;
import com.example.spotifyclone.album_ids.model.Song;
import com.example.spotifyclone.genre_ids.model.Genre;
import com.example.spotifyclone.shared.model.APIResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AlbumService {
    Gson gson=new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
    @GET("albums")
    Call <APIResponse<List<Album>>> getAlbums();

    @GET("album/{id}/songs")
    Call<APIResponse<List<Song>>> getSongs(@Path("id") String albumId);
}
