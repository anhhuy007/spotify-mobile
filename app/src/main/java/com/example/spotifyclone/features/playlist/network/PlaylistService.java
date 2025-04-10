package com.example.spotifyclone.features.playlist.network;

import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.playlist.model.Playlist;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.PaginatedResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PlaylistService {
    Gson gson=new GsonBuilder().setDateFormat("dd-MM-yyyy").create();

    @GET("playlist/user-playlist")
    Call<APIResponse<PaginatedResponse<Playlist>>> getUserPlaylist();;

    @POST("playlist/create-playlist")
    @FormUrlEncoded
    Call<APIResponse<Void>> createPlaylist(
            @Field("id") String song_id,
            @Field("name") String playlistName,
            @Field("image") String playlistImage
    );

    @POST("playlist/add-song")
    @FormUrlEncoded
    Call<APIResponse<Void>> addSongToPlaylist(
            @Field("playlistId") String playlistId,
            @Field("songId") String song_id
    );

    @GET("playlist/playlist-by-id/{id}")
    Call <APIResponse<Playlist>> getPlaylistById(@Path("id") String playlist_id);

    @GET("playlist/playlist-songs/{id}")
    Call <APIResponse<PaginatedResponse<Song>>>getPlaylistSongs(@Path("id") String playlist_id);

    @DELETE("playlist/{playlistId}/remove-song/{songId}")
    Call<APIResponse<Void>> removeSongFromPlaylist(
            @Path("playlistId") String playlistId,
            @Path("songId") String songId
    );

    @GET("song/popular/{id}")
    Call<APIResponse<PaginatedResponse<Song>>> getSongPopular(@Path("id") String playlist_id, @Query("limit") int limit);

    @GET("playlist/random-songs/{id}")
    Call<APIResponse<PaginatedResponse<Song>>> getRandomSongs(@Path("id") String playlist_id, @Query("limit") int limit);


    @POST("playlist/{playlistId}/update-info")
    @FormUrlEncoded
    Call<APIResponse<Void>> updateInfo(
            @Path("playlistId") String playlistId,
            @Field("name") String playlist_name,
            @Field("description") String playlist_description
    );

    @DELETE("playlist/remove-playlist/{playlistId}")
    Call<APIResponse<Void>> removePlaylist(
            @Path("playlistId") String playlistId

    );






}
