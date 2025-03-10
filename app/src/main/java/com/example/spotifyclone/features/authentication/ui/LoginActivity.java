package com.example.spotifyclone.features.authentication.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.MainActivity;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.viewmodel.AuthVMFactory;
import com.example.spotifyclone.features.authentication.viewmodel.AuthViewModel;
import com.example.spotifyclone.features.profile.ui.ProfileActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;

    //    ui elements
    private EditText emailInput, passwordInput;
    private Button loginButton, googleLoginBtn;
    private ProgressBar progressBar;
    private TextView forgotPasswordText, signUpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        var gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        var mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        initializeViews();

        // initialize view model
        authViewModel = new ViewModelProvider(this, new AuthVMFactory(this)).get(AuthViewModel.class);

        googleLoginBtn.setOnClickListener(v -> {
            var signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 1);
        });

        loginButton.setOnClickListener(v -> {
            loginUser();
        });

        forgotPasswordText.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });

        signUpText.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });

        observeViewModel();
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.etEmail);
        passwordInput = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.btnLogin);
        googleLoginBtn = findViewById(R.id.btnGoogleSignIn);
        forgotPasswordText = findViewById(R.id.tvForgotPassword);
        signUpText = findViewById(R.id.tvSignUp);
        progressBar = findViewById(R.id.progressBar);
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        authViewModel.login(email, password);
    }

    private void observeViewModel() {
        authViewModel.getIsSuccess().observe(this, result -> {
            progressBar.setVisibility(View.GONE);

            if (result) {
                Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        authViewModel.getErrorMessage().observe(this, errorMessage -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        });

        authViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            loginButton.setEnabled(!isLoading);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            // Handle Google Sign In
            var task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                var account = task.getResult();
                firebaseAuthWithGoogle(account.getIdToken());

                // pass token id to server to login with google account
                authViewModel.googleLogin(account.getIdToken());

            } catch (Exception e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                // Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                // updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        var credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        // Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Log.d("LoginScreen", "firebaseAuthWithGoogle: " + user.getDisplayName());
                        Log.d("LoginScreen", "Token: " + idToken);
                        // updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        // Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        // updateUI(null);
                    }
                });
    }
}
