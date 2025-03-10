package com.example.spotifyclone.features.authentication.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AuthVMFactory implements ViewModelProvider.Factory {
    private final Context context;

    public AuthVMFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AuthViewModel.class)) {
            return (T) new AuthViewModel(context);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
