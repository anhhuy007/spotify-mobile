package com.example.spotifyclone.features.settings.ui;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.profile.ui.profileUI;
import com.example.spotifyclone.shared.utils.Constants;

import java.util.Locale;

public class SettingsUI extends AppCompatActivity {

    private Switch switchTheme, switchLang, switchNoti;
    private LinearLayout accountInfoContainer, logoutContainer,freeAccountContainer,premiumAccountContainer;

    private ImageButton backButton;
    private static final int NOTIFICATION_PERMISSION_CODE = 123;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ImageView ava;
    private TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved settings before inflating layout
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("darkMode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        boolean isEnglish = prefs.getBoolean("isEnglish", false);
        setLocale(isEnglish ? "en" : "vi");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        registerPermissionLauncher();

        initializeViews();

        switchTheme.setChecked(isDarkMode);
        switchLang.setChecked(isEnglish);
        switchNoti.setChecked(prefs.getBoolean("notificationEnabled", false));

        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean hasNotificationPermission = checkNotificationPermission();
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("notificationEnabled", hasNotificationPermission);
            editor.apply();

            switchNoti.setOnCheckedChangeListener(null);
            switchNoti.setChecked(hasNotificationPermission);
            setupNotificationSwitchListener();
    }

    private void registerPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission granted
                        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
                        editor.putBoolean("notificationEnabled", true);
                        editor.apply();
                    } else {
                        // Permission denied, open app settings
                        showNotificationPermissionDialog();
                    }
                }
        );
    }

    private void initializeViews() {
        // Switches
        switchTheme = findViewById(R.id.switchTheme);
        switchLang = findViewById(R.id.switchLang);
        switchNoti = findViewById(R.id.switchNoti);

        // Containers
        accountInfoContainer = findViewById(R.id.account_info_container);
        logoutContainer = findViewById(R.id.logoutContainer);
        freeAccountContainer = findViewById(R.id.container_free_account);
        premiumAccountContainer = findViewById(R.id.container_pre_account);
        ava = findViewById(R.id.user_logo);
        userName = findViewById(R.id.user_name);

        userName.setText(Constants.userName);

        Glide.with(SettingsUI.this)
                .load(Constants.userAvatar)
                .placeholder(R.drawable.loading)
                .into(ava);

        if (Constants.isPremium) {
            freeAccountContainer.setVisibility(View.GONE);
            premiumAccountContainer.setVisibility(View.VISIBLE);
        } else {
            freeAccountContainer.setVisibility(View.VISIBLE);
            premiumAccountContainer.setVisibility(View.GONE);
        }


        // Buttons
        backButton = findViewById(R.id.back_button);
    }

    private void setupListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Switches
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
            editor.putBoolean("darkMode", isChecked);
            editor.apply();
            updateTheme();
        });

        switchLang.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
            editor.putBoolean("isEnglish", isChecked);
            editor.apply();
            setLocale(isChecked ? "en" : "vi");
            recreate();
        });

        setupNotificationSwitchListener();

        // Account info navigation
        accountInfoContainer.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsUI.this, profileUI.class);
            intent.putExtra("USER_ID", com.example.spotifyclone.shared.utils.Constants.userID);
            startActivity(intent);
        });

        // Logout button
        logoutContainer.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void setupNotificationSwitchListener() {
        switchNoti.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                requestNotificationPermission();
            } else {
                disableNotifications();
            }
        });
    }

    private boolean checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }

        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            return notificationManager.areNotificationsEnabled();
        }

        return true;
    }

    private void requestNotificationPermission() {
        // For Android 13 (API 33) and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.POST_NOTIFICATIONS)) {
                    showNotificationPermissionDialog();
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }
                return;
            }
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (!notificationManager.areNotificationsEnabled()) {
                openNotificationSettings();
                return;
            }
        }

        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putBoolean("notificationEnabled", true);
        editor.apply();
    }

    private void disableNotifications() {
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putBoolean("notificationEnabled", false);
        editor.apply();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Toast.makeText(this, R.string.notifications_disabled, Toast.LENGTH_SHORT).show();
        }

    }

    private void showNotificationPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.notification_permission_title)
                .setMessage(R.string.notification_permission_message)
                .setPositiveButton(R.string.open_settings, (dialog, which) -> openNotificationSettings())
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    switchNoti.setChecked(false);
                    SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
                    editor.putBoolean("notificationEnabled", false);
                    editor.apply();
                })
                .show();
    }

    private void openNotificationSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(intent);
        boolean hasNotificationPermission = checkNotificationPermission();
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("notificationEnabled", hasNotificationPermission);
            editor.apply();

            switchNoti.setOnCheckedChangeListener(null);
            switchNoti.setChecked(hasNotificationPermission);
            setupNotificationSwitchListener();

    }

    private void toggleSection(View optionsLayout, ImageView expandArrow) {
        // Toggle visibility
        if (optionsLayout.getVisibility() == View.GONE) {
            optionsLayout.setVisibility(View.VISIBLE);
            expandArrow.setRotation(90);
        } else {
            optionsLayout.setVisibility(View.GONE);
            expandArrow.setRotation(0);
        }
    }


    private void updateTheme() {
        AppCompatDelegate.setDefaultNightMode(switchTheme.isChecked() ?
                AppCompatDelegate.MODE_NIGHT_YES :
                AppCompatDelegate.MODE_NIGHT_NO);
        recreate();
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        performLogout();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void performLogout() {
        SharedPreferences userPrefs = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, R.string.logout_success, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(SettingsUI.this, profileUI.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}