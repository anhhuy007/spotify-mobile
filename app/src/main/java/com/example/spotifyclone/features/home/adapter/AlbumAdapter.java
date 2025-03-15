package com.example.spotifyclone.features.home.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.album.model.Album;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private List<Album> albums;
    private OnAlbumClickListener listener;

    public AlbumAdapter(List<Album> albums, OnAlbumClickListener listener) {
        this.albums = albums;
        this.listener = listener;
    }

    public void setAlbums(List<Album> albums) {
        if (albums != null) {
            this.albums.clear();
            this.albums.addAll(albums);
            notifyDataSetChanged();
        }
    }

    public interface OnAlbumClickListener {
        void onAlbumClick(Album album);
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_horizontal_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        holder.bind(albums.get(position));
    }

    @Override
    public int getItemCount() {
        return albums != null ? albums.size() : 0;
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAlbumCover;
        private TextView tvAlbumTitle, tvAlbumArtist;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAlbumCover = itemView.findViewById(R.id.ivAlbumCover);
            tvAlbumTitle = itemView.findViewById(R.id.tvAlbumTitle);
            tvAlbumArtist = itemView.findViewById(R.id.tvAlbumArtist);
        }

        public void bind(final Album album) {
            tvAlbumTitle.setText(album.getTitle());

            if (album.getArtistIds() != null && !album.getArtistIds().isEmpty()) {
                tvAlbumArtist.setText(TextUtils.join(", ", album.getArtistIds()));
            } else {
                tvAlbumArtist.setText("Unknown Artist");
            }

            if (album.getCoverUrl() != null && !album.getCoverUrl().isEmpty()) {
                Picasso.get().load(album.getCoverUrl()).into(ivAlbumCover);
            } else {
//                ivAlbumCover.setImageResource(R.drawable.placeholder_album);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAlbumClick(album);
                }
            });
        }
    }
}