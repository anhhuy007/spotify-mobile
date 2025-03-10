package com.example.spotifyclone.features.authentication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.adapter.ForgotPasswordPagerAdapter;
import com.example.spotifyclone.features.authentication.viewmodel.FPVMFactory;
import com.example.spotifyclone.features.authentication.viewmodel.FPViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ForgotPasswordPagerAdapter forgotPasswordPagerAdapter;

    public FPVMFactory fpvmFactory;
    public FPViewModel fpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        viewPager = findViewById(R.id.viewPager);
        forgotPasswordPagerAdapter = new ForgotPasswordPagerAdapter(this);
        viewPager.setAdapter(forgotPasswordPagerAdapter);
        viewPager.setUserInputEnabled(false);

        fpvmFactory = new FPVMFactory(this);
        fpViewModel = new ViewModelProvider(this, fpvmFactory).get(FPViewModel.class);
    }

    public void nextStep() {
        Log.d("DEBUG", "nextStep: " + viewPager.getCurrentItem());
        if (viewPager.getCurrentItem() < forgotPasswordPagerAdapter.getItemCount() - 1) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    public void previousStep() {
        Log.d("DEBUG", "previousStep: " + viewPager.getCurrentItem());
        if (viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    public void closeFP() {
        finish();
    }

    public void navigateToLogin() {
        // destroy activity and navigate to login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
