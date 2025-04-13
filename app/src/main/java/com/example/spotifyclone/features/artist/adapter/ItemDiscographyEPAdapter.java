package com.example.spotifyclone.features.artist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;

import com.example.spotifyclone.features.artist.model.ItemDiscographyEP;
import com.example.spotifyclone.features.artist.model.PopularSong;

import java.util.List;

public class ItemDiscographyEPAdapter extends RecyclerView.Adapter<ItemDiscographyEPAdapter.ItemDiscographyViewHolder> {

    private Context context;
    private List<ItemDiscographyEP> albumList;

    private final OnSongClickListener listener;

    public interface OnSongClickListener {
        void onSongClick(ItemDiscographyEP song);
    }

    public ItemDiscographyEPAdapter(Context context, List<ItemDiscographyEP> albumList, OnSongClickListener listener) {
        this.context = context;
        this.albumList = albumList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemDiscographyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_for_discography, parent, false);
        return new ItemDiscographyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemDiscographyViewHolder holder, int position) {
        ItemDiscographyEP item = albumList.get(position);

        holder.tvAlbumTitle.setText(item.getName());
        holder.tvAlbumYear.setText(item.getYear());

        Glide.with(context)
                .load(item.getCoverUrl())
                .placeholder(R.drawable.loading)
                .into(holder.imgAlbumCover);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class ItemDiscographyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAlbumCover;
        TextView tvAlbumTitle;
        TextView tvAlbumYear;

        public ItemDiscographyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAlbumCover = itemView.findViewById(R.id.imgDiscographyCover);
            tvAlbumTitle = itemView.findViewById(R.id.tvDiscographyTitle);
            tvAlbumYear = itemView.findViewById(R.id.tvDiscographyYear);
        }
    }
}