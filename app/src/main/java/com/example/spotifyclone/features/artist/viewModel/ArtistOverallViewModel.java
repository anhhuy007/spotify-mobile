package com.example.spotifyclone.features.artist.viewModel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.features.artist.model.apiArtistService;
import com.example.spotifyclone.features.artist.model.artistDetail;
import com.example.spotifyclone.features.artist.model.artistRetrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ArtistOverallViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<artistDetail> artist = new MutableLiveData<>();
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

    public LiveData<artistDetail> getArtist() {
        return artist;
    }

    public void fetchArtistDetails() {

        Retrofit retrofit = artistRetrofit.getClient();
        apiArtistService apiService = retrofit.create(apiArtistService.class);

        Call<artistDetail> call = apiService.getArtistDetail(artistId);
        call.enqueue(new Callback<artistDetail>() {
            @Override
            public void onResponse(Call<artistDetail> call, Response<artistDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    artist.setValue(response.body());
                } else {
                    Toast.makeText(context, "Failed to load artist data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<artistDetail> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}