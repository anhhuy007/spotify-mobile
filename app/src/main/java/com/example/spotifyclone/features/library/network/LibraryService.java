package com.example.spotifyclone.features.library.network;

import com.example.spotifyclone.features.library.model.LibraryArtist;
import com.example.spotifyclone.features.library.model.LibraryPlaylist;
import com.example.spotifyclone.features.library.model.SelectableArtist;
import com.example.spotifyclone.shared.model.APIResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LibraryService {
    @GET("playlist/library/playlists")
    Call<APIResponse<List<LibraryPlaylist>>> getPlaylists();

    @POST("playlist/createNewPlaylist")
    @FormUrlEncoded
    Call<APIResponse<LibraryPlaylist>> createPlaylist(
            @Field("name") String playlistName,
            @Field("cover_url") String playlistImage
    );

//    @POST("follower/listArtist")
//    @FormUrlEncoded
//    Call<APIResponse<Boolean>> followArtists(@Field("artist_ids") List<String> artistIds);
    @GET("follower/listArtist")
    Call<APIResponse<List<SelectableArtist>>> getListFollowedArtists();
    @GET("follower/list")
    Call<APIResponse<List<LibraryArtist>>> getFollowedArtists();


}