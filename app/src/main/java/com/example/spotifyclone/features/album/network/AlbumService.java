package com.example.spotifyclone.features.album.network;

import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.album.model.Song;
import com.example.spotifyclone.shared.model.APIResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AlbumService {
    Gson gson=new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
    @GET("album/popular")
    Call <APIResponse<List<Album>>> getAlbums();

    @GET("album/{id}/songs")
    Call<APIResponse<List<Song>>> getSongs(@Path("id") String albumId);
}
