package com.example.spotifyclone;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifyclone.features.authentication.ui.LoginActivity;
import com.example.spotifyclone.features.authentication.ui.SignupActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        final Button btnLogin = findViewById(R.id.loginButton);
        final Button btnSignup = findViewById(R.id.signupButton);
        final Button btnLoginWithGoogle = findViewById(R.id.googleSignInButton);

        btnLogin.setOnClickListener(v -> {
            // Open the login activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        btnSignup.setOnClickListener(v -> {
            // Open the signup activity
            startActivity(new Intent(MainActivity.this, SignupActivity.class));
        });

    }
}