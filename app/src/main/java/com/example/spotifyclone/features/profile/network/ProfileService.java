package com.example.spotifyclone.features.profile.network;

import com.example.spotifyclone.features.authentication.model.UploadAvatarResponse;
import com.example.spotifyclone.features.profile.model.Profile;
import com.example.spotifyclone.features.profile.model.ProfileUpdate;
import com.example.spotifyclone.features.profile.model.PasswordUpdate;
import com.example.spotifyclone.shared.model.APIResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ProfileService {
    @Multipart
    @POST("user/profile/upload-avatar")
    Call<APIResponse<UploadAvatarResponse>> uploadAvatar(
            @Part MultipartBody.Part image
    );

    @GET("user/{id}")
    Call<Profile> getUserProfile(@Path("id") String userID);

    @PUT("user/profile")
    Call<Profile> updateProfile(@Body ProfileUpdate profileUpdate);

    @PUT("user/change-password")
    Call<Profile> updatePassword(@Body PasswordUpdate passwordUpdate);

    @GET("user/profile/hello")
    Call<APIResponse<Object>> hello();
}