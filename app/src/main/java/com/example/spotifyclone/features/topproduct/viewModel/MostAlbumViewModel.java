package com.example.spotifyclone.features.topproduct.viewModel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.spotifyclone.features.artist.network.artistRetrofit;
import com.example.spotifyclone.features.topproduct.model.TopAlbum;
import com.example.spotifyclone.features.topproduct.network.ApiTopProduct;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
public class MostAlbumViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<TopAlbum> artist = new MutableLiveData<>();

    public MostAlbumViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }



    public LiveData<TopAlbum> getArtist() {
        return artist;
    }

    public void fetchArtistDetails() {

        Retrofit retrofit = RetrofitClient.getClient(context);
        ApiTopProduct apiService = retrofit.create(ApiTopProduct.class);


        apiService.getMostAlbum().enqueue(new Callback<APIResponse<TopAlbum>>() {
            @Override
            public void onResponse(Call<APIResponse<TopAlbum>> call, Response<APIResponse<TopAlbum>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    artist.setValue(response.body().getData());
                } else {
                    Toast.makeText(context, "Failed to load artist data", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<APIResponse<TopAlbum>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}