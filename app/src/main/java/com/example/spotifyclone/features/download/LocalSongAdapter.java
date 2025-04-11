package com.example.spotifyclone.features.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
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

import java.io.File;
import java.util.List;

public class LocalSongAdapter extends RecyclerView.Adapter<LocalSongAdapter.LocalSongViewHolder> {
    private List<Song> songs;
    private OnSongClickListener listener;

    public void setOnSongClickListener(OnSongClickListener onSongClickListener) {
        this.listener = onSongClickListener;
    }

    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    public LocalSongAdapter(List<Song> songs, OnSongClickListener listener) {
        this.songs = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LocalSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_song, parent, false);
        return new LocalSongViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalSongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.itemView.setOnClickListener(v -> {
            // Handle song click
            listener.onSongClick(song);
        });
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        if(songs == null) {
            return 0;
        }
        return songs.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    public static class LocalSongViewHolder extends RecyclerView.ViewHolder {
        private ImageView songImageView;
        private TextView songTitleTextView, artistNameTextView;
        private ImageButton moreButton;
        public LocalSongViewHolder(@NonNull View itemView, OnSongClickListener listener) {
            super(itemView);
            songImageView = itemView.findViewById(R.id.song_image);
            songTitleTextView = itemView.findViewById(R.id.song_name);
            artistNameTextView = itemView.findViewById(R.id.song_artist);
            moreButton = itemView.findViewById(R.id.moreOptionsButton);
        }
        public void bind(Song song) {
            // Bind the song data to the views
            songTitleTextView.setText(song.getTitle());
            if(song.getSingersString() != null) {
                artistNameTextView.setText(song.getSingersString());
            } else {
                artistNameTextView.setText("Unknown Artist");
            }
            Picasso.get()
                    .load(new File(song.getImageUrl()))
                    .into(songImageView);

        }
    }
}
