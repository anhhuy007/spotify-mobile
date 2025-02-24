package com.example.spotifyclone.features.authentication.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifyclone.features.authentication.model.LoginResponse;
import com.example.spotifyclone.features.authentication.network.AuthService;
import com.example.spotifyclone.features.authentication.network.TokenManager;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {
    private final AuthService authService;
    private final TokenManager tokenManager;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();

    public AuthViewModel(Context context) {
        this.authService = RetrofitClient.getClient(context).create(AuthService.class);
        this.tokenManager = new TokenManager(context);
    }

    public void login(String email, String password) {
        isLoading.setValue(true);
        Log.d("DEBUG", "login: " + email + " " + password);
        authService.login(email, password).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<APIResponse<LoginResponse>> call, Response<APIResponse<LoginResponse>> response) {
                Log.d("DEBUG", "onResponse: " + response);
                Log.d("DEBUG", "onResponse: " + response.body());
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    APIResponse<LoginResponse> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.isSuccess()) {
                        LoginResponse loginResponse = apiResponse.getData();
                        tokenManager.saveTokens(loginResponse.getTokens().getAccessToken(), loginResponse.getTokens().getRefreshToken());
                        isLoggedIn.setValue(true);
                    } else {
                        errorMessage.setValue(apiResponse != null ? apiResponse.getMessage() : "An error occurred");
                    }
                } else if (response.code() == 401) {
                    errorMessage.setValue("Invalid email or password");
                }
                else {
                    errorMessage.setValue("An error occurred");
                }
            }

            @Override
            public void onFailure(Call<APIResponse<LoginResponse>> call, Throwable t) {
                Log.d("DEBUG", "onFailure: " + t.getMessage());
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }
}
