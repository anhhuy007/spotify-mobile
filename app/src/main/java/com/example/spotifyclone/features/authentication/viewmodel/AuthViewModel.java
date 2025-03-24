package com.example.spotifyclone.features.authentication.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifyclone.features.authentication.model.CheckUserExistCallBack;
import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.shared.model.User;

public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepo;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();


    public AuthViewModel(Context context) {
        this.authRepo = new AuthRepository(context);

        if (authRepo.isLoggedIn()) {
            isSuccess.setValue(true);
            userLiveData.setValue(authRepo.getUser());
        } else {
            isSuccess.setValue(false);
        }

    }

    public void login(String email, String password) {
        isLoading.setValue(true);
        authRepo.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.setValue(false);
                isSuccess.setValue(true);
                userLiveData.setValue(user);
            }

            @Override
            public void onFailure(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void googleLogin(String idToken) {
        isLoading.setValue(true);
        authRepo.googleLogin(idToken, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.setValue(false);
                isSuccess.setValue(true);
                userLiveData.setValue(user);
            }

            @Override
            public void onFailure(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void signup(String username, String email, String password, String dob, String avatarUrl) {
        isLoading.setValue(true);
        authRepo.signup(username, email, password, dob, avatarUrl, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.setValue(false);
                isSuccess.setValue(true);
                userLiveData.setValue(user);
            }

            @Override
            public void onFailure(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });

    }

    public void checkUsernameAvailability(String username, CheckUserExistCallBack callBack) {
        authRepo.checkUsernameExist(username, callBack);
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
}
