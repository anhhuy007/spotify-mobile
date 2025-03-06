package com.example.spotifyclone.features.artist.network;

import com.example.spotifyclone.features.artist.model.ArtistDetail;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface apiArtistService {
    @GET("artist")
    Call<List<ArtistDetail>> getListArtist();

    @GET("artist/{id}")
    Call<ArtistDetail> getArtistDetail(@Path("id") String artistId);


}
