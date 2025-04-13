package com.example.spotifyclone.features.artist.viewModel;



import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.features.artist.model.PopularSong;
import com.example.spotifyclone.features.artist.network.ArtistService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PopularViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<List<PopularSong>> listDiscography = new MutableLiveData<>();
    private final String artistId;

    public PopularViewModel(@NonNull Application application, String artistId) {
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
            if (modelClass.isAssignableFrom(PopularViewModel.class)) {
                return (T) new PopularViewModel(application, artistId);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

    public LiveData<List<PopularSong>> getListDiscography() {
        return listDiscography;
    }

    public void fetchItems() {

        Retrofit retrofit = RetrofitClient.getClient(context);
        ArtistService apiService = retrofit.create(ArtistService.class);


        Call<APIResponse<List<PopularSong>>> call =  apiService.getListPopularArtistDetail(artistId);



        call.enqueue((new Callback<APIResponse<List<PopularSong>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<PopularSong>>> call, Response<APIResponse<List<PopularSong>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listDiscography.setValue(response.body().getData());
                } else {
                }
            }

            @Override
            public void onFailure(Call<APIResponse<List<PopularSong>>> call, Throwable t) {
            }
        }));
    }
}
