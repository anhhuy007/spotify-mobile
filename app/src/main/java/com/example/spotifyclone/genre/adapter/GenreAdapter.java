package com.example.spotifyclone.genre.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.spotifyclone.R;
import com.example.spotifyclone.genre.inter.OnGenreItemClickListener;
import com.example.spotifyclone.genre.model.Genre;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

    private List<Genre> genres;
    private final Context context;
    private final OnGenreItemClickListener listener;

    public GenreAdapter(Context context, List<Genre> genres, OnGenreItemClickListener listener) {
        this.context = context;
        this.genres = genres;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_genre_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(genres.get(position), context);
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public void setData(List<Genre> genres) {
        this.genres = genres;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView categoryName;
        private final ConstraintLayout genre_constraint;



        public ViewHolder(@NonNull View itemView, OnGenreItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.genre_image);
            categoryName = itemView.findViewById(R.id.genre_name);
            genre_constraint=itemView.findViewById(R.id.genre_constraint);

            itemView.setOnClickListener(v -> {
                Genre genre = (Genre) v.getTag();
                if (listener != null && genre != null) {
                    listener.OnItemClick(genre);
                }
            });
        }

        public void bind(Genre genre, Context context) {
            categoryName.setText(genre.getName());

            int radius = 20;
            Glide.with(context)
                    .load(genre.getImg_url())
                    .apply(new RequestOptions().transform(
                            new MultiTransformation<>(new CenterCrop(), new RoundedCorners(radius))
                    ))
                    .into(imageView);

            //         extract color from picture
            DominantColorExtractor.getDominantColor(context, genre.getImg_url(), color -> {
                if (color == 0) {
                    Log.e("ColorExtractor", "Không thể lấy màu từ ảnh!");
                    return;
                }
                GradientDrawable gradient = new GradientDrawable();
                gradient.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
                gradient.setColors(new int[]{color, Color.TRANSPARENT}); // Dùng Color.TRANSPARENT để tránh lỗi hiển thị
                gradient.setCornerRadius(0f);
                genre_constraint.setBackground(gradient);
            });


            itemView.setTag(genre);
        }
    }
}
