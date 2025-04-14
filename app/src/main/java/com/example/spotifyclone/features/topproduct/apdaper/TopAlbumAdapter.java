package com.example.spotifyclone.features.topproduct.apdaper;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;


import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.features.album.ui.AlbumDetailFragmentDirections;
import com.example.spotifyclone.features.topproduct.model.TopAlbum;

import java.util.List;

public class TopAlbumAdapter extends RecyclerView.Adapter<TopAlbumAdapter.ViewHolder> {
    private Context context;
    private List<TopAlbum> artistList;
    private View rootView;

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public TopAlbumAdapter(Context context, List<TopAlbum> artistList) {
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public TopAlbumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_top_product, parent, false);
        return new TopAlbumAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopAlbumAdapter.ViewHolder holder, int position) {
        TopAlbum item = artistList.get(position);
        holder.song_index.setText(Integer.toString(position + 1));
        holder.title.setText(item.getName());
        holder.des.setText(item.getDescription());
        Glide.with(context)
                .load(item.getAvatarUrl())
                .placeholder(R.drawable.loading)
                .into(holder.img);
        holder.itemView.setOnClickListener(v -> {
            if (rootView != null) {
                NavController navController = Navigation.findNavController(rootView);
                int currentDestId = navController.getCurrentDestination().getId();

                if (currentDestId == R.id.nav_album_detail) {
                    NavDirections action = AlbumDetailFragmentDirections.actionNavAlbumDetailSelf(
                            item.getId()
                    );
                    navController.navigate(action);
                } else {
                    Bundle args = new Bundle();
                    args.putString("_id", item.getId());

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
        TextView title,des, song_index;
        ImageView img;


        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.song_name);
            song_index = itemView.findViewById(R.id.song_index);
            des = itemView.findViewById(R.id.song_artist);
            img = itemView.findViewById(R.id.song_image);
        }
    }
}