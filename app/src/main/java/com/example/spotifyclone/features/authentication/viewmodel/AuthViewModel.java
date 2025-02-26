package com.example.spotifyclone.features.authentication.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.shared.model.User;


public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepo;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();


    public AuthViewModel(Context context) {
        this.authRepo = new AuthRepository(context);

        if (authRepo.isLoggedIn()) {
            isLoggedIn.setValue(true);
            userLiveData.setValue(authRepo.getUser());
        }
        else {
            isLoggedIn.setValue(false);
        }

    }

    public void login(String email, String password) {
        isLoading.setValue(true);
        Log.d("DEBUG", "login: " + email + " " + password);
        authRepo.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.setValue(false);
                isLoggedIn.setValue(true);
                userLiveData.setValue(user);
            }

            @Override
            public void onFailure(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

   public void logout() {
        authRepo.logout();
        isLoggedIn.setValue(false);
        userLiveData.setValue(null);
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
