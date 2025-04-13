package com.example.spotifyclone.features.authentication.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.annotation.Nonnull;

public class FPVMFactory implements ViewModelProvider.Factory {
    private final Context context;

    public FPVMFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@Nonnull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FPViewModel.class)) {
            return (T) new FPViewModel(context);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
