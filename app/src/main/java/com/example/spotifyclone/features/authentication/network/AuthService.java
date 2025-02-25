package com.example.spotifyclone.features.authentication.network;

import com.example.spotifyclone.features.authentication.model.LoginResponse;
import com.example.spotifyclone.features.authentication.model.Tokens;
import com.example.spotifyclone.shared.model.APIResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthService {
    @FormUrlEncoded
    @POST("auth/login")
    Call<APIResponse<LoginResponse>> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("auth/refresh")
    Call<APIResponse<Tokens>> refreshToken(@Field("refreshToken") String refreshToken);

    @POST("auth/logout")
    Call<APIResponse<Void>> logout();
}
