package com.example.spotifyclone.features.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.artist.model.Artist;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
    private List<Artist> artists;
    private OnArtistClickListener listener;

    public ArtistAdapter(List<Artist> artists, OnArtistClickListener listener) {
        this.artists = artists;
        this.listener = listener;
    }

    public void setArtists(List<Artist> artists) {
        if (artists != null) {
            this.artists.clear();
            this.artists.addAll(artists);
            notifyDataSetChanged();
        }
    }

    public interface OnArtistClickListener {
        void onArtistClick(Artist artist);
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_home_artist, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        holder.bind(artists.get(position));
    }

    @Override
    public int getItemCount() {
        return artists != null ? artists.size() : 0;
    }

    class ArtistViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivArtistAvatar;
        private TextView tvArtistName;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            ivArtistAvatar = itemView.findViewById(R.id.ivArtistAvatar);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
        }

        public void bind(final Artist artist) {
            // Set artist name
            tvArtistName.setText(artist.getName());

            // Load artist avatar image with Picasso
            if (artist.getAvatarUrl() != null && !artist.getAvatarUrl().isEmpty()) {
                Picasso.get().load(artist.getAvatarUrl()).into(ivArtistAvatar);
            } else {
                // You can uncomment and set a placeholder image
                // ivArtistAvatar.setImageResource(R.drawable.placeholder_artist);
            }

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onArtistClick(artist);
                }
            });
        }
    }
}