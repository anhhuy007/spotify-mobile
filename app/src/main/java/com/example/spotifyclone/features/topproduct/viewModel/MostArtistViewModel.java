package com.example.spotifyclone.features.topproduct.viewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.spotifyclone.features.topproduct.model.TopArtist;
import com.example.spotifyclone.features.topproduct.network.ApiTopProduct;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
public class MostArtistViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<TopArtist> artist = new MutableLiveData<>();

    public MostArtistViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }



    public LiveData<TopArtist> getArtist() {
        return artist;
    }

    public void fetchArtistDetails() {

        Retrofit retrofit = RetrofitClient.getClient(context);
        ApiTopProduct apiService = retrofit.create(ApiTopProduct.class);


        apiService.getMostArtist().enqueue(new Callback<APIResponse<TopArtist>>() {
            @Override
            public void onResponse(Call<APIResponse<TopArtist>> call, Response<APIResponse<TopArtist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    artist.setValue(response.body().getData());
                } else {
                }
            }
            @Override
            public void onFailure(Call<APIResponse<TopArtist>> call, Throwable t) {
            }
        });
    }
}