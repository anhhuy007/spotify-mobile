//package com.example.spotifyclone.features.artist.ui;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.widget.ImageButton;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.spotifyclone.R;
//import com.example.spotifyclone.features.artist.adapter.ArtistAdapter;
//import com.example.spotifyclone.features.artist.viewModel.ArtistListViewModel;
//
//public class  ArtistListActivity extends AppCompatActivity {
//    private RecyclerView recyclerView;
//    private Context context;
//    private ImageButton btnBack;
//    private ArtistAdapter artistAdapter;
//    private ArtistListViewModel artistListViewModel;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_artist_list);
//
//        // Initialize context and views
//        context = this;
//        initViews();
//        setupRecyclerView();
//        setupBackButton();
//
//        artistListViewModel = new ViewModelProvider(this).get(ArtistListViewModel.class);
//        artistListViewModel.getArtists().observe(this, artists -> {
//            if (artists != null) {
//                artistAdapter = new ArtistAdapter(this, artists);
//                recyclerView.setAdapter(artistAdapter);
//            }
//        });
//        artistListViewModel.fetchItems();
//    }
//
//    private void initViews() {
//        recyclerView = findViewById(R.id.artist_recycler);
//        btnBack = findViewById(R.id.back_button_artist_list);
//    }
//
//    private void setupRecyclerView() {
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//    }
//
//    private void setupBackButton() {
//        btnBack.setOnClickListener(v -> finish());
//    }
//}