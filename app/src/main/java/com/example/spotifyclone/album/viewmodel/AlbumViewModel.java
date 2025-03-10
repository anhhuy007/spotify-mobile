package com.example.spotifyclone.album.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.spotifyclone.album.model.Album;
import com.example.spotifyclone.album.model.Song;
import com.example.spotifyclone.album.network.AlbumService;
import com.example.spotifyclone.shared.model.APIResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumViewModel extends ViewModel {
    private final AlbumService albumService;
    private final MutableLiveData<List<Album>> albums=new MutableLiveData<>();
    private final MutableLiveData<List<Song>> album_songs=new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading=new MutableLiveData<>();
    private final MutableLiveData<String > errorMessage=new MutableLiveData<>();

    public AlbumViewModel(AlbumService albumService)
    {
        Log.d("Album viewmodel", "constructor");
        this.albumService=albumService;
    }
    public void fetchAlbumsByIds(){
        isLoading.setValue(true);
        albumService.getAlbums().enqueue(new Callback<APIResponse<List<Album>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<Album>>> call, Response<APIResponse<List<Album>>> response) {
                isLoading.setValue(false);
                Log.d("AlbumViewModel", "onResponse: " + response.body().getData());

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    albums.setValue(response.body().getData());
                } else {
                    errorMessage.setValue("Failed to load albums");
                }
            }
            @Override
            public void onFailure(Call<APIResponse<List<Album>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
                Log.d("DEBUG", "onFailure: " + t.getMessage());
            }
        });
    }

    public void fetchAlbumSongs(String album_id)//return list of album_songs
    {
        isLoading.setValue(true);
        albumService.getSongs(album_id).enqueue(new Callback<APIResponse<List<Song>>>() { // Load genre data
            @Override
            public void onResponse(Call<APIResponse<List<Song>>> call, Response<APIResponse<List<Song>>> response) {
                isLoading.setValue(false);
                Log.d("AlbumViewModel", "onResponse: " + response.body().getData());

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    album_songs.setValue(response.body().getData());
                } else {
                    errorMessage.setValue("Failed to load albums");
                }
            }
            @Override
            public void onFailure(Call<APIResponse<List<Song>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
                Log.d("DEBUG", "onFailure: " + t.getMessage());
            }
        });
    }

    public LiveData<List<Song>> getSongs(){
        return album_songs;
    }

    public LiveData<List<Album>> getAlbums(){
        return albums;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<String> getErrorMessage(){
        return errorMessage;
    }


}
