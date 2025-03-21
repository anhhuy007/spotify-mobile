package com.example.spotifyclone.shared.network;

import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST
    Call<APIResponse<User>> updateFCMToken(
            @Field("fcmToken") String fcmToken
    );
}
