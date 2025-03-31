package com.example.spotifyclone.features.settings.network;

import com.example.spotifyclone.features.authentication.model.UploadAvatarResponse;
import com.example.spotifyclone.features.premium.model.Subscription;
import com.example.spotifyclone.features.profile.model.PasswordUpdate;
import com.example.spotifyclone.features.profile.model.ProfileUpdate;
import com.example.spotifyclone.features.settings.model.Language;
import com.example.spotifyclone.features.settings.model.Theme;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.User;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface SettingService {



    @FormUrlEncoded
    @POST("user/profile/change-language")
    Call<APIResponse<User>> updateLanguage(
            @Field("language") String data
    );

    @FormUrlEncoded
    @POST("user/profile/change-theme")
    Call<APIResponse<User>> updateTheme(
            @Field("theme") String data
    );


}