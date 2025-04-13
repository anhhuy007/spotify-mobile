package com.example.spotifyclone.features.library.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.album.ui.AlbumDetailFragmentDirections;
import com.example.spotifyclone.features.library.model.LibraryPlaylist;
import com.example.spotifyclone.features.library.ui.LibraryFragmentDirections;

import java.util.List;

public class LibraryPlaylistAdapter extends RecyclerView.Adapter<LibraryPlaylistAdapter.PlaylistViewHolder> {

    private Context context;
    private List<LibraryPlaylist> playlistList;
    private View rootView;
    private String username;
    private String userImageUrl;

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public LibraryPlaylistAdapter(Context context, List<LibraryPlaylist> playlistList, String username, String userImageUrl) {
        this.context = context;
        this.playlistList = playlistList;
        this.username = username;
        this.userImageUrl = userImageUrl;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist_library, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        LibraryPlaylist playlist = playlistList.get(position);

        holder.playlistName.setText(playlist.getName());
        holder.playlistInfo.setText(context.getString(R.string.playlist) + " • " + playlist.getPlaylistInfo());

        if (playlist.getImageUrl() != null && !playlist.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(playlist.getImageUrl())
                    .placeholder(R.drawable.loading)
                    .into(holder.playlistImage);
        }

        holder.itemView.setOnClickListener(v -> {
            if (rootView != null) {
                NavController navController = Navigation.findNavController(rootView);

                // This is the correct direction based on your nav_graph.xml
                NavDirections action = LibraryFragmentDirections.actionLibToPlaylistDetailFragment(
                        username,
                        userImageUrl,
                        playlist.getId(),
                        playlist.getName(),
                        playlist.getImageUrl()
                );
                navController.navigate(action);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistList != null ? playlistList.size() : 0;
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView playlistImage;
        TextView playlistName;
        TextView playlistInfo;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistImage = itemView.findViewById(R.id.playlistImage);
            playlistName = itemView.findViewById(R.id.playlistName);
            playlistInfo = itemView.findViewById(R.id.playlistInfo);
        }
    }
}
