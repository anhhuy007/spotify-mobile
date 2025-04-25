package com.example.spotifyclone.features.authentication.ui.signup_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.MainActivity;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.model.AvatarUploadCallBack;
import com.example.spotifyclone.features.authentication.model.CheckUserExistCallBack;
import com.example.spotifyclone.features.authentication.ui.SignupActivity;
import com.example.spotifyclone.features.authentication.viewmodel.AuthVMFactory;
import com.example.spotifyclone.features.authentication.viewmodel.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class UsernameFragment extends Fragment {
    private TextInputLayout usernameInputLayout;
    private TextInputEditText usernameInput;
    private TextView tvUsernameAvailability;
    private ChipGroup suggestedUsernamesChipGroup;
    private MaterialButton btnComplete;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable usernameCheckRunnable;
    private ProgressBar loadingProgressBar;
    private ImageView btnBack, btnClose;

    private AuthViewModel authViewModel;

    private final List<String> reservedUsernames = Arrays.asList(
            "spotify", "admin", "user", "music", "playlist", "artist", "album"
    );

    private final List<String> adjectives = Arrays.asList(
            "happy", "cool", "awesome", "groovy", "melodic", "rhythmic", "sonic", "acoustic"
    );

    private final List<String> nouns = Arrays.asList(
            "listener", "beats", "tunes", "melody", "rhythm", "music", "sound", "notes"
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_username, container, false);

        usernameInputLayout = view.findViewById(R.id.usernameInputLayout);
        usernameInput = view.findViewById(R.id.usernameInput);
        tvUsernameAvailability = view.findViewById(R.id.tvUsernameAvailability);
        suggestedUsernamesChipGroup = view.findViewById(R.id.suggestedUsernamesChipGroup);
        btnComplete = view.findViewById(R.id.btnCompleteSignup);
        btnBack = view.findViewById(R.id.btnBack);
        btnClose = view.findViewById(R.id.btnClose);
        loadingProgressBar = view.findViewById(R.id.progressBarLoading);

        authViewModel = new ViewModelProvider(this, new AuthVMFactory(requireActivity())).get(AuthViewModel.class);

        generateUsernameSuggestions();

        usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usernameInputLayout.setError(null);
                tvUsernameAvailability.setVisibility(View.GONE);

                if (usernameCheckRunnable != null) {
                    handler.removeCallbacks(usernameCheckRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String username = s.toString().trim();
                if (username.length() >= 3) {
                    usernameCheckRunnable = () -> checkUsernameAvailability(username);
                    handler.postDelayed(usernameCheckRunnable, 500);
                }
            }
        });

        btnBack.setOnClickListener(v -> {
            ((SignupActivity) requireActivity()).previousStep();
        });
        btnClose.setOnClickListener(v -> {
            ((SignupActivity) requireActivity()).closeSignup();
        });

        btnComplete.setOnClickListener(v -> {
            if (validateUsername()) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                saveUsername();
                completeSignup();
            }
        });

        observeAuthViewModel();

        return view;
    }

    private void completeSignup() {
        String username = ((SignupActivity) requireActivity()).signupViewModel.getUsername().getValue();
        String email = ((SignupActivity) requireActivity()).signupViewModel.getEmail().getValue();
        String password = ((SignupActivity) requireActivity()).signupViewModel.getPassword().getValue();
        String avatarUri = ((SignupActivity) requireActivity()).signupViewModel.getAvatarUri().getValue();
        String dob = ((SignupActivity) requireActivity()).signupViewModel.getDataOfBirth().getValue();

        ((SignupActivity) requireActivity()).signupViewModel.uploadAvatar(avatarUri, new AvatarUploadCallBack() {
            @Override
            public void onSuccess(String avatarUrl) {
                authViewModel.signup(username, email, password, convertDob(dob), avatarUrl);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeAuthViewModel() {
        authViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                btnComplete.setEnabled(false);
                btnBack.setEnabled(false);
                btnClose.setEnabled(false);
            } else {
                btnComplete.setEnabled(true);
                btnBack.setEnabled(true);
                btnClose.setEnabled(true);
            }
        });

        authViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        authViewModel.getIsSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(getContext(), "Sign up Complete!", Toast.LENGTH_SHORT).show();
                // Navigate to the home screen
                startActivity(new Intent(requireContext(), MainActivity.class));
                requireActivity().finish();
            }
        });
    }

    private void generateUsernameSuggestions() {
        suggestedUsernamesChipGroup.removeAllViews();
        String emailPrefix = "";
        String email = requireActivity().getSharedPreferences("spotify_signup", getContext().MODE_PRIVATE)
                .getString("email", "");
        if (email.contains("@")) {
            emailPrefix = email.split("@")[0];
        }
        List<String> suggestions = new ArrayList<>();
        Random random = new Random();
        if (!emailPrefix.isEmpty()) {
            suggestions.add(emailPrefix + random.nextInt(1000));
        }
        while (suggestions.size() < 4) {
            String adjective = adjectives.get(random.nextInt(adjectives.size()));
            String noun = nouns.get(random.nextInt(nouns.size()));
            String suggestion = adjective + "_" + noun + random.nextInt(100);
            if (!suggestions.contains(suggestion)) {
                suggestions.add(suggestion);
            }
        }
        for (String suggestion : suggestions) {
            Chip chip = new Chip(getContext());
            chip.setText(suggestion);
            chip.setClickable(true);
            chip.setCheckable(false);
            chip.setChipBackgroundColorResource(R.color.gray_200);
            chip.setOnClickListener(v -> {
                usernameInput.setText(chip.getText());
                checkUsernameAvailability(chip.getText().toString());
            });
            suggestedUsernamesChipGroup.addView(chip);
        }
    }

    private void checkUsernameAvailability(String username) {
        usernameInputLayout.setHelperTextColor(getContext().getColorStateList(R.color.gray_600));
        usernameInputLayout.setHelperText("Checking availability...");
        handler.postDelayed(() -> {
            authViewModel.checkUsernameAvailability(username, new CheckUserExistCallBack() {
                @Override
                public void onUserExist(Boolean isExisted) {
                    if (isExisted) {
                        usernameInputLayout.setError("This username is already taken 🚫");
                        tvUsernameAvailability.setVisibility(View.GONE);
                        btnComplete.setEnabled(false);
                    } else {
                        usernameInputLayout.setHelperText("");
                        usernameInputLayout.setHelperTextColor(getContext().getColorStateList(R.color.spotify_green));
                        tvUsernameAvailability.setText("This username is available ✅");
                        tvUsernameAvailability.setVisibility(View.VISIBLE);
                        btnComplete.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(String error) {
                    usernameInputLayout.setError("An error occurred");
                    tvUsernameAvailability.setVisibility(View.GONE);
                    btnComplete.setEnabled(false);
                }
            });
        }, 1000);
    }

    private boolean validateUsername() {
        String username = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
        if (username.isEmpty() || username.length() < 3 || !username.matches("^[a-zA-Z0-9_]+$") || reservedUsernames.contains(username.toLowerCase())) {
            usernameInputLayout.setError("Invalid username");
            return false;
        }
        return true;
    }

    private void saveUsername() {
        String username = usernameInput.getText().toString().trim();
        ((SignupActivity) requireActivity()).signupViewModel.setUsername(username);
    }

    private String convertDob(String dob) {
        if (dob == null) {
            return "1990-01-01T00:00:00.000Z"; // default date of birth
        }

        // convert from dd/mm/yyyy to 2025-03-05T14:36:57.619Z
        String[] parts = dob.split("/");
        return parts[2] + "-" + parts[1] + "-" + parts[0] + "T00:00:00.000Z";
    }
}