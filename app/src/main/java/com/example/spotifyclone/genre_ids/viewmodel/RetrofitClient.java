package com.example.spotifyclone.genre_ids.viewmodel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:5000";
    private static Retrofit retrofit=null;
    public static APIService getApiService(){
        if(retrofit==null)
        {
            Gson gson=new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
            retrofit=new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(APIService.class);
    }

}
