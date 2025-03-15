package com.example.spotifyclone.features.album.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.album.adapter.AlbumAdapter;
import com.example.spotifyclone.features.album.adapter.AlbumSongAdapter;
import com.example.spotifyclone.features.album.inter.AlbumMainCallbacks;
import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModel;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModelFactory;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;

import java.util.ArrayList;
import java.util.List;


public class AlbumDetailFragment extends Fragment {
    private ImageView albumImage;
    private TextView artist_name;
    private TextView albumName;
    private RecyclerView recyclerView;
    private RecyclerView artist_album;
    private RecyclerView related_album;
    private AlbumSongAdapter songAdapter;
    private ImageView artist_image;
    private TextView artist_name2;

    private TextView day_create;
    private TextView artist_album_text;

    private AlbumAdapter artist_albumAdapter;

    private AlbumAdapter related_albumAdapter;
    private CardView album_info;
    private AlbumMainCallbacks callback;
    private Toolbar toolbar;

    private NestedScrollView nestedScrollView;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AlbumMainCallbacks) {
            callback = (AlbumMainCallbacks) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MainCallbacks");
        }
    }
    public static AlbumDetailFragment newInstance(Album album) {

        AlbumDetailFragment fragment = new AlbumDetailFragment();
        Bundle bundle = new Bundle();
        // Put infomation in bundle (safe way to store data)
        bundle.putString("cover_url", album.getCoverUrl());
        bundle.putString("name", album.getTitle());
        bundle.putString("id", album.getId());
        bundle.putString("day_create", album.getReleaseDate().toString());
        bundle.putStringArrayList("artists_name", new ArrayList<>(album.getArtistIds()));
        fragment.setArguments(bundle);
        bundle.putString("artist_url", album.getCoverUrl());
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_album_detail, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        albumImage=view.findViewById(R.id.album_image);
//        albumName=view.findViewById(R.id.album_name);
        artist_name=view.findViewById(R.id.artist_name);
//        album_info=view.findViewById(R.id.album_info);
//        backButton=view.findViewById(R.id.backButton);
        // artist info
        artist_name2 = view.findViewById(R.id.artist);
        artist_image = view.findViewById(R.id.artist_image);
        artist_album_text=view.findViewById(R.id.artist_album_text);

        toolbar=view.findViewById(R.id.toolbar);

        if (album_info == null) {
            Log.e("AlbumDetailFragment", "album_info is null");
        }




//        albumName.setText(getArguments().getString("name"));
        List<String> artistNames = getArguments().getStringArrayList("artists_name");

        if (artistNames != null) {
            artist_name.setText(TextUtils.join(", ", artistNames));  // Join with ", "
        }
        else{
            Log.d("albumdetailfragmemt", "array iss null");
        }
        Glide.with(getContext())
                .load(getArguments().getString("cover_url"))
                .into(albumImage);
        setupViewModel();
        setupRecyclerView(view);


        //  artist info
        artist_name2.setText(TextUtils.join(", ", artistNames));
        Glide.with(getContext())
                .load(getArguments().getString("artist_url"))
                .into(artist_image);
        artist_album_text.setText("Thêm nữa từ "+TextUtils.join(", ", artistNames));



        // Tool bar

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        nestedScrollView = view.findViewById(R.id.nestedScrollview);
        // Đặt listener cho sự kiện cuộn
        setupScrollListener();


        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            activity.getSupportActionBar().setDisplayShowTitleEnabled(false); //
            toolbar.setTitle(getArguments().getString("name"));
        }
        // Use MenuProvider to enable using button
        activity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == android.R.id.home) {
                    if(callback!=null){
                        callback.onMsgFromFragToMain("ALBUM DETAIL", null);
                    }
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);


        //         extract color from picture
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkMode = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES);

        int secondColor = isDarkMode ? Color.BLACK : Color.WHITE; // Đổi màu theo theme

        DominantColorExtractor.getDominantColor(getContext(), getArguments().getString("cover_url"), color -> {
            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{color, secondColor} // Dùng màu tùy theo theme
            );

            gradient.setCornerRadius(0f);

            // Tìm ConstraintLayout và đặt background
            ConstraintLayout imageConstraintLayout = view.findViewById(R.id.imageConstraintLayout);
            imageConstraintLayout.setBackground(gradient);
        });

    }
    private void setupScrollListener() {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Nếu scrollY (vị trí cuộn hiện tại) lớn hơn 0, thì đang cuộn lên (hoặc xuống).
                if (scrollY >oldScrollY) {
                    DominantColorExtractor.getDominantColor(getContext(), getArguments().getString("cover_url"), color -> {
                        GradientDrawable gradient = new GradientDrawable(
                                GradientDrawable.Orientation.TOP_BOTTOM,
                                new int[]{color} // Dùng màu tùy theo theme
                        );

                        // Tìm ConstraintLayout và đặt background
                        toolbar.setBackgroundColor(color);
                    });
                } else if(scrollY==0)  {
                    // Nếu scrollY = 0 (đang ở đầu), set lại màu ban đầu (hoặc transparent)
                    toolbar.setBackground(new ColorDrawable(Color.TRANSPARENT)); // Đặt background là ColorDrawable trong suốt
                }
            }
        });


}



    private void setupRecyclerView(View view) {

        // song of album
        recyclerView = view.findViewById(R.id.song_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        songAdapter=new AlbumSongAdapter(requireContext(), new ArrayList<>(), 3);
        recyclerView.setAdapter(songAdapter);

        // album of the same artist
        artist_album=view.findViewById(R.id.artist_album);
        artist_album.setLayoutManager(new LinearLayoutManager(requireContext(),  LinearLayoutManager.HORIZONTAL, false));
        int widthInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 200, requireContext().getResources().getDisplayMetrics()
        );
        artist_albumAdapter= new AlbumAdapter(requireContext(), new ArrayList<>(), album  -> {
            if (callback != null) {
                callback.onMsgFromFragToMain("ALBUM_FRAGMENT", album);
            }
        }, widthInPx, ViewGroup.LayoutParams.WRAP_CONTENT);
        artist_album.setAdapter(artist_albumAdapter);

        // album related
        related_album=view.findViewById(R.id.related_album);
        related_album.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        related_albumAdapter= new AlbumAdapter(requireContext(), new ArrayList<>(), album -> {
            Log.d("AlbumFragment", "Selected album: " + album.getTitle());
            if (callback != null) {
                callback.onMsgFromFragToMain("ALBUM_FRAGMENT", album);
            }
        },widthInPx, ViewGroup.LayoutParams.WRAP_CONTENT);
        related_album.setAdapter(related_albumAdapter);

    }

    private void setupViewModel() {
        AlbumViewModel albumViewModel = new ViewModelProvider(
                this,
                new AlbumViewModelFactory(requireContext())
        ).get(AlbumViewModel.class);

        albumViewModel.getSongs().observe(getViewLifecycleOwner(), songs -> {
            songAdapter.setData(songs);
        });

        albumViewModel.getAlbums().observe(getViewLifecycleOwner(), albums -> {
            artist_albumAdapter.setData(albums);
        });

        albumViewModel.getAlbums().observe(getViewLifecycleOwner(), albums -> {
            related_albumAdapter.setData(albums);
        });
        albumViewModel.fetchAlbumSongs(getArguments().getString("id"));// fetch API after observer is ready
        albumViewModel.fetchAlbumsByIds();

    }

}
