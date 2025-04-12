package com.example.spotifyclone.features.library.viewModel;


import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import android.util.Log;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.features.library.model.LibraryArtist;
import com.example.spotifyclone.features.library.network.LibraryService;

public class LibraryArtistsViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<List<LibraryArtist>> artistsList = new MutableLiveData<>();

    public LibraryArtistsViewModel(@NonNull Application application) {
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
            if (modelClass.isAssignableFrom(LibraryArtistsViewModel.class)) {
                return (T) new LibraryArtistsViewModel(application);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

    public LiveData<List<LibraryArtist>> getArtistsList() {
        return artistsList;
    }

    public void fetchArtists() {
        Retrofit retrofit = RetrofitClient.getClient(context);
        LibraryService apiService = retrofit.create(LibraryService.class);

        Call<APIResponse<List<LibraryArtist>>> call = apiService.getFollowedArtists();

        call.enqueue(new Callback<APIResponse<List<LibraryArtist>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<LibraryArtist>>> call, Response<APIResponse<List<LibraryArtist>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    artistsList.setValue(response.body().getData());
                } else {
                    Toast.makeText(context, "Failed to load artists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse<List<LibraryArtist>>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }
}