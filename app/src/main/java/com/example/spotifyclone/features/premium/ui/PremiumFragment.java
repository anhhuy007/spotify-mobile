package com.example.spotifyclone.features.premium.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.shared.model.User;

public class PremiumFragment extends Fragment {
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initUser();
        checkUserAndNavigate();
        return inflater.inflate(R.layout.fragment_premium_redirect, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUser();
        checkUserAndNavigate();
    }


    private void initUser() {
        AuthRepository authRepository = new AuthRepository(getContext());
        currentUser = authRepository.getUser();

    }

    private void checkUserAndNavigate() {
        if (currentUser != null) {
        }
    }


}
