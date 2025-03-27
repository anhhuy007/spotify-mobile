package com.example.spotifyclone.features.album.network;

import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.PaginatedResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AlbumService {
    Gson gson=new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
    @GET("album/popular")
    Call <APIResponse<PaginatedResponse<Album>>> getAlbums();

    @GET("album/{id}/songs")
    Call<APIResponse<PaginatedResponse<Song>>> getSongs(@Path("id") String albumId);

    @GET("album/by-artists")
    Call<APIResponse<PaginatedResponse<Album>>> getArtistAlbums(@Query("artistNames") List<String> artistNames);


}
