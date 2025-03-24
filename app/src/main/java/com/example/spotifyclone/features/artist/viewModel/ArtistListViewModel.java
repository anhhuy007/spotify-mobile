package com.example.spotifyclone.features.artist.viewModel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.spotifyclone.features.artist.network.apiArtistService;
import com.example.spotifyclone.features.artist.model.Item;
import com.example.spotifyclone.features.artist.network.artistRetrofit;
import com.example.spotifyclone.shared.model.APIResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ArtistListViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<List<Item>> artists = new MutableLiveData<>();

    public ArtistListViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<List<Item>> getArtists() {
        return artists;
    }

    public void fetchItems() {

        Retrofit retrofit = artistRetrofit.getClient();
        apiArtistService apiService = retrofit.create(apiArtistService.class);

        Call<APIResponse<List<Item>>> call = apiService.getListArtist();


        call.enqueue((new Callback<APIResponse<List<Item>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<Item>>> call, Response<APIResponse<List<Item>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    artists.setValue(response.body().getData());
                } else {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse<List<Item>>> call, Throwable t) {

            }
        }));
    }
}