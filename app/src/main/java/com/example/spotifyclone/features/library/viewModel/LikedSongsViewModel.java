package com.example.spotifyclone.features.library.viewModel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.features.library.network.LibraryService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LikedSongsViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<Integer> likedSongsCount = new MutableLiveData<>();

    public LikedSongsViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final Application application;

        public Factory(Application application) {
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(LikedSongsViewModel.class)) {
                return (T) new LikedSongsViewModel(application);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

    public LiveData<Integer> getLikedSongsCount() {
        return likedSongsCount;
    }

    public void fetchLikedSongsCount() {
        Retrofit retrofit = RetrofitClient.getClient(context);
        LibraryService apiService = retrofit.create(LibraryService.class);

        Call<APIResponse<Integer>> call = apiService.getLikedSongsCount();

        call.enqueue(new Callback<APIResponse<Integer>>() {
            @Override
            public void onResponse(Call<APIResponse<Integer>> call, Response<APIResponse<Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    likedSongsCount.setValue(response.body().getData());
                } else {
                    Toast.makeText(context, "Failed to load liked songs count", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Integer>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}