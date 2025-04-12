package com.example.spotifyclone.features.premium.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.premium.viewmodel.PremiumViewModel;

public class PremiumFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initState();
        checkUserAndNavigate();
        return inflater.inflate(R.layout.fragment_premium_redirect, container, false);
    }

    private PremiumViewModel premiumViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initState();
        checkUserAndNavigate();
    }

    private void initState() {
        premiumViewModel = new PremiumViewModel(getContext());
        premiumViewModel.checkSubscription();
    }

    private void checkUserAndNavigate() {
        premiumViewModel.getIsPremiumUser().observe(getViewLifecycleOwner(), isPremium -> {
            NavController navController = NavHostFragment.findNavController(this);
            if (isPremium) {
                Log.d("TAG", "User is premium");
                navController.navigate(R.id.subscriptionDetailFragment);
            }
            else {
                Log.d("TAG", "User is not premium");
                navController.navigate(R.id.subscriptionPlanFragment);
            }
        });
    }
}
