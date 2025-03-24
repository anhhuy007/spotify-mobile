package com.example.spotifyclone.features.playlist.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifyclone.features.playlist.model.Playlist;
import com.example.spotifyclone.features.playlist.network.PlaylistService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.PaginatedResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistViewModel extends ViewModel {
    private final PlaylistService playlistService;
    private final MutableLiveData<List<Playlist>> playlists=new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading=new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage=new MutableLiveData<>();

    private final MutableLiveData<APIResponse<Void>> createPlaylistResponse=new MutableLiveData<>();

    public PlaylistViewModel(PlaylistService playlistService){
        this.playlistService=playlistService;
    }
    public void fetchPlaylists(){
        isLoading.setValue(true);
        playlistService.getUserPlaylist().enqueue(new Callback<APIResponse<PaginatedResponse<Playlist>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Playlist>>> call, Response<APIResponse<PaginatedResponse<Playlist>>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        playlists.setValue(response.body().getData().getItems());
                    } else {
                        errorMessage.setValue("Failed to load albums");
                    }
                } else {
                    if (response.errorBody() != null) {
                        try {
                            Log.e("PlaylistViewModel", "Response Error: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("PlaylistViewModel", "Lỗi khi đọc errorBody", e);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Playlist>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
                Log.e("PlaylistViewModel", "onFailure: API không gọi được - " + t.getMessage(), t);
            }
        });

    }

    public void createPlaylist(String songId, String playlistName) {
        playlistService.createPlaylist(songId, playlistName).enqueue(new Callback<APIResponse<Void>>() {
            @Override
            public void onResponse(Call<APIResponse<Void>> call, Response<APIResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    createPlaylistResponse.setValue(response.body());
                } else {
                    createPlaylistResponse.setValue(new APIResponse<>());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Void>> call, Throwable t) {
                createPlaylistResponse.setValue(new APIResponse<>());
            }
        });
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<String> getErrorMessage(){
        return errorMessage;
    }
    public LiveData<List<Playlist>> getUserPlaylist(){return playlists;}
    public LiveData<APIResponse<Void>> getCreatePlaylistResponse() {
        return createPlaylistResponse;
    }



}


