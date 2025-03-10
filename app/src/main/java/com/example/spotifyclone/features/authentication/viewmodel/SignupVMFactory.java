package com.example.spotifyclone.features.authentication.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SignupVMFactory implements ViewModelProvider.Factory {
    private final Context context;

    public SignupVMFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SignupViewModel.class)) {
            return (T) new SignupViewModel(context);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
