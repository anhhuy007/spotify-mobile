package com.example.spotifyclone.features.authentication.network;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "auth_prefs";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveTokens(String accessToken, String refreshToken) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ACCESS_TOKEN, accessToken);
        editor.putString(REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    public String getAccessToken() {
        return prefs.getString(ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(REFRESH_TOKEN, null);
    }

    public void clearTokens() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(ACCESS_TOKEN);
        editor.remove(REFRESH_TOKEN);
        editor.apply();
    }
}
