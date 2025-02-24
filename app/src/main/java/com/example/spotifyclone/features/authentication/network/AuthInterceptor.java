package com.example.spotifyclone.features.authentication.network;

import android.content.Context;

import com.example.spotifyclone.features.authentication.model.Tokens;
import com.example.spotifyclone.shared.model.APIResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final TokenManager tokenManager;
    private final AuthService authService;

    public AuthInterceptor(Context context, AuthService authService) {
        this.tokenManager = new TokenManager(context);
        this.authService = authService;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String token = tokenManager.getAccessToken();

        if (token == null) {
            return chain.proceed(originalRequest);
        }

        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        Response response = chain.proceed(newRequest);

        if (response.code() == 401) {
            synchronized (this) {
                String refreshToken = tokenManager.getRefreshToken();
                if (refreshToken == null) {
                    return response;
                }

                String newToken = refreshAccessToken(refreshToken);
                if (newToken != null) {
                    tokenManager.saveTokens(newToken, refreshToken);

                    newRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + newToken)
                            .build();
                    return chain.proceed(newRequest);
                }
            }
        }

        return response;
    }

    private String refreshAccessToken(String refreshToken) {
        try {
            retrofit2.Response<APIResponse<Tokens>> response = authService
                    .refreshToken(refreshToken)
                    .execute();

            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                return response.body().getData().getAccessToken();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
