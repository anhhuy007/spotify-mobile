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

import com.example.spotifyclone.features.artist.model.ItemDiscographyAlbum;
import com.example.spotifyclone.features.artist.network.ArtistService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;



public class ListDiscographyAlbumViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<List<ItemDiscographyAlbum>> listDiscography = new MutableLiveData<>();
    private final String artistId;
    private int type;

    public ListDiscographyAlbumViewModel(@NonNull Application application, String artistId, int type) {
        super(application);
        this.context = application.getApplicationContext();
        this.artistId = artistId;
        this.type = type;
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final Application application;
        private final String artistId;
        private int type;

        public Factory(Application application, String artistId, int type) {
            this.application = application;
            this.artistId = artistId;
            this.type = type;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ListDiscographyAlbumViewModel.class)) {
                return (T) new ListDiscographyAlbumViewModel(application, artistId,type);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

    public LiveData<List<ItemDiscographyAlbum>> getListDiscography() {
        return listDiscography;
    }

    public void fetchItems() {

        Retrofit retrofit = RetrofitClient.getClient(context);
        ArtistService apiService = retrofit.create(ArtistService.class);


        Call<APIResponse<List<ItemDiscographyAlbum>>> call;

        switch (type) {
            case 1:
                call = apiService.getListDiscographyAlbum(artistId);
                break;
            default:
                call = apiService.getListDiscographyCollection(artistId);
        }


        call.enqueue((new Callback<APIResponse<List<ItemDiscographyAlbum>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<ItemDiscographyAlbum>>> call, Response<APIResponse<List<ItemDiscographyAlbum>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listDiscography.setValue(response.body().getData());
                } else {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse<List<ItemDiscographyAlbum>>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        }));
    }
}