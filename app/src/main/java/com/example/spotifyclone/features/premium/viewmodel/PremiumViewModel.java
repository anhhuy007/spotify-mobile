package com.example.spotifyclone.features.premium.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.spotifyclone.features.premium.network.PremiumService;
import com.example.spotifyclone.shared.network.RetrofitClient;

public class PremiumViewModel extends ViewModel {
    private PremiumService premiumService;

    public PremiumViewModel(Context context) {
        premiumService = RetrofitClient.getClient(context).create(PremiumService.class);
    }


}
