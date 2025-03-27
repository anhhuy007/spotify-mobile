package com.example.spotifyclone.features.follow.viewModel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.spotifyclone.features.follow.model.FollowCountResponse;
import com.example.spotifyclone.features.follow.network.FolowService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FollowedArtistsCountViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<FollowCountResponse> count = new MutableLiveData<>();

    public FollowedArtistsCountViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<FollowCountResponse> getCount() {
        return count;
    }

    public void fetchCount(String userId) {
        Retrofit retrofit = RetrofitClient.getClient(context);
        FolowService apiService = retrofit.create(FolowService.class);

        Call<APIResponse<FollowCountResponse>> call = apiService.getCountFollowedArtists(userId);
        call.enqueue(new Callback<APIResponse<FollowCountResponse>>() {
            @Override
            public void onResponse(Call<APIResponse<FollowCountResponse>> call, Response<APIResponse<FollowCountResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    count.setValue(response.body().getData());
                } else {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse<FollowCountResponse>> call, Throwable t) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
