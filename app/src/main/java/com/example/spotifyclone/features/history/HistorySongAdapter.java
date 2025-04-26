package com.example.spotifyclone.features.history;

import android.annotation.SuppressLint;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistorySongAdapter extends RecyclerView.Adapter<HistorySongAdapter. HistorySongViewHolder> {
    private static List<HistorySong> historySongs;
    private List<Song> songs;
    private onSongClickListener onSongClickListener;

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<HistorySong> historySongs, List<Song> songs) {
        if (historySongs == null || songs == null) {
            return;
        }
        this.historySongs = historySongs;
        this.songs = songs;
        notifyDataSetChanged();
    }

    public interface onSongClickListener {
        void onSongClick(Song song);
    }

    public HistorySongAdapter(List<HistorySong> historySongs, List<Song> songs, onSongClickListener onSongClickListener) {
        this.historySongs = historySongs;
        this.songs = songs;
        this.onSongClickListener = onSongClickListener;
    }

    @NonNull
    @Override
    public HistorySongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_song, parent, false);
        return new HistorySongViewHolder(view, onSongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorySongViewHolder holder, int position) {
        Song song = songs.get(position);
        HistorySong historySong = historySongs.get(position);
        holder.itemView.setOnClickListener(v -> {
            onSongClickListener.onSongClick(song);
        });
        holder.bind(song, historySong, position);
    }

    @Override
    public int getItemCount() {
        if(songs == null) {
            return 0;
        }
        return songs.size();
    }

    public static class HistorySongViewHolder extends RecyclerView.ViewHolder {
        private final ImageView songImageView;
        private final TextView songTitleTextView, artistNameTextView, dateTextView;
        private final ImageButton moreButton;

        public HistorySongViewHolder(@NonNull View itemView, onSongClickListener listener) {
            super(itemView);
            songImageView = itemView.findViewById(R.id.song_image);
            songTitleTextView = itemView.findViewById(R.id.song_name);
            artistNameTextView = itemView.findViewById(R.id.song_artist);
            moreButton = itemView.findViewById(R.id.moreOptionsButton);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }

        public void bind(Song song, HistorySong historySong, int position) {
            songTitleTextView.setText(song.getTitle());
            artistNameTextView.setText(song.getSingersString() != null ? song.getSingersString() : "Unknown Artist");

            Picasso.get().load(song.getImageUrl()).into(songImageView);

            boolean showDate = true;
            if (position > 0) {
                long prevTimestamp = historySongs.get(position - 1).getDate();
                long currTimestamp = historySong.getDate();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                String prevDate = sdf.format(new Date(prevTimestamp));
                String currDate = sdf.format(new Date(currTimestamp));

                showDate = !prevDate.equals(currDate);
            }

            if (showDate) {
                dateTextView.setVisibility(View.VISIBLE);
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                dateTextView.setText(displayFormat.format(new Date(historySong.getDate())));
            } else {
                dateTextView.setVisibility(View.GONE);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) songImageView.getLayoutParams();
                params.setMargins(0, 0, 0, 0);
                songImageView.setLayoutParams(params);

            }
        }
    }
}

