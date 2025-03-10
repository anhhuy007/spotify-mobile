package com.example.spotifyclone.features.authentication.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.adapter.SignupPagerAdapter;
import com.example.spotifyclone.features.authentication.viewmodel.SignupVMFactory;
import com.example.spotifyclone.features.authentication.viewmodel.SignupViewModel;

public class SignupActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private SignupPagerAdapter signupPagerAdapter;

    public SignupViewModel signupViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        viewPager = findViewById(R.id.viewPager);
        signupPagerAdapter = new SignupPagerAdapter(this);
        viewPager.setAdapter(signupPagerAdapter);
        viewPager.setUserInputEnabled(false);

        signupViewModel = new ViewModelProvider(this, new SignupVMFactory(this)).get(SignupViewModel.class);
    }

    public void nextStep() {
        if (viewPager.getCurrentItem() < signupPagerAdapter.getItemCount() - 1) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    public void previousStep() {
        if (viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    public void closeSignup() {
        finish();
    }

    public void navigateToLogin() {
        finish();
    }
}
