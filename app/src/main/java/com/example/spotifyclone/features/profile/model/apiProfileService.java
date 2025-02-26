package com.example.spotifyclone.features.profile.model;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface apiProfileService {
    @GET("user/{id}")
    Call<profile> getUserProfile(@Path("id") int userID);

    @PUT("user/{id}")
    Call<profile> updateUserProfile(@Path("id") String userID, @Body profile updatedProfile);
}
