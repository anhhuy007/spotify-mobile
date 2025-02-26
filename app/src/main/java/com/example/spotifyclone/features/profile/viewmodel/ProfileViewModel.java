package com.example.spotifyclone.features.profile.viewmodel;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.shared.model.User;

public class ProfileViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();

    public ProfileViewModel(Context context) {
        authRepository = new AuthRepository(context);
        userLiveData.setValue(authRepository.getUser());
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }
}
