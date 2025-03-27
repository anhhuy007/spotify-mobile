package com.example.spotifyclone.features.playlist.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.playlist.inter.OnPlaylistClickListener;
import com.example.spotifyclone.features.playlist.model.Playlist;
import com.example.spotifyclone.features.playlist.ui.CircleCheckBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private final Context context;
    private final OnPlaylistClickListener listener;
    private List<Playlist> playlists;
    private final HashSet<String> selectedPlaylists = new HashSet<>(); // Lưu danh sách đã check

    public PlaylistAdapter(Context context, List<Playlist> playlists, OnPlaylistClickListener listener) {
        this.context = context;
        this.playlists = playlists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new ViewHolder(view, this); // Truyền adapter vào ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
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



    public HashSet<String> getSelectedPlaylists() {
        return selectedPlaylists;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView playlist_cover;
        private final TextView playlist_name, number_songs;
        private final CircleCheckBox checkBox;
        private final PlaylistAdapter adapter; // Giữ tham chiếu đến adapter

        public ViewHolder(@NonNull View itemView, PlaylistAdapter adapter) {
            super(itemView);
            this.adapter = adapter; // Lưu adapter để truy cập `selectedPlaylists`

            playlist_cover = itemView.findViewById(R.id.playlist_cover);
            playlist_name = itemView.findViewById(R.id.playlist_name);
            number_songs = itemView.findViewById(R.id.number_songs);
            checkBox = itemView.findViewById(R.id.circleCheckBox);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Playlist playlist = (Playlist) itemView.getTag();
                if (playlist != null) {
                    if (isChecked) {
                        adapter.selectedPlaylists.add(playlist.getId()); // Thêm vào danh sách
                    } else {
                        adapter.selectedPlaylists.remove(playlist.getId()); // Xóa khỏi danh sách
                    }
                }
            });

            itemView.setOnClickListener(v -> {
                Playlist playlist = (Playlist) v.getTag();
                if (listener != null && playlist != null) {
                    listener.OnItemClick(playlist);
                }
            });

        }

        public void bind(Playlist playlist) {
            playlist_name.setText(playlist.getName());
            number_songs.setText(playlist.getSongIds().size() + " Bài hát");
            Glide.with(context).load(playlist.getCoverUrl()).into(playlist_cover);
            itemView.setTag(playlist);

            // Đặt trạng thái checkbox đúng khi cuộn RecyclerView
            checkBox.setChecked(adapter.selectedPlaylists.contains(playlist.getId()));

        }
    }
}

