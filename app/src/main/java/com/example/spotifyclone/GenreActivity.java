package com.example.spotifyclone;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.genre_ids.model.Genre;
import com.example.spotifyclone.genre_ids.ui.GenreAdapter;
import com.example.spotifyclone.genre_ids.viewmodel.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Genre> genres;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_genre);

        recyclerView=findViewById(R.id.genre_recycler_view);
        //declare layout manager
        GridLayoutManager layoutManager= new GridLayoutManager(GenreActivity.this, 2) ;
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration=new DividerItemDecoration(GenreActivity.this, DividerItemDecoration.HORIZONTAL);
        recyclerView.addItemDecoration(itemDecoration);
        genres=new ArrayList<>();

        callGenreAPI();
    }
    public void callGenreAPI(){
        RetrofitClient.getApiService().getGenres().enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                genres=response.body();
                for(Genre genre:genres)
                {
                    Log.d("genre",String.valueOf(genre.getId())+ String.valueOf(genre.getImg_url())+String.valueOf(genre.getName()));
                }
                GenreAdapter genreApdapter=new GenreAdapter(GenreActivity.this, genres);
                recyclerView.setAdapter(genreApdapter);
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
                Log.d("genre", "Fail to fecth" + t.getMessage());
            }
        });
    }
}