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
            @Field("name") String playlistName
    );
}
