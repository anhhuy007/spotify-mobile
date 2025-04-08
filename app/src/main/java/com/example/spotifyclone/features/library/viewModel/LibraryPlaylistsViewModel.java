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

import com.example.spotifyclone.features.library.model.LibraryPlaylist;
import com.example.spotifyclone.features.library.network.LibraryService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LibraryPlaylistsViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<List<LibraryPlaylist>> playlistsList = new MutableLiveData<>();

    public LibraryPlaylistsViewModel(@NonNull Application application) {
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
            if (modelClass.isAssignableFrom(LibraryPlaylistsViewModel.class)) {
                return (T) new LibraryPlaylistsViewModel(application);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

    public LiveData<List<LibraryPlaylist>> getPlaylistsList() {
        return playlistsList;
    }

    public void createPlaylist( String playlistName, String song_image) {

        Retrofit retrofit = RetrofitClient.getClient(context);
        LibraryService apiService = retrofit.create(LibraryService.class);

        Call<APIResponse<LibraryPlaylist>> call = apiService.createPlaylist(playlistName,song_image);

        call.enqueue(new Callback<APIResponse<LibraryPlaylist>>() {
            @Override
            public void onResponse(Call<APIResponse<LibraryPlaylist>> call, Response<APIResponse<LibraryPlaylist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fetchPlaylists();
                } else {
                    Toast.makeText(context, "Failed to load playlists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse<LibraryPlaylist>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void fetchPlaylists() {
        Retrofit retrofit = RetrofitClient.getClient(context);
        LibraryService apiService = retrofit.create(LibraryService.class);

        Call<APIResponse<List<LibraryPlaylist>>> call = apiService.getPlaylists();

        call.enqueue(new Callback<APIResponse<List<LibraryPlaylist>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<LibraryPlaylist>>> call, Response<APIResponse<List<LibraryPlaylist>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    playlistsList.setValue(response.body().getData());
                } else {
                    Toast.makeText(context, "Failed to load playlists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse<List<LibraryPlaylist>>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}