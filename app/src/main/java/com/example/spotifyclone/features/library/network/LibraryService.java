package com.example.spotifyclone.features.library.network;

import com.example.spotifyclone.features.artist.model.Item;
import com.example.spotifyclone.features.library.model.LibraryArtist;
import com.example.spotifyclone.features.library.model.LibraryPlaylist;
import com.example.spotifyclone.shared.model.APIResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LibraryService {
    @GET("library/getPlaylists")
    Call<APIResponse<List<LibraryPlaylist>>> getPlaylists();


    @GET("follower/list")
    Call<APIResponse<List<LibraryArtist>>> getFollowedArtists();

    @GET("library/getLikedSongsCount")
    Call<APIResponse<Integer>> getLikedSongsCount();


}