package com.example.spotifyclone.features.profile.network;

import com.example.spotifyclone.features.profile.model.Profile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface apiProfileService {
    @GET("user/{id}")
    Call<Profile> getUserProfile(@Path("id") String userID);

    @PUT("user/{id}")
    Call<Profile> updateUserProfile(@Path("id") String userID, @Body Profile updatedProfile);
}
