package com.example.spotifyclone.features.artist.viewModel;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.features.artist.adapter.artistAdapter;
import com.example.spotifyclone.features.artist.model.apiArtistService;
import com.example.spotifyclone.features.artist.model.artist;
import com.example.spotifyclone.features.artist.model.artistRetrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class artistListViewModel extends ViewModel {
    private final Context context;
    private RecyclerView recyclerView;
    private artistAdapter ArtistAdapter;

    public artistListViewModel(RecyclerView recyclerView, Context context) {
        this.recyclerView = recyclerView;
        this.context = context;
        fetchItems();
    }


    public void fetchItems() {
        Retrofit retrofit = artistRetrofit.getClient();
        apiArtistService apiService = retrofit.create(apiArtistService.class);

        Call<List<artist>> call = apiService.getListArtist();
        call.enqueue(new Callback<List<artist>>() {
            @Override
            public void onResponse(Call<List<artist>> call, Response<List<artist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArtistAdapter = new artistAdapter(context, response.body());
                    recyclerView.setAdapter(ArtistAdapter);
                } else {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<artist>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
