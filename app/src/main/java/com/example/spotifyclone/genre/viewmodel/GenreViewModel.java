package com.example.spotifyclone.genre.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifyclone.genre.model.Album;
import com.example.spotifyclone.genre.model.Genre;
import com.example.spotifyclone.genre.network.GenreService;
import com.example.spotifyclone.shared.model.APIResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreViewModel extends ViewModel {
    private final GenreService genreService;
    private final MutableLiveData<List<Genre>> genres=new MutableLiveData<>();
    private final MutableLiveData<List<Album>> albums=new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading=new MutableLiveData<>();
    private final MutableLiveData<String > errorMessage=new MutableLiveData<>();

    public GenreViewModel(GenreService genreService)
    {
        Log.d("Genre viewmodel", "constructor");
        this.genreService=genreService;
    }
    public void fetchGenresByIds(){
        isLoading.setValue(true);

        genreService.getGenres().enqueue(new Callback<APIResponse<List<Genre>>>() { // Load genre data

            @Override
            public void onResponse(Call<APIResponse<List<Genre>>> call, Response<APIResponse<List<Genre>>> response) {
                isLoading.setValue(false);
                Log.d("GenreViewModel", "onResponse: " + response.code());

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    genres.setValue(response.body().getData());
                } else {
                    errorMessage.setValue("Failed to load genres");
                }
            }
            @Override
            public void onFailure(Call<APIResponse<List<Genre>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
                Log.d("DEBUG", "onFailure: " + t.getMessage());
            }
        });
    }

    public void fetchGenreAlbumsByIds(){
        isLoading.setValue(true);
        genreService.getGenreAlbums().enqueue(new Callback<APIResponse<List<Album>>>() { // Load genre data

            @Override
            public void onResponse(Call<APIResponse<List<Album>>> call, Response<APIResponse<List<Album>>> response) {
                isLoading.setValue(false);
                Log.d("GenreViewModel", "onResponse: " + response.code());

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    albums.setValue(response.body().getData());
                } else {
                    errorMessage.setValue("Failed to load genres");
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
    public LiveData<List<Genre>> getGenres(){
        return genres;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<String> getErrorMessage(){
        return errorMessage;
    }

    public LiveData<List<Album>> getGenreAlbums(){return albums;}


}
