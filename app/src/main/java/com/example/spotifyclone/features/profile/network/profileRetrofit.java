package com.example.spotifyclone.features.profile.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class profileRetrofit {
//    private static final String BASE_URL = "https://67ad482a3f5a4e1477dd41d1.mockapi.io/andoird/";
    private static final String BASE_URL = "http://10.0.2.2:3000/";

    private static Retrofit retrofit;


    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

