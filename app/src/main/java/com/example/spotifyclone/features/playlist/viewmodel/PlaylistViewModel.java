package com.example.spotifyclone.features.playlist.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.playlist.model.Playlist;
import com.example.spotifyclone.features.playlist.network.PlaylistService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.PaginatedResponse;

import org.checkerframework.checker.units.qual.A;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistViewModel extends ViewModel {
    private final PlaylistService playlistService;
    private final MutableLiveData<List<Playlist>> userPlaylists=new MutableLiveData<>();
    private final MutableLiveData<List<Song>> playlistSongs=new MutableLiveData<>();
    private final MutableLiveData<List<Song>> popularSongs=new MutableLiveData<>();
    private final MutableLiveData<Playlist> playlsitById=new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading=new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage=new MutableLiveData<>();

    private final MutableLiveData<APIResponse<Void>> createPlaylistResponse=new MutableLiveData<>();
    private final MutableLiveData<APIResponse<Void>> createAddSongToPlaylist=new MutableLiveData<>();
    private final MutableLiveData<APIResponse<Void>> deleteSongFromPlaylist=new MutableLiveData<>();
    private final MutableLiveData<APIResponse<Void>> updatePlaylistInfo=new MutableLiveData<>();

    public PlaylistViewModel(PlaylistService playlistService){
        this.playlistService=playlistService;
    }
    public void fetchPlaylists() {
        isLoading.setValue(true);
        playlistService.getUserPlaylist().enqueue(new Callback<APIResponse<PaginatedResponse<Playlist>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Playlist>>> call, Response<APIResponse<PaginatedResponse<Playlist>>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        List<Playlist> receivedPlaylists = response.body().getData().getItems();
                        userPlaylists.setValue(receivedPlaylists);
                    } else {
                        errorMessage.setValue("Failed to load playlists");
                        Log.e("PlaylistViewModel", "API Response not successful");
                    }
                } else {
                    Log.e("PlaylistViewModel", "API Response failed");
                    if (response.errorBody() != null) {
                        try {
                            Log.e("PlaylistViewModel", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("PlaylistViewModel", "Error parsing errorBody", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Playlist>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
                Log.e("PlaylistViewModel", "API Call failed: " + t.getMessage(), t);
            }
        });
    }

    public void createPlaylist(String songId, String playlistName, String song_image) {
        playlistService.createPlaylist(songId, playlistName, song_image).enqueue(new Callback<APIResponse<Void>>() {
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

    public void addSongToPlaylist(String playlistId, String songId) {
        playlistService.addSongToPlaylist(playlistId, songId).enqueue(new Callback<APIResponse<Void>>() {
            @Override
            public void onResponse(Call<APIResponse<Void>> call, Response<APIResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    createAddSongToPlaylist.setValue(response.body());
                    fetchPlaylistSong(playlistId); //update playlist data.
                } else {
                    createAddSongToPlaylist.setValue(new APIResponse<>());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Void>> call, Throwable t) {
                createAddSongToPlaylist.setValue(new APIResponse<>());
            }
        });
    }

    public void fetchPlaylistById(String id){
        isLoading.setValue(true);
        playlistService.getPlaylistById(id).enqueue(new Callback<APIResponse<Playlist>>() {
            @Override
            public void onResponse(Call<APIResponse<Playlist>> call, Response<APIResponse<Playlist>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Playlist receivedPlaylist = response.body().getData();
                        playlsitById.setValue(receivedPlaylist);

                    } else {
                        errorMessage.setValue("Failed to load playlists");
                        Log.e("PlaylistViewModel", "API Response not successful");
                    }
                } else {
                    Log.e("PlaylistViewModel", "API Response failed");
                    if (response.errorBody() != null) {
                        try {
                            Log.e("PlaylistViewModel", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("PlaylistViewModel", "Error parsing errorBody", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Playlist>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
                Log.e("PlaylistViewModel", "API Call failed: " + t.getMessage(), t);
            }
        });


    }
    public void fetchPlaylistSong(String id){
        isLoading.setValue(true);

        playlistService.getPlaylistSongs(id).enqueue(new Callback<APIResponse<PaginatedResponse<Song>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Song>>> call, Response<APIResponse<PaginatedResponse<Song>>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        playlistSongs.setValue(response.body().getData().getItems());
                    } else {
                        errorMessage.setValue("Failed to load playlists");
                        Log.e("PlaylistViewModel", "API Response not successful");
                    }
                } else {
                    Log.e("PlaylistViewModel", "API Response failed");
                    if (response.errorBody() != null) {
                        try {
                            Log.e("PlaylistViewModel", "Error Body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("PlaylistViewModel", "Error parsing errorBody", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Song>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
                Log.e("PlaylistViewModel", "API Call failed: " + t.getMessage(), t);
            }
        });


    }


    public void removeSongFromPlaylist(String playlistId, String songId) {
        playlistService.removeSongFromPlaylist(playlistId, songId).enqueue(new Callback<APIResponse<Void>>() {
            @Override
            public void onResponse(Call<APIResponse<Void>> call, Response<APIResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    deleteSongFromPlaylist.setValue(response.body());
                    fetchPlaylistSong(playlistId); //fetch again to update UI.
                } else {
                    deleteSongFromPlaylist.setValue(new APIResponse<>());
                }
            }
            @Override
            public void onFailure(Call<APIResponse<Void>> call, Throwable t) {
                deleteSongFromPlaylist.setValue(new APIResponse<>());
            }
        });
    }

    public void fetchPopularSongs(String playlistId)
    {
        isLoading.setValue(true);
        playlistService.getSongPopular(playlistId, 5).enqueue(new Callback<APIResponse<PaginatedResponse<Song>>>() {
            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Song>>> call, Response<APIResponse<PaginatedResponse<Song>>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        popularSongs.setValue(response.body().getData().getItems());
                    } else {
                        errorMessage.setValue("Failed to load songs");
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
            public void onFailure(Call<APIResponse<PaginatedResponse<Song>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
                Log.e("PlaylistViewModel", "onFailure: API không gọi được - " + t.getMessage(), t);
            }
        });

    }

    public void updatePlaylistInfo(String playlistId, String playlist_name, String playlist_description) {
        playlistService.updateInfo(playlistId, playlist_name, playlist_description).enqueue(new Callback<APIResponse<Void>>() {
            @Override
            public void onResponse(Call<APIResponse<Void>> call, Response<APIResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updatePlaylistInfo.setValue(response.body());
                    fetchPlaylistById(playlistId); //update playlistdata
                } else {
                    updatePlaylistInfo.setValue(new APIResponse<>());
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Void>> call, Throwable t) {
                updatePlaylistInfo.setValue(new APIResponse<>());
            }
        });

    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<String> getErrorMessage(){
        return errorMessage;
    }
    public LiveData<List<Playlist>> getUserPlaylist(){return userPlaylists;}
    public LiveData<List<Song>> getPlaylistSongs(){return playlistSongs;}
    public LiveData<List<Song>> getPopularSongs(){return popularSongs;}
    public LiveData<Playlist> getPlaylistById(){
        return playlsitById;
    }
    public LiveData<APIResponse<Void>> getCreatePlaylistResponse() {
        return createPlaylistResponse;
    }
    public LiveData<APIResponse<Void>> getAddSongToPlaylistResponse() {
        return createAddSongToPlaylist;
    }
    public LiveData<APIResponse<Void>> getRemoveSongFromPlaylistResponse() {
        return deleteSongFromPlaylist;
    }

    public LiveData<APIResponse<Void>> getUpdatePlaylistResponse() {
        return updatePlaylistInfo;
    }





}


