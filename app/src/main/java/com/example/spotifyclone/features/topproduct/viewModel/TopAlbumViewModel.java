package com.example.spotifyclone.features.topproduct.viewModel;


import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


import com.example.spotifyclone.features.topproduct.model.TopAlbum;
import com.example.spotifyclone.features.topproduct.network.ApiTopProduct;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TopAlbumViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<List<TopAlbum>> listDiscography = new MutableLiveData<>();

    public TopAlbumViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<List<TopAlbum>> getTopProduct() {
        return listDiscography;
    }

    public void fetchItems() {

        Retrofit retrofit = RetrofitClient.getClient(context);
        ApiTopProduct apiService = retrofit.create(ApiTopProduct.class);

        Call<APIResponse<List<TopAlbum>>> call = apiService.getTopAlbum();

        call.enqueue((new Callback<APIResponse<List<TopAlbum>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<TopAlbum>>> call, Response<APIResponse<List<TopAlbum>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listDiscography.setValue(response.body().getData());
                } else {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse<List<TopAlbum>>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        }));
    }
}

