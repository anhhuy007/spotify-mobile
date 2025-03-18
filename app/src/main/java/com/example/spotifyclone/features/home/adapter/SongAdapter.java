package com.example.spotifyclone.features.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.model.song.Song;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Song> songs;
    private SongItemType itemType;
    private OnSongClickListener listener;
    private PlaybackState playbackState = PlaybackState.STOPPED;
    private Song currentSong;

    public SongAdapter(List<Song> songs, SongItemType itemType, OnSongClickListener listener) {
        this.songs = songs;
        this.itemType = itemType;
        this.listener = listener;
    }

    public void setSongs(List<Song> newSongs) {
        this.songs.clear();
        if (newSongs != null) {
            this.songs.addAll(newSongs);
        }
        notifyDataSetChanged();
    }

    public void updateUI(PlaybackState playbackState, Song value) {
        // Update UI based on playback state and current song
        this.playbackState = playbackState;
        this.currentSong = value;
        notifyDataSetChanged();
    }

    public interface OnSongClickListener {
        void onSongClick(Song song);
        void onPlayClick(Song song);
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
        if (viewType == SongItemType.HORIZONTAL.ordinal()) {
            view = inflater.inflate(R.layout.item_horizontal_song, parent, false);
            return new HorizontalSongViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_vertical_song, parent, false);
            return new VerticalSongViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Song song = songs.get(position);

        if (holder instanceof HorizontalSongViewHolder) {
            ((HorizontalSongViewHolder) holder).bind(song, position);
        } else if (holder instanceof VerticalSongViewHolder) {
            ((VerticalSongViewHolder) holder).bind(song, position);
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class HorizontalSongViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivSongCover;
        private TextView tvSongTitle, tvArtistName;

        public HorizontalSongViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSongCover = itemView.findViewById(R.id.ivSongCover);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
        }

        public void bind(final Song song, final int position) {
            tvSongTitle.setText(song.getTitle());

            // Get artist name from singer IDs if available
            tvArtistName.setText(
                    song.getSingers() != null && !song.getSingers().isEmpty() ? song.getSingers() : "Unknown Artist"
            );

            // Load image with Picasso
            if (song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
                Picasso.get().load(song.getImageUrl()).into(ivSongCover);
            } else {
                // Set a placeholder if no image available
//                ivSongCover.setImageResource(R.drawable.placeholder_album);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSongClick(song);
                }
            });
        }
    }

    class VerticalSongViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivBackgroundImage;
        private ImageView ivSongCover;
        private TextView tvSongTitle;
        private TextView tvArtistName;
        private FloatingActionButton fabPlay;
        private FloatingActionButton fabAdd;


        public VerticalSongViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBackgroundImage = itemView.findViewById(R.id.ivBackgroundImage);
            ivSongCover = itemView.findViewById(R.id.ivSongCover);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            fabPlay = itemView.findViewById(R.id.fabPlay);
            fabAdd = itemView.findViewById(R.id.fabAdd);
        }

        public void bind(final Song song, final int position) {
            tvSongTitle.setText(song.getTitle());

            // Get artist name from singer IDs if available
            tvArtistName.setText(
                    song.getSingers() != null && !song.getSingers().isEmpty() ? song.getSingers() : "Unknown Artist"
            );


            // Load song cover image
            if (song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
                // Load as song cover
                Picasso.get().load(song.getImageUrl()).into(ivSongCover);

                // Also load as background image (blurred or darkened by overlay)
                Picasso.get().load(song.getImageUrl()).into(ivBackgroundImage);
            } else {
                 /*Set placeholders if no image available
                ivSongCover.setImageResource(R.drawable.placeholder_album);
                ivBackgroundImage.setImageResource(R.drawable.placeholder_album_bg);*/
            }

            // Update play button based on playback state
            if (playbackState == PlaybackState.PLAYING && currentSong != null && currentSong.getId().equals(song.getId())) {
                fabPlay.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                fabPlay.setImageResource(android.R.drawable.ic_media_play);
            }

            // Item click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSongClick(song);
                }
            });

            // Play button click listener
            fabPlay.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlayClick(song);
                }
            });

        }
    }
}