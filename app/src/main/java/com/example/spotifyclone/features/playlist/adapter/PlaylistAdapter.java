package com.example.spotifyclone.features.playlist.adapter;

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
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.playlist.inter.OnPlaylistClickListener;
import com.example.spotifyclone.features.playlist.model.Playlist;

import java.util.ArrayList;
import java.util.List;


public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private Context context;
    private OnPlaylistClickListener listener;
    private List<Playlist>playlists;

    public PlaylistAdapter(Context context, List<Playlist> playlists, OnPlaylistClickListener listener){
        this.context = context;
        this.playlists = playlists;
        this.listener = listener;
    }
    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist, parent, false);
        return new PlaylistAdapter.ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        holder.bind(playlists.get(position), context);
    }

    @Override
    public int getItemCount() {
        if(playlists==null){

        }
        return playlists.size();
    }

    public void setData(List<Playlist> playlists) {
//        if(playlists.isEmpty()){
//            this.playlists=new ArrayList<>();
//        }
//        else{
//            this.playlists = playlists;
//        }
        this.playlists = playlists;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView playlist_cover;
        private final TextView playlist_name;
        private final TextView number_songs;

        public ViewHolder(@NonNull View itemView, OnPlaylistClickListener listener) {
            super(itemView);
            playlist_cover = itemView.findViewById(R.id.playlist_cover);
            playlist_name = itemView.findViewById(R.id.playlist_name);
            number_songs=itemView.findViewById(R.id.number_songs);

            itemView.setOnClickListener(v -> {
                Playlist playlist = (Playlist) v.getTag();
                if (listener != null && playlist != null) {
                    listener.OnItemClick(playlist);
                }
            });
        }

        public void bind(Playlist playlist, Context context) {
            playlist_name.setText(playlist.getName());
            number_songs.setText(playlist.getSongIds().size());
            int radius = 20;
//            Glide.with(context)
//                    .load(playlist.getCoverUrl())
//                    .into(playlist_cover);
//
            itemView.setTag(playlist);
        }
    }

}




