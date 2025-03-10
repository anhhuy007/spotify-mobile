package com.example.spotifyclone.features.authentication.ui.signup_fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.ui.SignupActivity;
import com.example.spotifyclone.features.authentication.viewmodel.SignupVMFactory;
import com.example.spotifyclone.features.authentication.viewmodel.SignupViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class CredentialsFragment extends Fragment {

    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton btnContinue;
    private TextView tvLogin;

    // Password validation pattern: at least 8 chars, with letters, numbers and symbols
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_credentials, container, false);

        // Initialize views
        emailInputLayout = view.findViewById(R.id.emailInputLayout);
        passwordInputLayout = view.findViewById(R.id.passwordInputLayout);
        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        btnContinue = view.findViewById(R.id.btnContinue);
        tvLogin = view.findViewById(R.id.tvLogin);
        ImageView btnClose = view.findViewById(R.id.btnClose);

        // Set up text change listeners for validation
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear error when user starts typing
                emailInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateInputs(false);
            }
        });

        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear error when user starts typing
                passwordInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateInputs(false);
            }
        });

        // Set click listeners
        btnClose.setOnClickListener(v -> {
            // Show confirmation dialog before closing
            showExitConfirmationDialog();
        });

        btnContinue.setOnClickListener(v -> {
            if (validateInputs(true)) {
                // Save credentials and proceed to next step
                saveCredentials();
                proceedToNextStep();
            }
        });

        tvLogin.setOnClickListener(v -> {
            // Navigate to login screen
            navigateToLogin();
        });

        return view;
    }

    private boolean validateInputs(boolean showErrors) {
        boolean isValid = true;

        // Validate email
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        if (email.isEmpty()) {
            if (showErrors) {
                emailInputLayout.setError("Email is required");
                Log.d("DEBUG", "Email is required");
            }
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (showErrors) {
                emailInputLayout.setError("Please enter a valid email address");
                Log.d("DEBUG", "Please enter a valid email address");
            }
            isValid = false;
        }

        // Validate password
        String password = passwordInput.getText() != null ? passwordInput.getText().toString() : "";
        if (password.isEmpty()) {
            if (showErrors) {
                passwordInputLayout.setError("Password is required");
                Log.d("DEBUG", "Password is required");
            }
            isValid = false;
        } else if (password.length() < 8) {
            if (showErrors) {
                passwordInputLayout.setError("Password must be at least 8 characters");
                Log.d("DEBUG", "Password must be at least 8 characters");
            }
            isValid = false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            if (showErrors) {
                passwordInputLayout.setError("Password must include letters, numbers, and symbols");
                Log.d("DEBUG", "Password must include letters, numbers, and symbols");
            }
            isValid = false;
        }

        Log.d("DEBUG", "isValid: " + isValid);

        // Update button state
//        btnContinue.setEnabled(isValid);

        return isValid;
    }

    private void saveCredentials() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        ((SignupActivity) requireActivity()).signupViewModel.setEmail(email);
        ((SignupActivity) requireActivity()).signupViewModel.setPassword(password);
    }

    private String hashPassword(String password) {
        // In a real app, you would use a proper hashing algorithm
        // This is just a placeholder
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void proceedToNextStep() {
        // Navigate to the birthday selection screen
        ((SignupActivity) requireActivity()).nextStep();
    }

    private void navigateToLogin() {
        // In a real app, you would navigate to the login screen
        Toast.makeText(requireContext(), "Navigating to login screen", Toast.LENGTH_SHORT).show();
        ((SignupActivity) requireActivity()).navigateToLogin();
    }

    private void showExitConfirmationDialog() {
        // In a real app, you would show a dialog asking if the user wants to exit


        // For simplicity, we'll just finish the activity
        ((SignupActivity) requireActivity()).closeSignup();
    }
}

