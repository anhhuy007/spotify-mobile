package com.example.spotifyclone.features.settings.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.spotifyclone.R;

import java.util.Locale;

public class settingsUI extends AppCompatActivity {

    private Switch switchTheme, switchLang, switchNoti;
    private ImageView imgOnNoti, imgOffNoti;
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

        // Áp dụng ngôn ngữ trước khi setContentView
        boolean isEnglish = prefs.getBoolean("isEnglish", false);
        setLocale(isEnglish ? "en" : "vi");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Ánh xạ View
        switchTheme = findViewById(R.id.switchTheme);
        switchLang = findViewById(R.id.switchlang);
        switchNoti = findViewById(R.id.switchNoti);
        imgOnNoti = findViewById(R.id.onNoti);
        imgOffNoti = findViewById(R.id.offNoti);
        txtVN = findViewById(R.id.VN);
        txtEN = findViewById(R.id.EN);

        // Gán trạng thái đã lưu
        switchTheme.setChecked(isDarkMode);
        switchLang.setChecked(isEnglish);
        switchNoti.setChecked(prefs.getBoolean("notificationEnabled", false));

        // Cập nhật UI theo trạng thái đã lưu
        updateLanguageUI();
        updateNotificationUI();

        // Xử lý sự kiện bật/tắt Theme
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("darkMode", isChecked);
            editor.apply();
            updateTheme();
        });

        // Xử lý sự kiện chọn ngôn ngữ
        switchLang.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isEnglish", isChecked);
            editor.apply();
            // Đổi ngôn ngữ và restart activity
            setLocale(isChecked ? "en" : "vi");
            recreate();
        });

        // Xử lý sự kiện bật/tắt thông báo
        switchNoti.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("notificationEnabled", isChecked);
            editor.apply();
            updateNotificationUI();
            toggleNotifications(isChecked);
        });
    }

    // Đặt ngôn ngữ cho ứng dụng
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    // Cập nhật chế độ sáng/tối toàn bộ app
    private void updateTheme() {
        if (switchTheme.isChecked()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        recreate(); // Áp dụng theme ngay lập tức
    }

    // Cập nhật UI ngôn ngữ
    private void updateLanguageUI() {
        if (switchLang.isChecked()) {
            txtVN.setTextColor(getResources().getColor(R.color.gray));
            txtEN.setTextColor(getResources().getColor(R.color.black));
        } else {
            txtVN.setTextColor(getResources().getColor(R.color.black));
            txtEN.setTextColor(getResources().getColor(R.color.gray));
        }
    }

    // Cập nhật UI trạng thái thông báo
    private void updateNotificationUI() {
        boolean isEnabled = switchNoti.isChecked();
        imgOnNoti.setAlpha(isEnabled ? 1.0f : 0.3f);
        imgOffNoti.setAlpha(isEnabled ? 0.3f : 1.0f);
    }

    // Bật/tắt thông báo
    private void toggleNotifications(boolean enable) {
        // Đây là nơi để triển khai logic bật/tắt thông báo
        if (enable) {
            // Đăng ký nhận thông báo
            registerForNotifications();
        } else {
            // Hủy đăng ký nhận thông báo
            unregisterForNotifications();
        }
    }

    // Đăng ký nhận thông báo
    private void registerForNotifications() {
        // Triển khai đăng ký FCM token hoặc cấu hình thông báo ở đây
        // Ví dụ:
        // FirebaseMessaging.getInstance().subscribeToTopic("all_notifications");
    }

    // Hủy đăng ký nhận thông báo
    private void unregisterForNotifications() {
        // Triển khai hủy đăng ký FCM token hoặc vô hiệu hóa thông báo ở đây
        // Ví dụ:
        // FirebaseMessaging.getInstance().unsubscribeFromTopic("all_notifications");
    }
}