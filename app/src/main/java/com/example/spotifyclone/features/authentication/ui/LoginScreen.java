package com.example.spotifyclone.features.authentication.ui;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifyclone.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginScreen extends Activity {

    Button googleLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        var gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        var mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleLoginButton = findViewById(R.id.googleLoginButton);
        googleLoginButton.setOnClickListener(v -> {
            var signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 1);
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
                // Signed in successfully, show authenticated UI.
                // updateUI(account);
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
                        Log.d("LoginScreen", "Token: " + user.getIdToken(true));
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
