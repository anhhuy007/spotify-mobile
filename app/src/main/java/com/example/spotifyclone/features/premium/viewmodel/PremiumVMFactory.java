package com.example.spotifyclone.features.premium.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

public class PremiumVMFactory implements ViewModelProvider.Factory {
    private Context context;

    public PremiumVMFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PremiumViewModel.class)) {
            return (T) new PremiumViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
