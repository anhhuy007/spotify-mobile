package com.example.spotifyclone.features.authentication.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifyclone.features.authentication.network.AuthService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FPViewModel extends ViewModel {
    private final AuthService authService;

    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> otp = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isOtpSent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isOTPValid = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPasswordReset = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public FPViewModel(Context context) {
        this.authService = RetrofitClient.getClient(context).create(AuthService.class);
    }

    public void sendOTP(String email) {
        isLoading.setValue(true);
        authService.sendOTP(email).enqueue(new Callback<APIResponse<Void>>() {
            @Override
            public void onResponse(Call<APIResponse<Void>> call, Response<APIResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    isLoading.setValue(false);
                    isOtpSent.setValue(true);
                } else {
                    isLoading.setValue(false);
                    errorMessage.setValue("Failed to send OTP");
                    Log.d("DEBUG", "onResponse: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Void>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
                Log.d("DEBUG", "onFailure: " + t.getMessage());
            }
        });
    }

    public void verifyOTP(String email, String otp) {
        isLoading.setValue(true);
        authService.verifyOTP(email, otp).enqueue(new Callback<APIResponse<Void>>() {
            @Override
            public void onResponse(Call<APIResponse<Void>> call, Response<APIResponse<Void>> response) {
                Log.d("DEBUG", "onResponse: " + response.message());
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    isLoading.setValue(false);
                    isOTPValid.setValue(true);
                } else {
                    isLoading.setValue(false);
                    errorMessage.setValue("Invalid OTP");
                    Log.d("DEBUG", "onResponse: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Void>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
                Log.d("DEBUG", "onFailure: " + t.getMessage());
            }
        });
    }

    public void resetPassword(String email, String password, String otp) {
        isLoading.setValue(true);
        authService.resetPassword(email, password, otp).enqueue(new Callback<APIResponse<Void>>() {
            @Override
            public void onResponse(Call<APIResponse<Void>> call, Response<APIResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    isLoading.setValue(false);
                    isPasswordReset.setValue(true);
                } else {
                    isLoading.setValue(false);
                    errorMessage.setValue("Failed to reset password");
                    Log.d("DEBUG", "onResponse: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Void>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
                Log.d("DEBUG", "onFailure: " + t.getMessage());
            }
        });
    }

    public void resetVMState() {
        isLoading.setValue(false);
        errorMessage.setValue(null);
    }

    public void setEmail(String email) {
        this.email.setValue(email);
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public void setOTP(String otp) {
        this.otp.setValue(otp);
    }

    public MutableLiveData<String> getOTP() {
        return otp;
    }

    public void setPassword(String password) {
        this.password.setValue(password);
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setIsOtpSent(Boolean isOtpSent) {
        this.isOtpSent.setValue(isOtpSent);
    }

    public MutableLiveData<Boolean> getIsOtpSent() {
        return isOtpSent;
    }

    public void setIsLoading(Boolean isLoading) {
        this.isLoading.setValue(isLoading);
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage.setValue(errorMessage);
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Boolean> getIsOTPValid() {
        return isOTPValid;
    }

    public MutableLiveData<Boolean> getIsPasswordReset() {
        return isPasswordReset;
    }
}
