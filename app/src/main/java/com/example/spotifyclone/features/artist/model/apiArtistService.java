package com.example.spotifyclone.features.artist.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface apiArtistService {
    @GET("artist")
    Call<List<artistDetail>> getListArtist();

    @GET("artist/{id}")
    Call<artistDetail> getArtistDetail(@Path("id") String artistId);


}
