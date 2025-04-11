package com.example.spotifyclone.features.home.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.download.SongDatabaseHelper;
import com.example.spotifyclone.features.home.network.HomeService;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.artist.model.Artist;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.PaginatedResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Album>> latestAlbums = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Album>> popularAlbums = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Song>> newSongs = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Song>> popularSongs = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Artist>> popularArtist = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Song>> localSongs = new MutableLiveData<>(new ArrayList<>());
    private final HomeService homeService;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private SongDatabaseHelper songDatabaseHelper;

    public HomeViewModel(Context context) {
        homeService = RetrofitClient.getClient(context).create(HomeService.class);
        songDatabaseHelper = new SongDatabaseHelper(context);
        fetchData();
    }

    void fetchData() {
        fetchLocalSongs();
        fetchNewSongs();
        fetchPopularSongs();
        fetchPopularAlbums();
        fetchLatestAlbums();
        fetchPopularArtists();
    }

    private void fetchLocalSongs() {
        List<Song> songs = songDatabaseHelper.getAllSavedSongs();
        if (songs != null && !songs.isEmpty()) {
            localSongs.setValue(songs);
        } else {
            errorMessage.setValue("No local songs found");
        }
    }

    private void fetchPopularArtists() {
        homeService.getPopularArtists().enqueue(new Callback<APIResponse<PaginatedResponse<Artist>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Artist>>> call, Response<APIResponse<PaginatedResponse<Artist>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    popularArtist.setValue(response.body().getData().getItems());
                } else {
                    errorMessage.setValue("Failed to load albums");
                }
            }

            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Artist>>> call, Throwable t) {
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    private void fetchLatestAlbums() {
        homeService.getLatestAlbums().enqueue(new Callback<APIResponse<PaginatedResponse<Album>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Album>>> call, Response<APIResponse<PaginatedResponse<Album>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    latestAlbums.setValue(response.body().getData().getItems());
                } else {
                    errorMessage.setValue("Failed to load albums");
                }
            }

            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Album>>> call, Throwable t) {
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    private void fetchPopularAlbums() {
        homeService.getPopularAlbums().enqueue(new Callback<APIResponse<PaginatedResponse<Album>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Album>>> call, Response<APIResponse<PaginatedResponse<Album>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    popularAlbums.setValue(response.body().getData().getItems());
                } else {
                    errorMessage.setValue("Failed to load albums");
                }
            }

            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Album>>> call, Throwable t) {
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    private void fetchPopularSongs() {
        homeService.getPopularSongs().enqueue(new Callback<APIResponse<PaginatedResponse<Song>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Song>>> call, Response<APIResponse<PaginatedResponse<Song>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    popularSongs.setValue(response.body().getData().getItems());
                } else {
                    errorMessage.setValue("Failed to load albums");
                }
            }

            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Song>>> call, Throwable t) {
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    private void fetchNewSongs() {
        homeService.getNewSongs().enqueue(new Callback<APIResponse<PaginatedResponse<Song>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Song>>> call, Response<APIResponse<PaginatedResponse<Song>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    newSongs.setValue(response.body().getData().getItems());
                } else {
                    errorMessage.setValue("Failed to load albums");
                }
            }

            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Song>>> call, Throwable t) {
                errorMessage.setValue(t.getMessage());
            }
        });
    }


    public LiveData<List<Album>> getLatestAlbums() {
        return latestAlbums;
    }

    public LiveData<List<Album>> getPopularAlbums() {
        return popularAlbums;
    }

    public LiveData<List<Song>> getNewSongs() {
        return newSongs;
    }

    public LiveData<List<Song>> getPopularSongs() {
        return popularSongs;
    }

    public LiveData<List<Artist>> getPopularArtists() {
        return popularArtist;
    }
    public LiveData<List<Song>> getLocalSongs() {
        return localSongs;
    }
}
