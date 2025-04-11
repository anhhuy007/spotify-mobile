package com.example.spotifyclone.features.home.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.model.song.Song;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Album> albums;
    private AlbumItemType itemType;
    private PlaybackState playbackState = PlaybackState.STOPPED;
    private OnAlbumClickListener listener;
    private String currentAlbumId;

    public AlbumAdapter(List<Album> albums, AlbumItemType itemType, OnAlbumClickListener listener) {
        this.albums = albums;
        this.itemType = itemType;
        this.listener = listener;
    }

    public void setAlbums(List<Album> newAlbums) {
        this.albums.clear();
        if (newAlbums != null) {
            this.albums.addAll(newAlbums);
        }
        notifyDataSetChanged();
    }

    public void updateUI(PlaybackState playbackState, String albumId) {
        // Update UI based on playback state and current song
        this.playbackState = playbackState;
        this.currentAlbumId = albumId;
        notifyDataSetChanged();
    }

    public interface OnAlbumClickListener {
        void onAlbumClick(Album album);
        void onPlayClick(Album album);
    }

    @Override
    public int getItemViewType(int position) {
        return itemType.ordinal();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == AlbumItemType.HORIZONTAL.ordinal()) {
            view = inflater.inflate(R.layout.item_horizontal_album, parent, false);
            return new HorizontalAlbumViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_vertical_album, parent, false);
            return new VerticalAlbumViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Album album = albums.get(position);

        if (holder instanceof HorizontalAlbumViewHolder) {
            ((HorizontalAlbumViewHolder) holder).bind(album);
        } else if (holder instanceof VerticalAlbumViewHolder) {
            ((VerticalAlbumViewHolder) holder).bind(album);
        }
    }

    @Override
    public int getItemCount() {
        return albums != null ? albums.size() : 0;
    }

    class HorizontalAlbumViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAlbumCover;
        private TextView tvAlbumTitle, tvAlbumArtist;

        public HorizontalAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAlbumCover = itemView.findViewById(R.id.ivAlbumCover);
            tvAlbumTitle = itemView.findViewById(R.id.tvAlbumTitle);
            tvAlbumArtist = itemView.findViewById(R.id.tvAlbumArtist);
        }

        public void bind(final Album album) {
            tvAlbumTitle.setText(album.getTitle());

            // Get artist name
            if (album.getArtists_name() != null && !album.getArtists_name().isEmpty()) {
                tvAlbumArtist.setText(album.getArtistsString());
            } else {
                tvAlbumArtist.setText("Unknown Artist");
            }

            // Load image with Picasso
            if (album.getCoverUrl() != null && !album.getCoverUrl().isEmpty()) {
                Picasso.get().load(album.getCoverUrl()).into(ivAlbumCover);
            } else {
                // Set a placeholder if no image available
                // ivAlbumCover.setImageResource(R.drawable.placeholder_album);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAlbumClick(album);
                }
            });
        }
    }

    class VerticalAlbumViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivBackgroundImage;
        private ImageView ivAlbumCover;
        private TextView tvAlbumTitle;
        private TextView tvAlbumArtist;
        private FloatingActionButton fabPlay;
        private FloatingActionButton fabAdd;

        public VerticalAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBackgroundImage = itemView.findViewById(R.id.ivBackgroundImage);
            ivAlbumCover = itemView.findViewById(R.id.ivAlbumCover);
            tvAlbumTitle = itemView.findViewById(R.id.tvAlbumTitle);
            tvAlbumArtist = itemView.findViewById(R.id.tvAlbumArtist);
            fabPlay = itemView.findViewById(R.id.fabPlay);
            fabAdd = itemView.findViewById(R.id.fabAdd);
        }

        public void bind(final Album album) {
            tvAlbumTitle.setText(album.getTitle());

            // Get artist name
            if (album.getArtists_name() != null && !album.getArtists_name().isEmpty()) {
                tvAlbumArtist.setText(album.getArtistsString());
            } else {
                tvAlbumArtist.setText("Unknown Artist");
            }

            // Load album cover image
            if (album.getCoverUrl() != null && !album.getCoverUrl().isEmpty()) {
                // Load as album cover
                Picasso.get().load(album.getCoverUrl()).into(ivAlbumCover);

                // Also load as background image (blurred or darkened by overlay)
                Picasso.get().load(album.getCoverUrl()).into(ivBackgroundImage);
            } else {
                // Set placeholders if no image available
                // ivAlbumCover.setImageResource(R.drawable.placeholder_album);
                // ivBackgroundImage.setImageResource(R.drawable.placeholder_album_bg);
            }

            if (playbackState == PlaybackState.PLAYING && currentAlbumId != null && currentAlbumId.equals(album.getId())) {
                fabPlay.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                fabPlay.setImageResource(android.R.drawable.ic_media_play);
            }

            // Item click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAlbumClick(album);
                }
            });

            // Play button click listener
            fabPlay.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlayClick(album);
                }
            });
        }
    }

    public enum AlbumItemType {
        HORIZONTAL,
        VERTICAL
    }
}