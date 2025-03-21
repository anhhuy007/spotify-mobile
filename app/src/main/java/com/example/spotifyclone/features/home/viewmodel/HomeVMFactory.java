package com.example.spotifyclone.features.home.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.features.authentication.viewmodel.AuthViewModel;
import com.example.spotifyclone.features.home.network.HomeService;

public class HomeVMFactory implements ViewModelProvider.Factory {
    private final Context context;
    public HomeVMFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(context);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
