package com.example.spotifyclone.features.player.network;

import com.example.spotifyclone.features.artist.model.Artist;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.shared.model.APIResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import com.example.spotifyclone.shared.model.PaginatedResponse;

public interface SongService {
    @GET("song")
    Call<List<Song>> getSongs();

    @GET("song/{id}")
    Call<APIResponse<Song>> getSongById(@Path("id") String songId);

    @GET("song")
    Call<APIResponse<List<Song>>> getSongsByArtist(@Query("artistId") String artistId);

    @GET("song/random")
    Call<APIResponse<PaginatedResponse<Song>>> getRandomSongs(@Query("limit") int limit);

    @GET("album/{id}/songs")
    Call<APIResponse<PaginatedResponse<Song>>> getAlbumSongs(@Path("id") String albumId);

    @GET("artist/{id}/songs")
    Call<APIResponse<PaginatedResponse<Song>>> getArtistSongs(@Path("id") String artistId);

    @GET("playlist/playlist-songs/{id}")
    Call <APIResponse<PaginatedResponse<Song>>>getPlaylistSongs(@Path("id") String playlist_id);
}



