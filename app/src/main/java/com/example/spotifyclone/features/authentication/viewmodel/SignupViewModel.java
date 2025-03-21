package com.example.spotifyclone.features.authentication.viewmodel;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifyclone.features.authentication.model.AvatarUploadCallBack;
import com.example.spotifyclone.features.authentication.model.UploadAvatarResponse;
import com.example.spotifyclone.features.authentication.network.AuthService;
import com.example.spotifyclone.features.profile.network.ProfileService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;
import com.facebook.internal.Mutable;

import java.io.File;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class SignupViewModel extends ViewModel {
    private final Context context;

    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<String> username = new MutableLiveData<>();
    private MutableLiveData<String> dataOfBirth = new MutableLiveData<>();
    private MutableLiveData<String> avatarUri = new MutableLiveData<>();
    private MutableLiveData<String> avatarUrl = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();

    public SignupViewModel(Context context) {
        this.context = context;
    }

    public void setEmail(String email) {
        this.email.setValue(email);
    }

    public void setPassword(String password) {
        this.password.setValue(password);
    }

    public void setUsername(String username) {
        this.username.setValue(username);
    }

    public void setDataOfBirth(String dataOfBirth) {
        this.dataOfBirth.setValue(dataOfBirth);
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri.setValue(avatarUri);
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl.setValue(avatarUrl);
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public MutableLiveData<String> getUsername() {
        return username;
    }

    public MutableLiveData<String> getAvatarUri() {
        return avatarUri;
    }

    public MutableLiveData<String> getAvatarUrl() {
        return avatarUrl;
    }

    public MutableLiveData<String> getDataOfBirth() {
        return dataOfBirth;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsSuccess() {
        return isSuccess;
    }

    public void uploadAvatar(String imageUri, AvatarUploadCallBack callBack) {
        if (imageUri.isEmpty()) {
            callBack.onSuccess("");
            return;
        }

        Log.d("DEBUG", "Uploading avatar: " + imageUri);
        Uri uri = Uri.parse(imageUri);
        File file = new File(Objects.requireNonNull(getRealPathFromUri(uri)));

        if (!file.exists()) {
            Log.e("ERROR", "File does not exist: " + file.getAbsolutePath());
            return;
        } else {
            Log.d("DEBUG", "File exists: " + file.getAbsolutePath());
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        // Upload the avatar
        ProfileService profileService = RetrofitClient.getClient(context).create(ProfileService.class);
        profileService.uploadAvatar(body).enqueue(new retrofit2.Callback<APIResponse<UploadAvatarResponse>>() {
            @Override
            public void onResponse(Call<APIResponse<UploadAvatarResponse>> call, Response<APIResponse<UploadAvatarResponse>> response) {
                if (response.isSuccessful()) {
                    // Get the avatar URL
                    String avatarUrl = response.body().getData().getAvatarUrl();
                    setAvatarUrl(avatarUrl);

                    callBack.onSuccess(avatarUrl);

                    Log.d("DEBUG", "Avatar uploaded successfully");
                    Log.d("DEBUG", "Avatar URL: " + avatarUrl);
                } else {
                    Log.e("ERROR", "Failed to upload avatar: " + response.message());
                    callBack.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<UploadAvatarResponse>> call, Throwable t) {
                Log.e("FAILURE", "Failed to upload avatar: " + t.getMessage());
                callBack.onFailure(t.getMessage());
            }
        });
    }

    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return null;
        }

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(columnIndex);
        cursor.close();

        return path;
    }

}
