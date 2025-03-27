package com.example.spotifyclone.features.album.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.album.network.AlbumService;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.PaginatedResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumViewModel extends ViewModel {
    private final AlbumService albumService;
    private final MutableLiveData<List<Album>> popular_albums=new MutableLiveData<>();
    private final MutableLiveData<List<Album>> artist_albums=new MutableLiveData<>();
    private final MutableLiveData<List<Song>> album_songs=new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading=new MutableLiveData<>();
    private final MutableLiveData<String > errorMessage=new MutableLiveData<>();

    public AlbumViewModel(AlbumService albumService)
    {
        this.albumService=albumService;
    }
    public void fetchAlbumsByIds(){
        isLoading.setValue(true);
        albumService.getAlbums().enqueue(new Callback<APIResponse<PaginatedResponse<Album>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Album>>> call, Response<APIResponse<PaginatedResponse<Album>>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        popular_albums.setValue(response.body().getData().getItems());
                    } else {
                        errorMessage.setValue("Failed to load albums");
                    }
                } else {
                    if (response.errorBody() != null) {
                        try {
                            Log.e("AlbumViewModel", "Response Error: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("AlbumViewModel", "Lỗi khi đọc errorBody", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Album>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
                Log.e("AlbumViewModel", "onFailure: API không gọi được - " + t.getMessage(), t);
            }
        });
    }
    public void fetchAlbumsByArtists(List<String> artistNames){
        isLoading.setValue(true);
        albumService.getArtistAlbums(artistNames).enqueue(new Callback<APIResponse<PaginatedResponse<Album>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Album>>> call, Response<APIResponse<PaginatedResponse<Album>>> response) {
                isLoading.setValue(true);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        artist_albums.setValue(response.body().getData().getItems());
                    } else {
                        errorMessage.setValue("Failed to load albums");
                    }
                }
                else{
                    try {
                        Log.e("AlbumViewModel", "Response Error: " + response.errorBody().string());

                    }
                    catch (Exception e)
                    {
                        Log.e("AlbumViewModel", "Lỗi khi đọc errorBody", e);
                    }
                }
            }
            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Album>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
                Log.e("AlbumViewModel", "onFailure: API không gọi được - " + t.getMessage(), t);
            }
        });
    }

    public void fetchAlbumSongs(String album_id)//return list of album_songs
    {
        isLoading.setValue(true);

        albumService.getSongs(album_id).enqueue(new Callback<APIResponse<PaginatedResponse<Song>>>() { // Load genre data
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Song>>> call, Response<APIResponse<PaginatedResponse<Song>>> response) {
                try {
                    isLoading.setValue(false);
                    if (response.isSuccessful()) {
                        if (response.body().getData().getItems() != null) {
                            if (response.body().isSuccess()) {
                                album_songs.setValue(response.body().getData().getItems());
                            } else {
                                Log.d("AlbumViewModel", "API success flag false");
                            }
                        } else {
                            Log.d("AlbumViewModel", "Response body is null");
                        }
                    } else {
                        Log.d("AlbumViewModel", "Response not successful: " + response.code());
                    }
                } catch (Exception e) {
                    errorMessage.setValue("Error processing response: " + e.getMessage());
                }
            }
            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Song>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }




    public LiveData<List<Song>> getSongs(){
        return album_songs;
    }

    public LiveData<List<Album>> getAlbums(){
        return popular_albums;
    }
    public LiveData<List<Album>> getArtistAlbums(){
        return artist_albums;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<String> getErrorMessage(){
        return errorMessage;
    }


}
