
package com.example.spotifyclone.features.artist.adapter;


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
import com.example.spotifyclone.features.artist.model.Item;
import com.example.spotifyclone.features.artist.model.ItemDiscographyAlbum;
import com.example.spotifyclone.features.artist.model.ItemDiscographyEP;

import java.util.List;

public class ArtistPlaylistAdapter extends RecyclerView.Adapter<ArtistPlaylistAdapter.ViewHolder> {
    private Context context;
    private List<ItemDiscographyAlbum> artistList;
    private View rootView;

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public ArtistPlaylistAdapter(Context context, List<ItemDiscographyAlbum> artistList) {
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artist_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemDiscographyAlbum item = artistList.get(position);
        holder.tv_playlist_title.setText(item.getName());
        Glide.with(context)
                .load(item.getCoverUrl())
                .placeholder(R.drawable.loading)
                .into(holder.img_playlist);

        holder.img_playlist.setOnClickListener(v -> {
            if (rootView != null) {
                // Get the current NavController from the rootView
                NavController navController = Navigation.findNavController(rootView);

                // Get the current destination ID
                int currentDestId = navController.getCurrentDestination().getId();

                // Navigate based on current location
                if (currentDestId == R.id.nav_album_detail) {
                    // Already in album detail, use self action
                    NavDirections action = AlbumDetailFragmentDirections.actionNavAlbumDetailSelf(
                            item.getId()
                    );
                    navController.navigate(action);
                } else {
                    // From another fragment, use the appropriate action to album detail
                    // Create a bundle with the album ID
                    Bundle args = new Bundle();
                    args.putString("_id", item.getId());

                    // Navigate to album detail with the ID
                    navController.navigate(R.id.nav_album_detail, args);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_playlist_title;
        ImageView img_playlist;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_playlist_title = itemView.findViewById(R.id.tv_playlist_title);
            img_playlist = itemView.findViewById(R.id.img_playlist);
        }
    }
}


