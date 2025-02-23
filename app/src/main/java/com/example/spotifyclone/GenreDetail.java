package com.example.spotifyclone;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class GenreDetail extends AppCompatActivity {
    private ImageView genre_image;
    private TextView genre_description;
    private TextView genre_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_genre_detail);

        genre_image=findViewById(R.id.genre_image);
        genre_description=findViewById(R.id.genre_description);
        genre_name=findViewById(R.id.genre_name);

        Bundle extra=getIntent().getExtras();
        Glide.with(this).load(extra.getString("image_url")).into(genre_image);
        genre_description.setText(extra.getString("description"));
        genre_name.setText(extra.getString("name"));

    }

}
