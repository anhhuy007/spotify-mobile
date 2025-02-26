package com.example.spotifyclone.features.authentication.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.spotifyclone.features.authentication.model.LoginResponse;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.features.authentication.network.AuthService;
import com.example.spotifyclone.shared.model.User;
import com.example.spotifyclone.shared.network.RetrofitClient;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_USER = "user";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final AuthService authService;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private final Gson gson;

    public AuthRepository(Context context) {
        this.authService = RetrofitClient.getClient(context).create(AuthService.class);
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
        this.gson = new Gson();
    }

    /**
     * Perform login and store user & tokens in SharedPreferences
     */
    public void login(String email, String password, AuthCallback callback) {
        authService.login(email, password).enqueue(new Callback<APIResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<APIResponse<LoginResponse>> call, Response<APIResponse<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    LoginResponse loginResponse = response.body().getData();

                    // Save user and tokens
                    saveUser(loginResponse.getUser());
                    saveTokens(loginResponse.getTokens().getAccessToken(), loginResponse.getTokens().getRefreshToken());
                    setIsLoggedIn(true);

                    callback.onSuccess(loginResponse.getUser());
                } else {
                    callback.onFailure("Invalid email or password");
                }
            }

            @Override
            public void onFailure(Call<APIResponse<LoginResponse>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    private void setIsLoggedIn(boolean b) {
        editor.putBoolean(KEY_IS_LOGGED_IN, b);
        editor.apply();
    }

    /**
     * Save user to SharedPreferences
     */
    private void saveUser(User user) {
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER, userJson);
        editor.apply();
    }

    /**
     * Retrieve user from SharedPreferences
     */
    public User getUser() {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        return userJson != null ? gson.fromJson(userJson, User.class) : null;
    }

    /**
     * Save authentication tokens
     */
    private void saveTokens(String accessToken, String refreshToken) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    /**
     * Retrieve access token
     */
    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    /**
     * Retrieve refresh token
     */
    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Logout user
     */
    public void logout() {
        editor.remove(KEY_USER);
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_REFRESH_TOKEN);
        editor.apply();
    }

    /**
     * Authentication callback interface
     */
    public interface AuthCallback {
        void onSuccess(User user);
        void onFailure(String error);
    }
}
