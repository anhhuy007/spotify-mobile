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

import com.example.spotifyclone.features.artist.model.FansAlsoLike;
import com.example.spotifyclone.features.artist.network.apiArtistService;
import com.example.spotifyclone.features.artist.network.artistRetrofit;
import com.example.spotifyclone.shared.model.APIResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;



public class FansAlsoLikeViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<List<FansAlsoLike>> listDiscography = new MutableLiveData<>();
    private final String artistId;
    private int type;

    public FansAlsoLikeViewModel(@NonNull Application application, String artistId) {
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
            if (modelClass.isAssignableFrom(FansAlsoLikeViewModel.class)) {
                return (T) new FansAlsoLikeViewModel(application, artistId);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

    public LiveData<List<FansAlsoLike>> getListDiscography() {
        return listDiscography;
    }

    public void fetchItems() {

        Retrofit retrofit = artistRetrofit.getClient();
        apiArtistService apiService = retrofit.create(apiArtistService.class);


        Call<APIResponse<List<FansAlsoLike>>> call =  apiService.getListFansAlsoLikeArtistDetail(artistId);



        call.enqueue((new Callback<APIResponse<List<FansAlsoLike>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<FansAlsoLike>>> call, Response<APIResponse<List<FansAlsoLike>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listDiscography.setValue(response.body().getData());
                } else {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse<List<FansAlsoLike>>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        }));
    }
}
