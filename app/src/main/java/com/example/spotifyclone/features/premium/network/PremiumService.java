package com.example.spotifyclone.features.premium.network;

import com.example.spotifyclone.features.premium.model.Subscription;
import com.example.spotifyclone.shared.model.APIResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PremiumService {
    @FormUrlEncoded
    @POST("/subscription/create")
    Call<APIResponse<Subscription>> createSubscription(
            String userId,
            String subscriptionType,
            String startDate,
            String endDate
    );

    @GET("/subscription/check/{userId}")
    Call<APIResponse<Subscription>> checkSubscription(
            @Path("userId") String userId
    );

    @DELETE("/subscription/cancel/{userId}")
    Call<APIResponse<Void>> cancelSubscription(
            @Path("userId") String userId
    );

    @GET("/subscription/{userId}/all")
    Call<APIResponse<Subscription>> getAllSubscriptions(
            @Path("userId") String userId
    );
}
