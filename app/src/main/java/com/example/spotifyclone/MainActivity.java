package com.example.spotifyclone;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentActivity;

import com.example.spotifyclone.album_ids.model.Album;
import com.example.spotifyclone.album_ids.ui.AlbumDetailFragment;
import com.example.spotifyclone.album_ids.ui.AlbumFragment;
import com.example.spotifyclone.album_ids.ui.AlbumMainCallbacks;
import com.example.spotifyclone.genre_ids.model.Genre;
import com.example.spotifyclone.genre_ids.ui.GenreDetailFragment;
import com.example.spotifyclone.genre_ids.ui.GenreFragment;
import com.example.spotifyclone.genre_ids.ui.GenreMainCallbacks;

public class MainActivity extends FragmentActivity implements GenreMainCallbacks, AlbumMainCallbacks {
    //    private GenreFragment genreFragment;


    private EditText search_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // Bật Dark Mode

//
//        // Create GenreFragment
//        setContentView(R.layout.activity_genrelayout);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.genre_list_holder, new GenreFragment())
//                .commit();
//          Create AlbumFragment
        setContentView(R.layout.activity_albumlayout);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.album_list_holder, new AlbumFragment())
                .commit();

    }

    @Override
    public void onMsgFromFragToMain(String sender, Genre genre) {
        if (sender.equals("GENRE_FRAGMENT")) {
            Log.d("MainActivity", "Genre selected: " + genre.getName());
            GenreDetailFragment detailFragment = GenreDetailFragment.newInstance(genre);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.genre_list_holder, detailFragment)
                    .addToBackStack(null) // add to backstack
                    .commit();
            // Hide search bar
            search_input=findViewById(R.id.search_input);
            search_input.setVisibility(View.GONE);

        }
        else if(sender.equals("GENRE DETAIL")){
            getSupportFragmentManager().popBackStack(); // Quay lại Fragment trước đó
            search_input.setVisibility(View.VISIBLE);
        }

    }
    @Override
    public void onMsgFromFragToMain(String sender, Album album) {
        if (sender.equals("ALBUM_FRAGMENT")) {
            Log.d("MainActivity", "Album selected: " + album.getTitle());

            AlbumDetailFragment detailFragment = AlbumDetailFragment.newInstance(album);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.album_list_holder, detailFragment)
                    .addToBackStack(null) //add to backstack
                    .commit();
            // Hide search bar
            search_input=findViewById(R.id.search_input);
            search_input.setVisibility(View.GONE);

        }
        else if(sender.equals("ALBUM DETAIL")){
            Log.d("Main", "Have been step on there");
            getSupportFragmentManager().popBackStack(); // Quay lại Fragment trước đó
            search_input.setVisibility(View.VISIBLE);
        }

    }


}
