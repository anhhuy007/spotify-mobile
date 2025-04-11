package com.example.spotifyclone.features.follow.network;

import com.example.spotifyclone.features.follow.model.Follow;
import com.example.spotifyclone.features.follow.model.FollowCountResponse;
import com.example.spotifyclone.shared.model.APIResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FolowService {
    @GET("follower/{user_id}/count")
    Call<APIResponse<FollowCountResponse>> getCountFollowedArtists(@Path("user_id") String userId);

    @GET("follower/{user_id}/list")
    Call<APIResponse<List<Follow>>> getListFollowedArtists(@Path("user_id") String userId);

    @POST("follower/add")
    Call<APIResponse<Follow>> addFollower(@Body Follow follow);

    @POST("follower/delete")
    Call<APIResponse<Follow>> deleteFollower1(@Body Follow follow);

    @POST("follower/check")
    Call<APIResponse<Follow>> checkFollower(@Body Follow follow);

    @DELETE("follower/delete/{id}")
    Call<APIResponse<Void>> deleteFollower(@Path("id") String followId);
}
