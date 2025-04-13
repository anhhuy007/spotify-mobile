package com.example.spotifyclone.features.library.viewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.features.follow.model.Follow;
import com.example.spotifyclone.features.follow.network.FolowService;
import com.example.spotifyclone.features.library.model.LibraryArtist;
import com.example.spotifyclone.features.library.model.SelectableArtist;
import com.example.spotifyclone.features.library.network.LibraryService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ArtistSelectionViewModel extends AndroidViewModel {
    private final Context context;
    private LibraryArtistsViewModel subViewModel;
    private final MutableLiveData<List<SelectableArtist>> artistsList = new MutableLiveData<>();

    public void setSubViewModel(LibraryArtistsViewModel subVM){
        this.subViewModel= subVM;
    }

    public ArtistSelectionViewModel(@NonNull Application application) {
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
            if (modelClass.isAssignableFrom(ArtistSelectionViewModel.class)) {
                return (T) new ArtistSelectionViewModel(application);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

    public LiveData<List<SelectableArtist>> getArtistsList() {
        return artistsList;
    }


    public void fetchSuggestedArtists() {
        Retrofit retrofit = RetrofitClient.getClient(context);
        LibraryService apiService = retrofit.create(LibraryService.class);

        Call<APIResponse<List<SelectableArtist>>> call = apiService.getListFollowedArtists();

        call.enqueue(new Callback<APIResponse<List<SelectableArtist>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<SelectableArtist>>> call, Response<APIResponse<List<SelectableArtist>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    artistsList.setValue(response.body().getData());
                } else {
                }
            }

            @Override
            public void onFailure(Call<APIResponse<List<SelectableArtist>>> call, Throwable t) {
            }
        });
    }

    public void addFollower(Follow follow) {
        Retrofit retrofit = RetrofitClient.getClient(context);
        FolowService apiService = retrofit.create(FolowService.class);

        Call<APIResponse<Follow>> call = apiService.addFollower(follow);
        call.enqueue(new Callback<APIResponse<Follow>>() {
            @Override
            public void onResponse(Call<APIResponse<Follow>> call, Response<APIResponse<Follow>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fetchSuggestedArtists();
                    subViewModel.fetchArtists();
                } else {
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Follow>> call, Throwable t) {
            }
        });
    }

    public void deleteFollower(Follow follow) {
        Retrofit retrofit = RetrofitClient.getClient(context);
        FolowService apiService = retrofit.create(FolowService.class);

        Call<APIResponse<Follow>> call = apiService.deleteFollower1(follow);
        call.enqueue(new Callback<APIResponse<Follow>>() {
            @Override
            public void onResponse(Call<APIResponse<Follow>> call, Response<APIResponse<Follow>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fetchSuggestedArtists();
                    subViewModel.fetchArtists();

                } else {
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Follow>> call, Throwable t) {
            }
        });
    }
}