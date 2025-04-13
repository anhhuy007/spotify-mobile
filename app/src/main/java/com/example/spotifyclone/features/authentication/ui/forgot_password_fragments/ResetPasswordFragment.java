package com.example.spotifyclone.features.authentication.ui.forgot_password_fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.ui.ForgotPasswordActivity;
import com.example.spotifyclone.features.authentication.ui.LoginActivity;
import com.example.spotifyclone.features.authentication.viewmodel.FPVMFactory;
import com.example.spotifyclone.features.authentication.viewmodel.FPViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class ResetPasswordFragment extends Fragment {
    private TextInputLayout passwordInputLayout;
    private TextInputLayout confirmPasswordInputLayout;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private TextView tvPasswordStrength;
    private ProgressBar passwordStrengthBar;
    private MaterialButton btnResetPassword;

    // Password validation patterns
    private static final Pattern HAS_LETTER = Pattern.compile("[a-zA-Z]");
    private static final Pattern HAS_DIGIT = Pattern.compile("\\d");
    private static final Pattern HAS_SYMBOL = Pattern.compile("[^a-zA-Z0-9]");
    private static final int MIN_PASSWORD_LENGTH = 8;

    public ResetPasswordFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fp_resetpw, container, false);

        Log.d("DEBUG", "onCreateView ResetPasswordFragment");

        // Initialize views
        passwordInputLayout = view.findViewById(R.id.passwordInputLayout);
        confirmPasswordInputLayout = view.findViewById(R.id.confirmPasswordInputLayout);
        passwordInput = view.findViewById(R.id.passwordInput);
        confirmPasswordInput = view.findViewById(R.id.confirmPasswordInput);
        tvPasswordStrength = view.findViewById(R.id.tvPasswordStrength);
        passwordStrengthBar = view.findViewById(R.id.passwordStrengthBar);
        btnResetPassword = view.findViewById(R.id.btnResetPassword);
        ImageView btnBack = view.findViewById(R.id.btnBack);

        passwordStrengthBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_500, null)));

        // Set up text change listeners for validation
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
                updatePasswordStrength(s.toString());
                validateInputs(false);
            }
        });

        confirmPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear error when user starts typing
                confirmPasswordInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateInputs(false);
            }
        });

        // Set click listeners
        btnBack.setOnClickListener(v -> {
                    ((AppCompatActivity) requireActivity()).finish();
                }
        );

        btnResetPassword.setOnClickListener(v -> {
            if (validateInputs(true)) {
                confirmPasswordInput.clearFocus();
                resetPassword();
            }
        });

        btnBack.setOnClickListener(v -> {
            ((ForgotPasswordActivity) requireActivity()).previousStep();
        });

        observeViewModel();

        return view;
    }

    private void observeViewModel() {
        ((ForgotPasswordActivity) requireActivity()).fpViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                // Show loading state
                btnResetPassword.setEnabled(false);
                btnResetPassword.setText("Resetting...");
            } else {
                // Hide loading state
                btnResetPassword.setEnabled(true);
                btnResetPassword.setText("Reset Password");
            }
        });

        ((ForgotPasswordActivity) requireActivity()).fpViewModel.getIsPasswordReset().observe(getViewLifecycleOwner(), isPasswordReset -> {
            if (isPasswordReset) {
                // Password reset successful, navigate to login
                Log.d("DEBUG", "Password reset successfully");
                Toast.makeText(requireActivity(), "Password reset successful", Toast.LENGTH_SHORT).show();

                ((ForgotPasswordActivity) requireActivity()).navigateToLogin();
            }
        });

        ((ForgotPasswordActivity) requireActivity()).fpViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                // Show error message
                Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePasswordStrength(String password) {
        int strength = 0;
        String strengthText = "Weak";
        int progressColor = getResources().getColor(R.color.red_500, null);

        if (password.length() >= MIN_PASSWORD_LENGTH) {
            strength++;
        }

        if (HAS_LETTER.matcher(password).find()) {
            strength++;
        }

        if (HAS_DIGIT.matcher(password).find()) {
            strength++;
        }

        if (HAS_SYMBOL.matcher(password).find()) {
            strength++;
        }

        switch (strength) {
            case 0:
            case 1:
                strengthText = "Weak";
                progressColor = getResources().getColor(R.color.red_500, null);
                passwordStrengthBar.setProgress(25);
                break;
            case 2:
                strengthText = "Fair";
                progressColor = getResources().getColor(R.color.orange_500, null);
                passwordStrengthBar.setProgress(50);
                break;
            case 3:
                strengthText = "Good";
                progressColor = getResources().getColor(R.color.yellow_500, null);
                passwordStrengthBar.setProgress(75);
                break;
            case 4:
                strengthText = "Strong";
                progressColor = getResources().getColor(R.color.spotify_green, null);
                passwordStrengthBar.setProgress(100);
                break;
        }

        tvPasswordStrength.setText("Password strength: " + strengthText);
        passwordStrengthBar.setProgressTintList(ColorStateList.valueOf(progressColor));
    }

    private boolean validateInputs(boolean showErrors) {
        boolean isValid = true;

        Log.d("DEBUG", "Validating inputs with showErrors: " + showErrors);

        // Validate password
        String password = passwordInput.getText() != null ? passwordInput.getText().toString() : "";
        if (password.isEmpty()) {
            if (showErrors) {
                passwordInputLayout.setError("Password is required");
            }
            isValid = false;
        } else if (password.length() < MIN_PASSWORD_LENGTH) {
            if (showErrors) {
                passwordInputLayout.setError("Password must be at least 8 characters");
            }
            isValid = false;
        } else if (!HAS_LETTER.matcher(password).find() ||
                !HAS_DIGIT.matcher(password).find() ||
                !HAS_SYMBOL.matcher(password).find()) {
            if (showErrors) {
                passwordInputLayout.setError("Password must include letters, numbers, and symbols");
            }
            isValid = false;
        } else {
            // Clear any previous errors when password is valid
            passwordInputLayout.setError(null);
        }

        // Validate confirm password
        String confirmPassword = confirmPasswordInput.getText() != null ?
                confirmPasswordInput.getText().toString() : "";
        if (confirmPassword.isEmpty()) {
            if (showErrors) {
                confirmPasswordInputLayout.setError("Please confirm your password");
            }
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            if (showErrors) {
                Log.d("DEBUG", "Passwords do not match");
                confirmPasswordInputLayout.setError("Passwords do not match");
            }
            isValid = false;
        } else {
            // Clear any previous errors when confirmation is valid
            confirmPasswordInputLayout.setError(null);
        }

        return isValid;
    }

    private void resetPassword() {
        String password = passwordInput.getText().toString();
        ((ForgotPasswordActivity) requireActivity()).fpViewModel.setPassword(password);

        // Show loading state
        btnResetPassword.setEnabled(false);
        btnResetPassword.setText("Resetting...");

        Log.d("DEBUG", "Resetting password...");
        Log.d("DEBUG", "Email: " + ((ForgotPasswordActivity) requireActivity()).fpViewModel.getEmail().getValue());
        Log.d("DEBUG", "OTP: " + ((ForgotPasswordActivity) requireActivity()).fpViewModel.getOTP().getValue());
        Log.d("DEBUG", "Password: " + ((ForgotPasswordActivity) requireActivity()).fpViewModel.getPassword().getValue());

        // Reset password
        ((ForgotPasswordActivity) requireActivity()).fpViewModel.resetPassword(
                ((ForgotPasswordActivity) requireActivity()).fpViewModel.getEmail().getValue(),
                ((ForgotPasswordActivity) requireActivity()).fpViewModel.getPassword().getValue(),
                ((ForgotPasswordActivity) requireActivity()).fpViewModel.getOTP().getValue()
        );
    }
}

