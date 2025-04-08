package com.example.spotifyclone.features.playlist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.playlist.inter.OnSongClickListner;

import java.util.List;

public class PlaylistSongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Song> songs;
    private final Context context;
    private final OnSongClickListner listener;
    public static final int ADD_TYPE = 0;
    public static final int EDIT_TYPE = 1;
    private final int type;


    public PlaylistSongAdapter(Context context, List<Song> songs, OnSongClickListner listener, int type) {
        this.context = context;
        this.songs = songs;
        this.listener = listener;
        this.type=type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ADD_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_detail_add_song, parent, false);
            return new AddSongViewHolder(view, listener);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_detail_edit_song, parent, false);
            return new EditSongViewHolder(view, listener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return type;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Song song=songs.get(position);
        if(holder instanceof AddSongViewHolder)
        {
            ((AddSongViewHolder) holder).bind(song,context);
        }
        else if (holder instanceof EditSongViewHolder) {
            ((EditSongViewHolder) holder).bind(song, context);
        }

    }

    @Override
    public int getItemCount() {
        if(songs==null)
        {
            return 0;
        }
        return songs.size();
    }

    public void setData(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    // ViewHolder cho ADD_TYPE
    public static class AddSongViewHolder extends RecyclerView.ViewHolder {
        private final ImageView song_image;
        private final TextView song_artist;
        private final TextView song_name;
        private final ImageButton add_button;

        public AddSongViewHolder(@NonNull View itemView, OnSongClickListner listener) {
            super(itemView);
            song_image = itemView.findViewById(R.id.song_image);
            song_artist = itemView.findViewById(R.id.song_artist);
            song_name = itemView.findViewById(R.id.song_name);
            add_button = itemView.findViewById(R.id.add_button);

            add_button.setOnClickListener(v -> {
                Song song = (Song) itemView.getTag();
                if (listener != null && song != null) {
                    listener.OnAddClickSong(song);
                }
            });
            itemView.setOnClickListener(v -> {
                Song song = (Song) itemView.getTag();
                if (listener != null && song != null) {
                    listener.OnPlaySong(song);
                }
            });

        }

        public void bind(Song song, Context context) {
            song_name.setText(song.getTitle());
            song_artist.setText(song.getAuthorNames() != null ? String.join(", ", song.getAuthorNames()) : "Unknown Artist");

            Glide.with(context)
                    .load(song.getImageUrl())
                    .apply(new RequestOptions().transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners(20))))
                    .into(song_image);

            itemView.setTag(song);
        }
    }

    // ViewHolder cho EDIT_TYPE
    public static class EditSongViewHolder extends RecyclerView.ViewHolder {
        private final ImageView remove_song_button;
        private final TextView song_artist;
        private final TextView song_name;
//        private final ImageButton reorder_button;

        public EditSongViewHolder(@NonNull View itemView, OnSongClickListner listener) {
            super(itemView);
            remove_song_button = itemView.findViewById(R.id.remove_song_button);
            song_artist = itemView.findViewById(R.id.song_artist);
            song_name = itemView.findViewById(R.id.song_name);
//            reorder_button = itemView.findViewById(R.id.reorder_button);

            remove_song_button.setOnClickListener(v -> {
                Song song = (Song) itemView.getTag();
                if (listener != null && song != null) {
                    listener.OnRemoveClickSong(song);
                }
            });

        }

        public void bind(Song song, Context context) {
            song_name.setText(song.getTitle());
            song_artist.setText(song.getAuthorNames() != null ? String.join(", ", song.getAuthorNames()) : "Unknown Artist");
            itemView.setTag(song);
        }
    }
}