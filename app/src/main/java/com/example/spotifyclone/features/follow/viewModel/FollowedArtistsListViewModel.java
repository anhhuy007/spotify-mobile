package com.example.spotifyclone.features.follow.viewModel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.spotifyclone.features.follow.model.Follow;
import com.example.spotifyclone.features.follow.network.FolowService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FollowedArtistsListViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<List<Follow>> followedArtists = new MutableLiveData<>();

    public FollowedArtistsListViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<List<Follow>> getFollowedArtists() {
        return followedArtists;
    }

    public void fetchFollowedArtists(String userId) {
        Retrofit retrofit = RetrofitClient.getClient(context);
        FolowService apiService = retrofit.create(FolowService.class);

        Call<APIResponse<List<Follow>>> call = apiService.getListFollowedArtists(userId);
        call.enqueue(new Callback<APIResponse<List<Follow>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<Follow>>> call, Response<APIResponse<List<Follow>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    followedArtists.setValue(response.body().getData());
                } else {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse<List<Follow>>> call, Throwable t) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
