package com.example.spotifyclone.features.artist.ui;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.artist.adapter.artistAdapter;
import com.example.spotifyclone.features.artist.model.apiArtistService;
import com.example.spotifyclone.features.artist.model.artist;
import com.example.spotifyclone.features.artist.model.artistRetrofit;
import com.example.spotifyclone.features.artist.viewModel.artistListViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class artistListUI extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_list);
        context = this;
        recyclerView = findViewById(R.id.artist_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        artistListViewModel ArtistListViewModel = new artistListViewModel(recyclerView,context);
    }


}