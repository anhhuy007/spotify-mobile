package com.example.spotifyclone.features.artist.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.artist.model.artistDetail;
import com.example.spotifyclone.features.artist.ui.ArtistOverallUI;
import com.example.spotifyclone.features.artist.ui.ArtistUI;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    private Context context;
    private List<artistDetail> artistList;

    public ArtistAdapter(Context context, List<artistDetail> artistList) {
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.artist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        artistDetail item = artistList.get(position);
        holder.textView.setText(item.getName());
        Glide.with(context)
                .load(item.getAvatarUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageView);
        holder.artistItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ArtistUI.class);
                intent.putExtra("ARTIST_ID", item.getId());
                context.startActivity(intent);

            }
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

        public ViewHolder(View itemView) {
            super(itemView);
            artistItem = itemView.findViewById(R.id.artist_item);
            textView = itemView.findViewById(R.id.artist_name);
            imageView = itemView.findViewById(R.id.artist_image);
        }
    }
}