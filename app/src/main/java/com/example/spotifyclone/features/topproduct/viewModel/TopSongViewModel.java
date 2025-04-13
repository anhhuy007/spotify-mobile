package com.example.spotifyclone.features.topproduct.viewModel;




import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


import com.example.spotifyclone.features.topproduct.model.TopSong;
import com.example.spotifyclone.features.topproduct.network.ApiTopProduct;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TopSongViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<List<TopSong>> listDiscography = new MutableLiveData<>();

    public TopSongViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<List<TopSong>> getTopProduct() {
        return listDiscography;
    }

    public void fetchItems() {

        Retrofit retrofit = RetrofitClient.getClient(context);
        ApiTopProduct apiService = retrofit.create(ApiTopProduct.class);

        Call<APIResponse<List<TopSong>>> call =  apiService.getTopSong();

        call.enqueue((new Callback<APIResponse<List<TopSong>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<TopSong>>> call, Response<APIResponse<List<TopSong>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listDiscography.setValue(response.body().getData());
                } else {
                }
            }

            @Override
            public void onFailure(Call<APIResponse<List<TopSong>>> call, Throwable t) {
            }
        }));
    }
}

