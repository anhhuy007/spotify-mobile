package com.example.spotifyclone.features.artist.viewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.features.artist.network.ArtistService;
import com.example.spotifyclone.features.artist.model.Item;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ArtistOverallViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<Item> artist = new MutableLiveData<>();
    private final String artistId;

    private ArtistOverallViewModel(@NonNull Application application, String artistId) {
        super(application);
        this.context = application.getApplicationContext();
        this.artistId = artistId;
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final Application application;
        private final String artistId;

        public Factory(Application application, String artistId) {
            this.application = application;
            this.artistId = artistId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ArtistOverallViewModel.class)) {
                return (T) new ArtistOverallViewModel(application, artistId);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

    public LiveData<Item> getArtist() {
        return artist;
    }

    public void fetchArtistDetails() {

        Retrofit retrofit = RetrofitClient.getClient(context);
        ArtistService apiService = retrofit.create(ArtistService.class);


        apiService.getArtistDetail(artistId).enqueue(new Callback<APIResponse<Item>>() {
            @Override
            public void onResponse(Call<APIResponse<Item>> call, Response<APIResponse<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    artist.setValue(response.body().getData());
                } else {
                }
            }


            @Override
            public void onFailure(Call<APIResponse<Item>> call, Throwable t) {
            }
        });
    }
}