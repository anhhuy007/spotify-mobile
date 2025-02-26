package com.example.spotifyclone;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifyclone.features.artist.ui.artistDetailUI;
import com.example.spotifyclone.features.artist.ui.artistListUI;
import com.example.spotifyclone.features.profile.ui.profileUI;
import com.example.spotifyclone.features.settings.helper.NotificationHelper;
import com.example.spotifyclone.features.settings.ui.settingsUI;
import com.example.spotifyclone.shared.utils.Constants;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        NotificationHelper.createNotificationChannel(this);

        Button btnArtistList = findViewById(R.id.button);
        btnArtistList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, artistListUI.class);
            startActivity(intent);
        });

        Button btnProfile = findViewById(R.id.button1);
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, profileUI.class);
            intent.putExtra("USER_ID", Constants.userID);
            startActivity(intent);
        });

        Button btnSetting = findViewById(R.id.button2);
        btnSetting.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, settingsUI.class);
            startActivity(intent);
        });

    }
}
