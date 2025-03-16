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

import com.example.spotifyclone.features.artist.model.ItemDiscographyEP;
import com.example.spotifyclone.features.artist.network.apiArtistService;
import com.example.spotifyclone.features.artist.network.artistRetrofit;
import com.example.spotifyclone.shared.model.APIResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;



public class ListDiscographyEPViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<List<ItemDiscographyEP>> listDiscography = new MutableLiveData<>();
    private final String artistId;
    private int type;

    public ListDiscographyEPViewModel(@NonNull Application application, String artistId, int type) {
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
            if (modelClass.isAssignableFrom(ListDiscographyEPViewModel.class)) {
                return (T) new ListDiscographyEPViewModel(application, artistId,type);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

    public LiveData<List<ItemDiscographyEP>> getListDiscography() {
        return listDiscography;
    }

    public void fetchItems() {

        Retrofit retrofit = artistRetrofit.getClient();
        apiArtistService apiService = retrofit.create(apiArtistService.class);


        Call<APIResponse<List<ItemDiscographyEP>>> call;

        switch (type) {
            case 1:
                call = apiService.getListDiscographyEP(artistId);
                break;
            default:
                call = apiService.getListDiscographyHave(artistId);
        }


        call.enqueue((new Callback<APIResponse<List<ItemDiscographyEP>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<ItemDiscographyEP>>> call, Response<APIResponse<List<ItemDiscographyEP>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listDiscography.setValue(response.body().getData());
                } else {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse<List<ItemDiscographyEP>>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        }));
    }
}
