package com.example.spotifyclone.features.authentication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.spotifyclone.features.authentication.ui.forgot_password_fragments.EmailFragment;
import com.example.spotifyclone.features.authentication.ui.forgot_password_fragments.OTPFragment;
import com.example.spotifyclone.features.authentication.ui.forgot_password_fragments.ResetPasswordFragment;

public class ForgotPasswordPagerAdapter extends FragmentStateAdapter {
    public ForgotPasswordPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1: return new OTPFragment();
            case 2: return new ResetPasswordFragment();
            default: return new EmailFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
