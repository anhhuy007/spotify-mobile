package com.example.spotifyclone.features.authentication.network;

import com.example.spotifyclone.features.authentication.model.CheckUsernameExistResponse;
import com.example.spotifyclone.features.authentication.model.LoginResponse;
import com.example.spotifyclone.features.authentication.model.Tokens;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.User;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AuthService {
    @FormUrlEncoded
    @POST("auth/login")
    Call<APIResponse<LoginResponse>> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("auth/signup")
    Call<APIResponse<User>> signup(
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password,
            @Field("dob") String dob,
            @Field("avatarUrl") String avatarUrl
    );

    @FormUrlEncoded
    @POST("auth/google")
    Call<APIResponse<LoginResponse>> googleLogin(
            @Field("tokenId") String idToken
    );

    @FormUrlEncoded
    @POST("auth/refresh-token")
    Call<APIResponse<Tokens>> refreshToken(@Field("refreshToken") String refreshToken);

    @POST("auth/logout")
    Call<APIResponse<Void>> logout();

    @GET("auth/check-username")
    Call<APIResponse<CheckUsernameExistResponse>> checkUserNameExist(
            @Query("username") String username
    );

    @FormUrlEncoded
    @POST("auth/forgot-password")
    Call<APIResponse<Void>> sendOTP(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("auth/verify-otp")
    Call<APIResponse<Void>> verifyOTP(
            @Field("email") String email,
            @Field("otp") String otp
    );

    @FormUrlEncoded
    @POST("auth/reset-password")
    Call<APIResponse<Void>> resetPassword(
            @Field("email") String email,
            @Field("password") String password,
            @Field("otp") String otp
    );
}
