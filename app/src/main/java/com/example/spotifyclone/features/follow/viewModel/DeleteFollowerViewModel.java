package com.example.spotifyclone.features.follow.viewModel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.spotifyclone.features.follow.network.FolowService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DeleteFollowerViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<Boolean> isDeleted = new MutableLiveData<>();

    public DeleteFollowerViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public MutableLiveData<Boolean> getIsDeleted() {
        return isDeleted;
    }

    public void deleteFollower(String followId) {
        Retrofit retrofit = RetrofitClient.getClient(context);
        FolowService apiService = retrofit.create(FolowService.class);

        Call<APIResponse<Void>> call = apiService.deleteFollower(followId);
        call.enqueue(new Callback<APIResponse<Void>>() {
            @Override
            public void onResponse(Call<APIResponse<Void>> call, Response<APIResponse<Void>> response) {
                if (response.isSuccessful()) {
                    isDeleted.setValue(true);
                } else {
                    Toast.makeText(context, "Failed to delete follower", Toast.LENGTH_SHORT).show();
                    isDeleted.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Void>> call, Throwable t) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                isDeleted.setValue(false);
            }
        });
    }
}
