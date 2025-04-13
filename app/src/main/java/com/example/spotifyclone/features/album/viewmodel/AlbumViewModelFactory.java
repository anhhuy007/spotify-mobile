package com.example.spotifyclone.features.album.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.features.album.network.AlbumService;
import com.example.spotifyclone.shared.network.RetrofitClient;


// for ViewModel to receive variable constructor.
//public class AlbumViewModelFactory implements ViewModelProvider.Factory {
//
//    private final AlbumService albumService;
//
//    public AlbumViewModelFactory(AlbumService albumService) {
//        this.albumService = albumService;
//    }
//
//    @NonNull
//    @Override
//    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//        if (modelClass.isAssignableFrom(com.example.spotifyclone.album_ids.viewmodel.AlbumViewModel.class)) {
//            return (T) new AlbumViewModel(albumService);
//        }
//        throw new IllegalArgumentException("Unknown ViewModel class");
//    }
//}
public class AlbumViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public AlbumViewModelFactory(Context context) {
        this.context = context.getApplicationContext(); // Tránh memory leak
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AlbumViewModel.class)) {
            // Khởi tạo AlbumService bên trong ViewModelFactory
            AlbumService albumService = RetrofitClient.getClient(context).create(AlbumService.class);
            return (T) new AlbumViewModel(albumService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

