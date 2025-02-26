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
import com.example.spotifyclone.features.artist.model.artist;
import com.example.spotifyclone.features.artist.ui.artistDetailUI;

import java.util.List;

public class artistAdapter extends RecyclerView.Adapter<artistAdapter.ViewHolder> {
    private Context context;
    private List<artist> artistList;

    public artistAdapter(Context context, List<artist> artistList) {
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
        artist item = artistList.get(position);
        holder.textView.setText(item.getId());
        Glide.with(context)
                .load(item.getAvatarUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageView);
        holder.artistItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), artistDetailUI.class);
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