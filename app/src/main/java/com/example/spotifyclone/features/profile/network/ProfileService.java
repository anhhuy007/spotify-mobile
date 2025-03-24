package com.example.spotifyclone.features.profile.network;

import com.example.spotifyclone.features.authentication.model.UploadAvatarResponse;
import com.example.spotifyclone.shared.model.APIResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ProfileService {
    @Multipart
    @POST("user/profile/upload-avatar")
    Call<APIResponse<UploadAvatarResponse>> uploadAvatar(
            @Part MultipartBody.Part image
    );

    @GET("user/profile/hello")
    Call<APIResponse<Object>> hello();
}
