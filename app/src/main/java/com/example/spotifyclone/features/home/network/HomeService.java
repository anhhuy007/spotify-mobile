package com.example.spotifyclone.features.home.network;

import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.artist.model.Artist;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.PaginatedResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface HomeService {
    @GET("song/popular")
    Call<APIResponse<PaginatedResponse<Song>>> getPopularSongs();

    @GET("song/new")
    Call<APIResponse<PaginatedResponse<Song>>> getNewSongs();

    @GET("album/popular")
    Call<APIResponse<PaginatedResponse<Album>>> getPopularAlbums();

    @GET("album/latest")
    Call<APIResponse<PaginatedResponse<Album>>> getLatestAlbums();

    @GET("artist/popular")
    Call<APIResponse<PaginatedResponse<Artist>>> getPopularArtists();

}
