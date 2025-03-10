package com.example.spotifyclone.features.authentication.ui.forgot_password_fragments;

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

import androidx.fragment.app.Fragment;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.ui.ForgotPasswordActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EmailFragment extends Fragment {

    private TextInputLayout emailInputLayout;
    private TextInputEditText emailInput;
    private MaterialButton btnSendResetLink;
    private TextView tvBackToLogin;

    public EmailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fp_email, container, false);

        // Initialize views
        emailInputLayout = view.findViewById(R.id.emailInputLayout);
        emailInput = view.findViewById(R.id.emailInput);
        btnSendResetLink = view.findViewById(R.id.btnSendResetLink);
        tvBackToLogin = view.findViewById(R.id.tvBackToLogin);
        ImageView btnBack = view.findViewById(R.id.btnBack);

        // Set up text change listener for email validation
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear error when user starts typing
                emailInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEmail(false);
            }
        });

        // Set click listeners
        btnBack.setOnClickListener(v -> {
            ((ForgotPasswordActivity) requireActivity()).navigateToLogin();
        });

        btnSendResetLink.setOnClickListener(v -> {
            if (validateEmail(true)) {
                emailInput.clearFocus();
                sendOTP();
            }
        });

        tvBackToLogin.setOnClickListener(v -> {
            ((ForgotPasswordActivity) requireActivity()).navigateToLogin();
        });

        observeViewModel();

        return view;
    }

    private void observeViewModel() {
        ((ForgotPasswordActivity) requireActivity()).fpViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                btnSendResetLink.setEnabled(false);
                btnSendResetLink.setText("Sending...");
            } else {
                btnSendResetLink.setEnabled(true);
                btnSendResetLink.setText("Send OTP");
            }
        });

        ((ForgotPasswordActivity) requireActivity()).fpViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                emailInputLayout.setError(errorMessage);
            }
        });

        ((ForgotPasswordActivity) requireActivity()).fpViewModel.getIsOtpSent().observe(getViewLifecycleOwner(), otpSent -> {
            if (otpSent) {
                ((ForgotPasswordActivity) requireActivity()).fpViewModel.setEmail(emailInput.getText().toString().trim());
                ((ForgotPasswordActivity) requireActivity()).nextStep();
            }
        });
    }

    private boolean validateEmail(boolean showError) {
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";

        if (email.isEmpty()) {
            if (showError) {
                emailInputLayout.setError("Email is required");
            }
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (showError) {
                emailInputLayout.setError("Please enter a valid email address");
            }
            return false;
        }

        return true;
    }

    private void sendOTP() {
        String email = emailInput.getText().toString().trim();

        // Show loading state
        btnSendResetLink.setEnabled(false);
        btnSendResetLink.setText("Sending...");

        // Send OTP
        ((ForgotPasswordActivity) requireActivity()).fpViewModel.sendOTP(email);
    }
}

