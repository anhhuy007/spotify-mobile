package com.example.spotifyclone.features.artist.model;

import com.example.spotifyclone.features.profile.model.profile;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface apiArtistService {
    @GET("artist")
    Call<List<artist>> getListArtist();

    @GET("artist/{id}")
    Call<artist> getArtistDetail(@Path("id") String artistId);


}
