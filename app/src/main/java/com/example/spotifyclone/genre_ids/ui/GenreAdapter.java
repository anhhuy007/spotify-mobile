package com.example.spotifyclone.genre_ids.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.spotifyclone.GenreActivity;
import com.example.spotifyclone.GenreDetail;
import com.example.spotifyclone.R;
import com.example.spotifyclone.genre_ids.model.Genre;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

    private final List<Genre> genres;
    private Context context;

    public GenreAdapter(Context context, List <Genre>genres)
    {
        Log.d("holder", "Initialize");

        this.context=context;
        this.genres=genres;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_genre_item, parent, false); //convert xml to view object
        Log.d("holder", "On create viewholder");

        return new ViewHolder(view);//create new viewholder for each item
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.d("holder", "holder item");
        Genre genre=genres.get(position);
        if(genre==null)
        {
            return ;
        }
        int radius = 20; // Độ bo góc
        MultiTransformation<Bitmap> multi = new MultiTransformation<>(
                new CenterCrop(),
                new RoundedCorners(radius)
        );

        RequestOptions requestOptions = new RequestOptions()
                .transform(multi);

        Glide.with(context)
                .load(genre.getImg_url())
                .apply(requestOptions)
                .into(holder.imageView);
        holder.category_name.setText(genre.getName());

        //Switch to another intent: catch event when cliking on image
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GenreDetail.class);
            intent.putExtra("image_url", genre.getImg_url());
            intent.putExtra("description", genre.getDescription());
            intent.putExtra("name", genre.getName());
            context.startActivity(intent);
        });



    }

    @Override
    public int getItemCount() {
        if(genres!=null)
        {
            return genres.size();
        }
        return 0;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView imageView;
        private final TextView category_name;
        public ViewHolder(@NonNull View itemView){//Create ViewHolder to contain image of a item
            super(itemView);
            imageView=itemView.findViewById(R.id.genre_image);
            category_name=itemView.findViewById(R.id.genre_name);
        }

    }
}
