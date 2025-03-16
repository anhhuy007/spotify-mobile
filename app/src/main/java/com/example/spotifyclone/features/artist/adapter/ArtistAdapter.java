package com.example.spotifyclone.features.artist.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.artist.model.Item;
import com.example.spotifyclone.features.artist.ui.ArtistActivity;
import com.example.spotifyclone.shared.ui.DominantColorExtractor;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    private Context context;
    private List<Item> artistList;

    public ArtistAdapter(Context context, List<Item> artistList) {
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = artistList.get(position);
        holder.textView.setText(item.getName());
        Glide.with(context)
                .load(item.getAvatarUrl())
                .placeholder(R.drawable.loading)
                .into(holder.imageView);
        holder.artistItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ArtistActivity.class);
                intent.putExtra("ARTIST_ID", item.getId());
                context.startActivity(intent);

            }
        });
        // extract color from picture
        DominantColorExtractor.getDominantColor(context, item.getAvatarUrl(), color -> {
            int baseColor = ContextCompat.getColor(context, R.color.white);
            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{color, baseColor}
            );

            gradient.setCornerRadius(0f);

            holder.artist_item_container.setBackground(gradient);
        });
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CardView artistItem;
        ImageView imageView;

        ConstraintLayout artist_item_container;

        public ViewHolder(View itemView) {
            super(itemView);
            artistItem = itemView.findViewById(R.id.artist_item);
            textView = itemView.findViewById(R.id.artist_name);
            imageView = itemView.findViewById(R.id.artist_image);
            artist_item_container = itemView.findViewById(R.id.artist_item_container);
        }
    }
}