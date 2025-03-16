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
import com.example.spotifyclone.features.artist.model.Item;
import com.example.spotifyclone.features.artist.model.ItemDiscographyEP;

import java.util.List;

public class AlbumArtistAdapter extends RecyclerView.Adapter<AlbumArtistAdapter.ViewHolder> {
    private Context context;
    private List<ItemDiscographyEP> artistList;

    public AlbumArtistAdapter(Context context, List<ItemDiscographyEP> artistList) {
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemDiscographyEP item = artistList.get(position);
        holder.tv_album_title.setText(item.getName());
        holder.tv_album_info.setText(item.getYear()+"•" + context.getString(R.string.single));
        Glide.with(context)
                .load(item.getCoverUrl())
                .placeholder(R.drawable.loading)
                .into(holder.img_album_cover);

    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_album_title,tv_album_info;
        ImageView img_album_cover;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_album_info = itemView.findViewById(R.id.tv_album_info);
            tv_album_title = itemView.findViewById(R.id.tv_album_title);
            img_album_cover = itemView.findViewById(R.id.img_album_cover);
        }
    }
}
