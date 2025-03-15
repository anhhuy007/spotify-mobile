package com.example.spotifyclone.features.player.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.player.model.song.Song;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UpcomingSongAdapter extends RecyclerView.Adapter<UpcomingSongAdapter.UpcomingSongViewHolder> {
    private List<Song> songs;
    private final OnSongClickListener onSongClickListener;

    public interface OnSongClickListener {
        void onSongClicked(Song song, int position);
    }

    public UpcomingSongAdapter(List<Song> songs, OnSongClickListener listener) {
        this.songs = songs != null ? new ArrayList<>(songs) : new ArrayList<>();
        this.onSongClickListener = listener;
    }

    @NonNull
    @Override
    public UpcomingSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_upcoming_song, parent, false);
        return new UpcomingSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingSongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void setSongs(List<Song> newSongs) {
        this.songs.clear();
        if (newSongs != null) {
            this.songs.addAll(newSongs);
        }
        notifyDataSetChanged();
    }

    class UpcomingSongViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgSong;
        private final TextView tvSongTitle;
        private final TextView tvArtist;
        private final ImageButton btnOptions;

        public UpcomingSongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.imgSong);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            btnOptions = itemView.findViewById(R.id.btnOptions);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onSongClickListener != null) {
                    onSongClickListener.onSongClicked(songs.get(position), position);
                }
            });
        }

        public void bind(Song song) {
            if (song != null) {
                tvSongTitle.setText(song.getTitle());

                String artistName = "Unknown Artist";
                if (song.getSingers() != null && song.getSingers().size() > 0) {
//                    artistName = song.getAuthor_ids()[0];
                }
                tvArtist.setText(artistName);

                if (song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
                    Picasso.get()
                            .load(song.getImageUrl())
                            .into(imgSong);
                } else {
//                    imgSong.setImageResource(R.drawable.placeholder_album);
                }
            }
        }
    }
}