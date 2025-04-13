package com.example.spotifyclone.features.profile.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.features.profile.model.PasswordUpdate;
import com.example.spotifyclone.features.profile.model.ProfileUpdate;
import com.example.spotifyclone.features.profile.network.ProfileService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.User;
import com.example.spotifyclone.shared.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<Boolean> getUpdateStatus() {
        return updateStatus;
    }

    public void updateProfileInfo(String newName, String avaUrl) {
        Retrofit retrofit = RetrofitClient.getClient(context);
        ProfileService apiService = retrofit.create(ProfileService.class);

        apiService.updateProfile(new ProfileUpdate(newName, avaUrl)).enqueue(new Callback<APIResponse<User>>() {
            @Override
            public void onResponse(Call<APIResponse<User>> call, Response<APIResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User updatedUser = response.body().getData();
                    user.setValue(updatedUser);

                    AuthRepository authRepo = new AuthRepository(context);
                    authRepo.saveUser(updatedUser);

                    updateStatus.setValue(true);
                    Toast.makeText(context, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                } else {
                    updateStatus.setValue(false);
                    Toast.makeText(context, "Cập nhật hồ sơ thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse<User>> call, Throwable t) {
                updateStatus.setValue(false);
                Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updatePassword(String oldPassword, String newPassword) {
        PasswordUpdate passwordUpdate = new PasswordUpdate(oldPassword, newPassword);
        Retrofit retrofit = RetrofitClient.getClient(context);
        ProfileService apiService = retrofit.create(ProfileService.class);

        apiService.updatePassword(passwordUpdate).enqueue(new Callback<APIResponse<User>>() {
            @Override
            public void onResponse(Call<APIResponse<User>> call, Response<APIResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateStatus.setValue(true);
                    Toast.makeText(context, "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
                } else {
                    updateStatus.setValue(false);
                    Toast.makeText(context, "Cập nhật mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse<User>> call, Throwable t) {
                updateStatus.setValue(false);
                Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}