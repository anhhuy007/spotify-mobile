package com.example.spotifyclone.features.genre.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.genre.model.Genre;
import com.example.spotifyclone.features.genre.network.GenreService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.PaginatedResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreViewModel extends ViewModel {
    private final GenreService genreService;
    private final MutableLiveData<List<Genre>> genres=new MutableLiveData<>();
    private final MutableLiveData<List<Album>> albums=new MutableLiveData<>();
    private final MutableLiveData<Genre> genreById=new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading=new MutableLiveData<>();
    private final MutableLiveData<String > errorMessage=new MutableLiveData<>();

    public GenreViewModel(GenreService genreService)
    {
        Log.d("Genre viewmodel", "constructor");
        this.genreService=genreService;
    }
    public void fetchGenresByIds(){
        isLoading.setValue(true);

        Log.d("GenreViewModel", "fetchGenresByids");

        genreService.getGenres().enqueue(new Callback<APIResponse<PaginatedResponse<Genre>>>() { // Load genre data

            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Genre>>> call, Response<APIResponse<PaginatedResponse<Genre>>> response) {
                Log.d("GenreViewModel", "onResponse: " + response.code());

                isLoading.setValue(false);

                if (response.isSuccessful()) {
                    // Log the raw response body
                    try {
                        String rawJson = response.body().toString();
                        Log.d("GenreViewModel", "Raw JSON: " + rawJson);
                    } catch (Exception e) {
                        Log.e("GenreViewModel", "Error reading raw response", e);
                    }

                    APIResponse<PaginatedResponse<Genre>> body = response.body();
                    if (body != null) {
                        Log.d("GenreViewModel", "Response Body: " + new Gson().toJson(body));
                        if (body.isSuccess()) {
                            PaginatedResponse<Genre> data = body.getData();
                            if (data != null && data.getItems() != null) {
                                genres.setValue(data.getItems());
                                Log.d("GenreViewModel", "Set genres: " + data.getItems().size());
                            } else {
                                Log.e("GenreViewModel", "Data or items is null");
                            }
                        } else {
                            Log.e("GenreViewModel", "API returned failure: " + body.getMessage());
                        }
                    } else {
                        Log.e("GenreViewModel", "Response body is null");
                    }
                } else {
                    try {
                        Log.e("GenreViewModel", "API error: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("GenreViewModel", "Error reading error body", e);
                    }
                }
            }
            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Genre>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
                Log.d("DEBUG_GENRE", "onFailure: " + t.getMessage());
            }
        });
    }

    public void fetchGenreAlbumsByIds(){
        isLoading.setValue(true);
        genreService.getGenreAlbums().enqueue(new Callback<APIResponse<PaginatedResponse<Album>>>() { // Load genre data

            @Override
            public void onResponse(Call<APIResponse<PaginatedResponse<Album>>> call, Response<APIResponse<PaginatedResponse<Album>>> response) {
                isLoading.setValue(false);
                Log.d("GenreViewModel", "onResponse: " + response.code());

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    albums.setValue(response.body().getData().getItems());
                } else {
                    errorMessage.setValue("Failed to load genres");
                }
            }
            @Override
            public void onFailure(Call<APIResponse<PaginatedResponse<Album>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
                Log.d("DEBUG_Genre", "onFailure: " + t.getMessage());
            }
        });
    }
    public void fetchGenreByID(String genre_id)//return list of album_songs
    {
        isLoading.setValue(true);
        genreService.getGenreById(genre_id).enqueue(new Callback<APIResponse<Genre>>() { // Load genre data
            @Override
            public void onResponse(Call<APIResponse<Genre>> call, Response<APIResponse<Genre>> response) {
                try {
                    isLoading.setValue(false);
                    if (response.isSuccessful()) {
                        if (response.body().getData() != null) {
                            if (response.body().isSuccess()) {
                                genreById.setValue(response.body().getData());
                            } else {
                                Log.d("GenreViewModel", "API success flag false");
                            }
                        } else {
                            Log.d("GenreViewModel", "Response body is null");
                        }
                    } else {
                        Log.d("GenreViewModel", "Response not successful: " + response.code());
                    }
                } catch (Exception e) {
                    errorMessage.setValue("Error processing response: " + e.getMessage());
                }
            }
            @Override
            public void onFailure(Call<APIResponse<Genre>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    public LiveData<List<Genre>> getGenres(){
        return genres;
    }
    public LiveData<Genre> getGenreById(){
        return genreById;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<String> getErrorMessage(){
        return errorMessage;
    }

    public LiveData<List<Album>> getGenreAlbums(){return albums;}


}
