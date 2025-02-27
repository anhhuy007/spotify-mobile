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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.spotifyclone.shared.utils.Constants;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.profile.ui.profileUI;
import com.example.spotifyclone.shared.utils.Constants;

import java.util.Locale;

public class settingsUI extends AppCompatActivity {

    private Switch switchTheme, switchLang, switchNoti;
    private ImageView imgOnNoti, imgOffNoti, imgDark, imgLight;
    private TextView txtVN, txtEN;
    private LinearLayout accountInfoContainer, themeContainer, languageContainer, notificationsContainer;
    private LinearLayout themeOptions, languageOptions, notificationOptions;
    private ImageView themeExpand, languageExpand, notificationsExpand;
    private Button logoutButton;
    private ImageButton backButton;
    private static final int NOTIFICATION_PERMISSION_CODE = 123;
    private ActivityResultLauncher<String> requestPermissionLauncher;

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

        // Register permission launcher
        registerPermissionLauncher();

        // Initialize views
        initializeViews();

        // Set initial states
        switchTheme.setChecked(isDarkMode);
        switchLang.setChecked(isEnglish);
        switchNoti.setChecked(prefs.getBoolean("notificationEnabled", false));

        // Set up listeners
        setupListeners();

        // Update UI elements
        updateLanguageUI();
        updateNotificationUI();
        updateThemeIcons();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check notification permission status and update switch accordingly
        boolean hasNotificationPermission = checkNotificationPermission();
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);

        // Only update the UI if there's a mismatch between permissions and saved preference
        if (hasNotificationPermission != prefs.getBoolean("notificationEnabled", false)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("notificationEnabled", hasNotificationPermission);
            editor.apply();

            // Update switch without triggering listener
            switchNoti.setOnCheckedChangeListener(null);
            switchNoti.setChecked(hasNotificationPermission);
            setupNotificationSwitchListener();

            // Update UI
            updateNotificationUI();
        }
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
                        updateNotificationUI();
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

        // Images
        imgOnNoti = findViewById(R.id.onNoti);
        imgOffNoti = findViewById(R.id.offNoti);
        imgDark = findViewById(R.id.dark);
        imgLight = findViewById(R.id.light);

        // Text
        txtVN = findViewById(R.id.VN);
        txtEN = findViewById(R.id.EN);

        // Containers
        accountInfoContainer = findViewById(R.id.account_info_container);
        themeContainer = findViewById(R.id.theme_container);
        languageContainer = findViewById(R.id.language_container);
        notificationsContainer = findViewById(R.id.notifications_container);

        // Option panels
        themeOptions = findViewById(R.id.theme_options);
        languageOptions = findViewById(R.id.language_options);
        notificationOptions = findViewById(R.id.notification_options);

        // Expand arrows
        themeExpand = findViewById(R.id.theme_expand);
        languageExpand = findViewById(R.id.language_expand);
        notificationsExpand = findViewById(R.id.notifications_expand);

        // Buttons
        logoutButton = findViewById(R.id.logoutButton);
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

        // Click listeners for expanding/collapsing sections
        themeContainer.setOnClickListener(v -> toggleSection(themeOptions, themeExpand));
        languageContainer.setOnClickListener(v -> toggleSection(languageOptions, languageExpand));
        notificationsContainer.setOnClickListener(v -> toggleSection(notificationOptions, notificationsExpand));

        // Account info navigation
        accountInfoContainer.setOnClickListener(v -> {
            Intent intent = new Intent(settingsUI.this, profileUI.class);
            intent.putExtra("USER_ID", com.example.spotifyclone.shared.utils.Constants.userID);
            startActivity(intent);
        });

        // Logout button
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void setupNotificationSwitchListener() {
        switchNoti.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // User wants to enable notifications
                requestNotificationPermission();
            } else {
                // User wants to disable notifications
                disableNotifications();
            }
        });
    }

    private boolean checkNotificationPermission() {
        // For Android 13 (API 33) and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }

        // For Android 8.0 (API 26) to Android 12.1
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            return notificationManager.areNotificationsEnabled();
        }

        // For older versions, assume permission is granted as it's in the manifest
        return true;
    }

    private void requestNotificationPermission() {
        // For Android 13 (API 33) and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Check if we should show rationale
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.POST_NOTIFICATIONS)) {
                    showNotificationPermissionDialog();
                } else {
                    // Request the permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }
                return;
            }
        }
        // For Android 8.0 (API 26) to Android 12.1
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (!notificationManager.areNotificationsEnabled()) {
                openNotificationSettings();
                return;
            }
        }

        // If we got here, permission is already granted
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putBoolean("notificationEnabled", true);
        editor.apply();
        updateNotificationUI();
    }

    private void disableNotifications() {
        // Save the preference
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putBoolean("notificationEnabled", false);
        editor.apply();

        // For Android 8.0+ we can direct users to notification settings to disable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Toast.makeText(this, R.string.notifications_disabled, Toast.LENGTH_SHORT).show();
            // Optionally open notification settings
            // openNotificationSettings();
        }

        updateNotificationUI();
    }

    private void showNotificationPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.notification_permission_title)
                .setMessage(R.string.notification_permission_message)
                .setPositiveButton(R.string.open_settings, (dialog, which) -> openNotificationSettings())
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // Update switch state since permission was denied
                    switchNoti.setChecked(false);
                    SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
                    editor.putBoolean("notificationEnabled", false);
                    editor.apply();
                    updateNotificationUI();
                })
                .show();
    }

    private void openNotificationSettings() {
        Intent intent = new Intent();

        // For Android 8.0 (API 26) and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        }
        // For Android 5.0-7.1.1 (API 21-25)
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
        }
        // For older versions
        else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + getPackageName()));
        }

        startActivity(intent);
    }

    private void toggleSection(View optionsLayout, ImageView expandArrow) {
        // Toggle visibility
        if (optionsLayout.getVisibility() == View.GONE) {
            optionsLayout.setVisibility(View.VISIBLE);
            expandArrow.setRotation(90); // Rotate arrow to point down
        } else {
            optionsLayout.setVisibility(View.GONE);
            expandArrow.setRotation(0); // Reset arrow rotation
        }
    }

    private void updateThemeIcons() {
        boolean isDarkMode = switchTheme.isChecked();
        if (isDarkMode) {
            imgDark.setColorFilter(getResources().getColor(R.color.colorPrimary));
            imgLight.setColorFilter(getResources().getColor(R.color.black));
        } else {
            imgLight.setColorFilter(getResources().getColor(R.color.colorPrimary));
            imgDark.setColorFilter(getResources().getColor(R.color.black));
        }
    }

    private void updateTheme() {
        AppCompatDelegate.setDefaultNightMode(switchTheme.isChecked() ?
                AppCompatDelegate.MODE_NIGHT_YES :
                AppCompatDelegate.MODE_NIGHT_NO);
        updateThemeIcons();
        recreate();
    }

    private void updateLanguageUI() {
        txtVN.setTextColor(getResources().getColor(switchLang.isChecked() ? R.color.black : R.color.colorPrimary));
        txtEN.setTextColor(getResources().getColor(switchLang.isChecked() ? R.color.colorPrimary : R.color.black));
    }

    private void updateNotificationUI() {
        imgOnNoti.setColorFilter(getResources().getColor(switchNoti.isChecked() ? R.color.colorPrimary : R.color.black));
        imgOffNoti.setColorFilter(getResources().getColor(switchNoti.isChecked() ? R.color.black : R.color.colorPrimary));
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
        // Clear user data
        SharedPreferences userPrefs = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.clear();
        editor.apply();

        // Show confirmation message
        Toast.makeText(this, R.string.logout_success, Toast.LENGTH_SHORT).show();

        // Navigate to login screen
        Intent intent = new Intent(settingsUI.this, profileUI.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}