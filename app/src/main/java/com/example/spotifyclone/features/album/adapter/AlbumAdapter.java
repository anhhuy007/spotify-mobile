package com.example.spotifyclone.features.album.adapter;

import android.content.Context;
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
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.album.inter.OnAlbumItemClickListener;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    private List<Album> albums;
    private final Context context;
    private final OnAlbumItemClickListener listener;
    private int layoutWidth, layoutHeight;

    public AlbumAdapter(Context context, List<Album> albums, OnAlbumItemClickListener listener, int layoutWidth, int layoutHeight) {
        this.context = context;
        this.albums = albums;
        this.listener = listener;
        this.layoutWidth = layoutWidth;
        this.layoutHeight = layoutHeight;
    }

    @NonNull
    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_album_item, parent, false);

        // Update size of itemView
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(layoutWidth, layoutHeight);
        } else {
            layoutParams.width = layoutWidth;
            layoutParams.height = layoutHeight;
        }
        view.setLayoutParams(layoutParams);
        return new AlbumAdapter.ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.ViewHolder holder, int position) {
        holder.bind(albums.get(position), context);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public void setData(List<Album> albums) {
        this.albums = albums;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView album_cover;
        private final TextView album_artist;
        private final TextView album_name;

        public ViewHolder(@NonNull View itemView, OnAlbumItemClickListener listener) {
            super(itemView);
            album_cover = itemView.findViewById(R.id.album_cover);
            album_artist = itemView.findViewById(R.id.artist_name);
            album_name=itemView.findViewById(R.id.album_name);

            itemView.setOnClickListener(v -> {
                Album album = (Album) v.getTag();
                if (listener != null && album != null) {
                    listener.OnItemClick(album);
                }
            });
        }

        public void bind(Album album, Context context) {
            album_name.setText(album.getTitle());
            album_artist.setText(
                    album.getArtists_name() != null ? String.join(", ", album.getArtists_name()) : "Unknown Artist"
            );

            int radius = 20;
            Glide.with(context)
                    .load(album.getCoverUrl())
                    .apply(new RequestOptions().transform(
                            new MultiTransformation<>(new CenterCrop(), new RoundedCorners(radius))
                    ))
                    .into(album_cover);

            itemView.setTag(album);
        }
    }}
