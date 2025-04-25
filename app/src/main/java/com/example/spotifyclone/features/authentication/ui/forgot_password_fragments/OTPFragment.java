package com.example.spotifyclone.features.authentication.ui.forgot_password_fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.ui.ForgotPasswordActivity;
import com.example.spotifyclone.features.authentication.viewmodel.FPVMFactory;
import com.example.spotifyclone.features.authentication.viewmodel.FPViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OTPFragment extends Fragment {
    private EditText[] otpDigits = new EditText[6];
    private TextView tvEmail;
    private TextView tvTimer;
    private TextView tvResendCode;
    private MaterialButton btnVerify;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 5 * 60 * 1000; // 5 minutes
    private boolean timerRunning;
    private String email;

    public OTPFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fp_otp, container, false);

        // Initialize views
        tvEmail = view.findViewById(R.id.tvEmail);
        tvTimer = view.findViewById(R.id.tvTimer);
        tvResendCode = view.findViewById(R.id.tvResendCode);
        btnVerify = view.findViewById(R.id.btnVerify);
        ImageView btnBack = view.findViewById(R.id.btnBack);

        // Initialize OTP digit fields
        otpDigits[0] = view.findViewById(R.id.otpDigit1);
        otpDigits[1] = view.findViewById(R.id.otpDigit2);
        otpDigits[2] = view.findViewById(R.id.otpDigit3);
        otpDigits[3] = view.findViewById(R.id.otpDigit4);
        otpDigits[4] = view.findViewById(R.id.otpDigit5);
        otpDigits[5] = view.findViewById(R.id.otpDigit6);

        // Set email
        tvEmail.setText(email);

        // Set up OTP input fields
        setupOtpInputs();

        // Start timer
        startTimer();

        // Set click listeners
        btnBack.setOnClickListener(
                v -> {
                    ((ForgotPasswordActivity) requireActivity()).previousStep();
                }
        );

        btnVerify.setOnClickListener(v -> {
            if (validateOtp()) {
                otpDigits[5].clearFocus();
                verifyOtp();
            }
        });

        tvResendCode.setOnClickListener(v -> {
            resendCode();
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
                btnVerify.setEnabled(false);
                btnVerify.setText("Verifying...");
            } else {
                // Hide loading state
                btnVerify.setEnabled(true);
                btnVerify.setText("Verify");
            }
        });

        ((ForgotPasswordActivity) requireActivity()).fpViewModel.getIsOtpSent().observe(getViewLifecycleOwner(), isOtpSent -> {
            if (isOtpSent) {
                // OTP sent successfully
                Toast.makeText(requireActivity(), "Verification code resent to " + email, Toast.LENGTH_SHORT).show();
            }
        });

        ((ForgotPasswordActivity) requireActivity()).fpViewModel.getIsOTPValid().observe(getViewLifecycleOwner(), isOTPValid -> {
            if (isOTPValid) {
                // OTP is valid, move to next step
                ((ForgotPasswordActivity) requireActivity()).fpViewModel.setOTP(getEnteredOtp());
                ((ForgotPasswordActivity) requireActivity()).nextStep();
            }
        });

        ((ForgotPasswordActivity) requireActivity()).fpViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                // Show error message
                Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupOtpInputs() {
        for (int i = 0; i < otpDigits.length; i++) {
            final int currentIndex = i;

            otpDigits[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && currentIndex < otpDigits.length - 1) {
                        // Auto-focus next digit
                        otpDigits[currentIndex + 1].requestFocus();
                    }

                    // Enable verify button if all digits are filled
                    btnVerify.setEnabled(validateOtp());
                }
            });

            // Handle backspace to navigate to previous digit
            otpDigits[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (currentIndex > 0 && otpDigits[currentIndex].getText().toString().isEmpty()) {
                        // If current field is empty and backspace is pressed, go to previous field
                        otpDigits[currentIndex - 1].requestFocus();
                        otpDigits[currentIndex - 1].setText("");
                        return true;
                    }
                }
                return false;
            });
        }

        // Set focus on first digit
        otpDigits[0].requestFocus();
    }

    private boolean validateOtp() {
        for (EditText digit : otpDigits) {
            if (digit.getText().toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String getEnteredOtp() {
        StringBuilder otp = new StringBuilder();
        for (EditText digit : otpDigits) {
            otp.append(digit.getText().toString());
        }
        return otp.toString();
    }

    private void verifyOtp() {
        String otp = getEnteredOtp();

        // Show loading state
        btnVerify.setEnabled(false);
        btnVerify.setText("Verifying...");

        // Verify OTP
        email = ((ForgotPasswordActivity) requireActivity()).fpViewModel.getEmail().getValue();
        ((ForgotPasswordActivity) requireActivity()).fpViewModel.verifyOTP(email, otp);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                tvTimer.setText("Code expired");
                tvResendCode.setEnabled(true);
            }
        }.start();

        timerRunning = true;
        tvResendCode.setEnabled(false);
    }

    private void updateTimerText() {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftInMillis) -
                TimeUnit.MINUTES.toSeconds(minutes);

        String timeLeftFormatted = String.format(Locale.getDefault(),
                "Code expires in: %02d:%02d", minutes, seconds);
        tvTimer.setText(timeLeftFormatted);
    }

    private void resendCode() {
        // Show loading state
        tvResendCode.setEnabled(false);
        tvResendCode.setText("Resending...");

        email = ((ForgotPasswordActivity) requireActivity()).fpViewModel.getEmail().getValue();
        ((ForgotPasswordActivity) requireActivity()).fpViewModel.sendOTP(email);

        // Reset timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        timeLeftInMillis = 5 * 60 * 1000; // 5 minutes
        startTimer();

        // Clear OTP fields
        for (EditText digit : otpDigits) {
            digit.setText("");
        }
        otpDigits[0].requestFocus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}