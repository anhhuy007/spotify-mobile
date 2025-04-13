package com.example.spotifyclone.features.settings.viewModel;

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
import com.example.spotifyclone.features.settings.model.Language;
import com.example.spotifyclone.features.settings.model.Theme;
import com.example.spotifyclone.features.settings.network.SettingService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.User;
import com.example.spotifyclone.shared.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

//public class SettingViewModel extends AndroidViewModel {
//    private final Context context;
//    private final MutableLiveData<String> lang = new MutableLiveData<>();
//    private final MutableLiveData<String> theme = new MutableLiveData<>();
//    private final MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();
//
//    public SettingViewModel(@NonNull Application application) {
//        super(application);
//        this.context = application.getApplicationContext();
//    }
//
//    public LiveData<String> getLanguage() {
//        return lang;
//    }
//
//    public LiveData<String> getTheme() {
//        return theme;
//    }
//
//    public LiveData<Boolean> getUpdateStatus() {
//        return updateStatus;
//    }
//
//    public void updateLanguage(String data) {
//        Retrofit retrofit = RetrofitClient.getClient(context);
//        SettingService apiService = retrofit.create(SettingService.class);
//        apiService.updateLanguage(data).enqueue(new Callback<APIResponse<User>>() {
//            @Override
//            public void onResponse(Call<APIResponse<User>> call, Response<APIResponse<User>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    User updatedUser = response.body().getData();
//                    lang.setValue(data);
//                    AuthRepository authRepo = new AuthRepository(context);
//                    authRepo.saveUser(updatedUser);
//                    updateStatus.setValue(true);
//                    Toast.makeText(context, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
//                } else {
//                    updateStatus.setValue(false);
//                    Toast.makeText(context, "Cập nhật hồ sơ thất bại", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<APIResponse<User>> call, Throwable t) {
//                updateStatus.setValue(false);
//                Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    public void updateTheme(String data) {
//        Retrofit retrofit = RetrofitClient.getClient(context);
//        SettingService apiService = retrofit.create(SettingService.class);
//        apiService.updateTheme(data).enqueue(new Callback<APIResponse<User>>() {
//            @Override
//            public void onResponse(Call<APIResponse<User>> call, Response<APIResponse<User>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    User updatedUser = response.body().getData();
//                    theme.setValue(data);
//                    AuthRepository authRepo = new AuthRepository(context);
//                    authRepo.saveUser(updatedUser);
//                    updateStatus.setValue(true);
//                    Toast.makeText(context, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
//                } else {
//                    updateStatus.setValue(false);
//                    Toast.makeText(context, "Cập nhật hồ sơ thất bại", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<APIResponse<User>> call, Throwable t) {
//                updateStatus.setValue(false);
//                Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//}

public class SettingViewModel extends AndroidViewModel {

    private final Context context;
    private final MutableLiveData<String> lang = new MutableLiveData<>();
    private final MutableLiveData<String> theme = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();
    // Hằng số thông báo
    private static final String UPDATE_SUCCESS = "Cập nhật hồ sơ thành công";
    private static final String UPDATE_FAILED = "Cập nhật hồ sơ thất bại";


    public SettingViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<String> getLanguage() {
        return lang;
    }

    public LiveData<String> getTheme() {
        return theme;
    }

    public LiveData<Boolean> getUpdateStatus() {
        return updateStatus;
    }

    public void updateLanguage(String data) {
        Retrofit retrofit = RetrofitClient.getClient(context);
        SettingService apiService = retrofit.create(SettingService.class);
        apiService.updateLanguage(data).enqueue(new Callback<APIResponse<User>>() {
            @Override
            public void onResponse(Call<APIResponse<User>> call, Response<APIResponse<User>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    User updatedUser = response.body().getData();
                    if (updatedUser != null) {

                        // Lưu người dùng
                        AuthRepository authRepo = new AuthRepository(context);
                        authRepo.saveUser(updatedUser);
                        // Cập nhật ngôn ngữ
                        lang.setValue(data);
                        // Cập nhật trạng thái
                        updateStatus.setValue(true);
                        Toast.makeText(context, UPDATE_SUCCESS, Toast.LENGTH_SHORT).show();
                    } else {
                        handleUpdateFailure();
                    }
                } else {
                    handleUpdateFailure();
                }
            }

            @Override
            public void onFailure(Call<APIResponse<User>> call, Throwable t) {
                handleUpdateFailure(t.getMessage());
            }
        });
    }

    public void updateTheme(String data) {
        Retrofit retrofit = RetrofitClient.getClient(context);
        SettingService apiService = retrofit.create(SettingService.class);
        apiService.updateTheme(data).enqueue(new Callback<APIResponse<User>>() {
            @Override
            public void onResponse(Call<APIResponse<User>> call, Response<APIResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User updatedUser = response.body().getData();
                    if (updatedUser != null) {

                        AuthRepository authRepo = new AuthRepository(context);
                        authRepo.saveUser(updatedUser);

                        theme.setValue(data);

                        updateStatus.setValue(true);
                        Toast.makeText(context, UPDATE_SUCCESS, Toast.LENGTH_SHORT).show();
                    } else {
                        handleUpdateFailure();
                    }
                } else {
                    handleUpdateFailure();
                }
            }

            @Override
            public void onFailure(Call<APIResponse<User>> call, Throwable t) {
                handleUpdateFailure(t.getMessage());
            }
        });
    }

    // Phương thức hỗ trợ xử lý lỗi
    private void handleUpdateFailure() {
        handleUpdateFailure(UPDATE_FAILED);
    }

    private void handleUpdateFailure(String errorMessage) {
        updateStatus.setValue(false);
        Toast.makeText(context, "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
    }
}
