package com.example.spotifyclone.features.follow.viewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.spotifyclone.features.follow.model.Follow;
import com.example.spotifyclone.features.follow.network.FolowService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddFollowerViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<Follow> addedFollow = new MutableLiveData<>();

    public AddFollowerViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public MutableLiveData<Follow> getAddedFollow() {
        return addedFollow;
    }

    public void addFollower(Follow follow) {
        Retrofit retrofit = RetrofitClient.getClient(context);
        FolowService apiService = retrofit.create(FolowService.class);

        Call<APIResponse<Follow>> call = apiService.addFollower(follow);
        call.enqueue(new Callback<APIResponse<Follow>>() {
            @Override
            public void onResponse(Call<APIResponse<Follow>> call, Response<APIResponse<Follow>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addedFollow.setValue(response.body().getData());
                } else {
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Follow>> call, Throwable t) {
            }
        });
    }
}
