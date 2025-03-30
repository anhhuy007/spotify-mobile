package com.example.spotifyclone.features.profile.network;

import com.example.spotifyclone.features.artist.model.ItemDiscographyEP;
import com.example.spotifyclone.features.authentication.model.UploadAvatarResponse;
import com.example.spotifyclone.features.profile.model.ProfileUpdate;
import com.example.spotifyclone.features.profile.model.PasswordUpdate;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.User;

import java.util.List;

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


    @PUT("user/profile")
    Call<APIResponse<User>> updateProfile(@Body ProfileUpdate profileUpdate);

    @PUT("user/profile/change-password")
    Call<APIResponse<User>> updatePassword(@Body PasswordUpdate passwordUpdate);

    @GET("follower/{user_id}/count")
    Call<APIResponse<Integer>> getCountFollowedArtists(@Path("id") String artistId);

    @GET("user/profile/hello")
    Call<APIResponse<Object>> hello();
}