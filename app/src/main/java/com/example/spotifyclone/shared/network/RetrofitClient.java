package com.example.spotifyclone.shared.network;

import android.content.Context;

import com.example.spotifyclone.features.authentication.network.AuthInterceptor;
import com.example.spotifyclone.features.authentication.network.AuthService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL="https://spotify-server-7bcw.onrender.com/";

//    private static final String BASE_URL="http://127.0.0.1:3000/";
//    private static final String BASE_URL="https://spotify-server-7bcw.onrender.com/";
//    private static final String BASE_URL = "http://127.0.0.1:3000/";


    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            Retrofit basicRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            AuthService authService = basicRetrofit.create(AuthService.class);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor(new AuthInterceptor(context, authService))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
