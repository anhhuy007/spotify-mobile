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

import java.util.List;

public class ProfilePlaylistAdapter extends RecyclerView.Adapter<ProfilePlaylistAdapter.ViewHolder> {
    private final Context context;
    private final OnPlaylistClickListener listener;
    private final String user_name;
    private List<Playlist> playlists;

    public ProfilePlaylistAdapter(Context context, List<Playlist> playlists,  OnPlaylistClickListener listener, String user_name) {
        this.context = context;
        this.playlists = playlists;
        this.listener = listener;
        this.user_name=user_name;
    }

    @NonNull
    @Override
    public ProfilePlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_list, parent, false);
        return new ViewHolder(view, this);
    }
    @Override
    public void onBindViewHolder(@NonNull ProfilePlaylistAdapter.ViewHolder holder, int position) {
        holder.bind(playlists.get(position));
    }
    @Override
    public int getItemCount() {
        return (playlists != null) ? playlists.size() : 0;
    }

    public void setData(List<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView playlist_cover;
        private final TextView playlist_name, playlist_info;
        private final ProfilePlaylistAdapter adapter; // Giữ tham chiếu đến adapter


        public ViewHolder(@NonNull View itemView, ProfilePlaylistAdapter adapter) {
            super(itemView);
            this.adapter = adapter; // Lưu adapter để truy cập `selectedPlaylists`
            playlist_cover = itemView.findViewById(R.id.playlist_cover);
            playlist_name = itemView.findViewById(R.id.playlist_name);
            playlist_info = itemView.findViewById(R.id.playlist_info);
        }

        public void bind(Playlist playlist) {
            playlist_name.setText(playlist.getName());
            playlist_info.setText("0 lượt lưu" + " + "+user_name);
            Glide.with(context).load(playlist.getCoverUrl()).into(playlist_cover);
            itemView.setOnClickListener(v -> {
                if (listener != null && playlist != null) {
                    listener.OnItemClick(playlist);
                }
            });


        }
    }

}
