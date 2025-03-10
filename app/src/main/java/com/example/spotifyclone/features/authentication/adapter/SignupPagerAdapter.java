package com.example.spotifyclone.features.authentication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.spotifyclone.features.authentication.ui.signup_fragments.BirthdayFragment;
import com.example.spotifyclone.features.authentication.ui.signup_fragments.CredentialsFragment;
import com.example.spotifyclone.features.authentication.ui.signup_fragments.UsernameFragment;
import com.example.spotifyclone.features.authentication.ui.signup_fragments.AvatarFragment;

public class SignupPagerAdapter extends FragmentStateAdapter {
    public SignupPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1: return new BirthdayFragment();
            case 2: return new AvatarFragment();
            case 3: return new UsernameFragment();
            default: return new CredentialsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
