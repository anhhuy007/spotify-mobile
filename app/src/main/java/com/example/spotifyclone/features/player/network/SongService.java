package com.example.spotifyclone.features.player.network;

import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.shared.model.APIResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SongService {
    @GET("songs")
    Call<List<Song>> getSongs();

    @GET("songs/{id}")
    Call<APIResponse<Song>> getSongById(@Path("id") String songId);

    @GET("songs")
    Call<APIResponse<List<Song>>> getSongsByArtist(@Query("artistId") String artistId);

}