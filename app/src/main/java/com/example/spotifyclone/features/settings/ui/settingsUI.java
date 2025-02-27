package com.example.spotifyclone.features.settings.ui;

import static com.example.spotifyclone.features.settings.helper.NotificationHelper.createNotificationChannel;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Switch;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.spotifyclone.R;

import java.util.Locale;

public class settingsUI extends AppCompatActivity {

    private Switch switchTheme, switchLang, switchNoti;
    private ImageView imgOnNoti, imgOffNoti, imgDark, imgLight;
    private TextView txtVN, txtEN;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        switchTheme = findViewById(R.id.switchTheme);
        switchLang = findViewById(R.id.switchlang);
        switchNoti = findViewById(R.id.switchNoti);
        imgOnNoti = findViewById(R.id.onNoti);
        imgOffNoti = findViewById(R.id.offNoti);
        imgDark = findViewById(R.id.dark);
        imgLight = findViewById(R.id.light);
        txtVN = findViewById(R.id.VN);
        txtEN = findViewById(R.id.EN);

        switchTheme.setChecked(isDarkMode);
        switchLang.setChecked(isEnglish);
        switchNoti.setChecked(prefs.getBoolean("notificationEnabled", false));

        updateLanguageUI();
        updateNotificationUI();
        updateThemeIcons();

        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("darkMode", isChecked);
            editor.apply();
            updateTheme();
        });

        switchLang.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isEnglish", isChecked);
            editor.apply();
            setLocale(isChecked ? "en" : "vi");
            recreate();
        });

        switchNoti.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS")
                                != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{"android.permission.POST_NOTIFICATIONS"}, 100);
                    openNotificationSettings();
                    switchNoti.setChecked(false);
                } else {

                    createNotificationChannel(this);
                    saveNotificationState(true);
                }
            } else {
                unregisterForNotifications();
                saveNotificationState(false);
            }
            updateNotificationUI();
        });
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        resources.updateConfiguration(config, resources.getDisplayMetrics());
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

        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("darkMode", switchTheme.isChecked());
        editor.apply();

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

    private void unregisterForNotifications() {
        NotificationManagerCompat.from(this).cancelAll();
    }

    private void saveNotificationState(boolean enabled) {
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putBoolean("notificationEnabled", enabled);
        editor.apply();
    }

    private void openNotificationSettings() {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(intent);
    }



}
