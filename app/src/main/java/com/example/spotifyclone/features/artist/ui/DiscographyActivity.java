package com.example.spotifyclone.features.artist.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.artist.adapter.ItemDiscographyAlbumAdapter;
import com.example.spotifyclone.features.artist.adapter.ItemDiscographyEPAdapter;
import com.example.spotifyclone.features.artist.viewModel.ListDiscographyAlbumViewModel;
import com.example.spotifyclone.features.artist.viewModel.ListDiscographyEPViewModel;


public class DiscographyActivity extends AppCompatActivity {


    private RecyclerView rvAlbums, rvEPs, rvCollection, rvHave;
    private Context context;
    private ImageButton btnBackDiscography;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discography);
        context = this;

        String artistId = getIntent().getStringExtra("ARTIST_ID");
        if (artistId == null || artistId.isEmpty()) {
            Toast.makeText(context, "Invalid Artist ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize RecyclerViews
        rvAlbums = findViewById(R.id.rvAlbums);
        rvEPs = findViewById(R.id.rvEPs);
        rvCollection = findViewById(R.id.rvCollection);
        rvHave = findViewById(R.id.rvHave);
        btnBackDiscography = findViewById(R.id.btnBackDiscography);


        // Set layout managers
        rvAlbums.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvEPs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvCollection.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvHave.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        btnBackDiscography.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //rvAlbum
        ListDiscographyAlbumViewModel listDiscographyViewModelAlbum =  new ViewModelProvider(this,
                new ListDiscographyAlbumViewModel.Factory(getApplication(), artistId,1))
                .get(ListDiscographyAlbumViewModel.class);
        listDiscographyViewModelAlbum.getListDiscography().observe(this, item -> {
            if (item != null) {
                ItemDiscographyAlbumAdapter rvPopularSongsAdapter = new ItemDiscographyAlbumAdapter(context, item);
                rvAlbums.setAdapter(rvPopularSongsAdapter);
            }
            ViewGroup.LayoutParams params = rvAlbums.getLayoutParams();
            params.height = (int) (116 * getResources().getDisplayMetrics().density*item.size());
            rvAlbums.setLayoutParams(params);
        });
        listDiscographyViewModelAlbum.fetchItems();

        //rvEP
        ListDiscographyEPViewModel listDiscographyViewModelEP =  new ViewModelProvider(this,
                new ListDiscographyEPViewModel.Factory(getApplication(), artistId,1))
                .get(ListDiscographyEPViewModel.class);
        listDiscographyViewModelEP.getListDiscography().observe(this, item -> {
            if (item != null) {
                ItemDiscographyEPAdapter rvPopularSongsAdapter = new ItemDiscographyEPAdapter(context, item);
                rvEPs.setAdapter(rvPopularSongsAdapter);
            }
            ViewGroup.LayoutParams params = rvEPs.getLayoutParams();
            params.height = (int) (116 * getResources().getDisplayMetrics().density*item.size());
            rvEPs.setLayoutParams(params);
        });
        listDiscographyViewModelEP.fetchItems();

        //rvCollection
        ListDiscographyAlbumViewModel listDiscographyViewModelCollection =  new ViewModelProvider(this,
                new ListDiscographyAlbumViewModel.Factory(getApplication(), artistId,3))
                .get(ListDiscographyAlbumViewModel.class);
        listDiscographyViewModelCollection.getListDiscography().observe(this, item -> {
            if (item != null) {
                ItemDiscographyAlbumAdapter rvPopularSongsAdapter = new ItemDiscographyAlbumAdapter(context, item);
                rvCollection.setAdapter(rvPopularSongsAdapter);
            }
            ViewGroup.LayoutParams params = rvCollection.getLayoutParams();
            params.height = (int) (116 * getResources().getDisplayMetrics().density*item.size());
            rvCollection.setLayoutParams(params);
        });
        listDiscographyViewModelCollection.fetchItems();

        //rvHave
        ListDiscographyEPViewModel listDiscographyViewModelHave =  new ViewModelProvider(this,
                new ListDiscographyEPViewModel.Factory(getApplication(), artistId,1))
                .get(ListDiscographyEPViewModel.class);
        listDiscographyViewModelHave.getListDiscography().observe(this, item -> {
            if (item != null) {
                ItemDiscographyEPAdapter rvPopularSongsAdapter = new ItemDiscographyEPAdapter(context, item);
                rvHave.setAdapter(rvPopularSongsAdapter);
            }
            ViewGroup.LayoutParams params = rvHave.getLayoutParams();
            params.height = (int) (116 * getResources().getDisplayMetrics().density*item.size());
            rvHave.setLayoutParams(params);
        });
        listDiscographyViewModelHave.fetchItems();



        setupTabListeners();
    }

    private void setupTabListeners() {
        findViewById(R.id.tabAlbum).setOnClickListener(v -> {
            // Cuộn đến phần album
            scrollToView(rvAlbums);
        });

        findViewById(R.id.tabEP).setOnClickListener(v -> {
            // Cuộn đến phần EP
            scrollToView(rvEPs);
        });

        findViewById(R.id.tabCollection).setOnClickListener(v -> {
            // Cuộn đến phần collection
            scrollToView(rvCollection);
        });

        findViewById(R.id.tabHave).setOnClickListener(v -> {
            // Cuộn đến phần have
            scrollToView(rvHave);
        });
    }

    private void scrollToView(View view) {
        // Tìm NestedScrollView
        androidx.core.widget.NestedScrollView scrollView = findViewById(R.id.rvAlbums)
                .getParent().getParent() instanceof androidx.core.widget.NestedScrollView ?
                (androidx.core.widget.NestedScrollView) findViewById(R.id.rvAlbums).getParent().getParent() : null;

        if (scrollView != null) {
            // Tìm phần tử tiêu đề (TextView) đứng trước RecyclerView
            ViewGroup parentLayout = (ViewGroup) view.getParent();
            int index = parentLayout.indexOfChild(view);

            // Giả định rằng TextView tiêu đề luôn đứng trước RecyclerView
            if (index > 0) {
                View header = parentLayout.getChildAt(index - 1);
                // Cuộn đến vị trí của tiêu đề
                scrollView.post(() -> scrollView.smoothScrollTo(0, header.getTop()));
            } else {
                // Nếu không tìm thấy tiêu đề, cuộn đến RecyclerView
                scrollView.post(() -> scrollView.smoothScrollTo(0, view.getTop()));
            }
        }
    }
}
