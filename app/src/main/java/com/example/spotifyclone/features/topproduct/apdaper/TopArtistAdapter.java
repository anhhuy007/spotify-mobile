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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.features.topproduct.model.TopArtist;

import java.util.List;

public class TopArtistAdapter extends RecyclerView.Adapter<TopArtistAdapter.ViewHolder> {
    private Context context;
    private List<TopArtist> artistList;

    private View rootView;

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public TopArtistAdapter(Context context, List<TopArtist> artistList) {
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public TopArtistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_top_product, parent, false);
        return new TopArtistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopArtistAdapter.ViewHolder holder, int position) {
        TopArtist item = artistList.get(position);
        holder.song_index.setText(Integer.toString(position + 1));

        holder.title.setText(item.getName());
        holder.des.setText(item.getDescription());
        Glide.with(context)
                .load(item.getAvatarUrl())
                .placeholder(R.drawable.loading)
                .into(holder.img);

        holder.itemView.setOnClickListener(v -> {
            if (rootView != null) {
                // Get the NavController from the rootView
                NavController navController = Navigation.findNavController(rootView);

                // Create the navigation action with the required argument
                Bundle args = new Bundle();
                args.putString("ARTIST_ID", item.getId());

                // Navigate to the ArtistFragment
                navController.navigate(R.id.artistFragment, args);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,des,song_index;
        ImageView img;


        public ViewHolder(View itemView) {
            super(itemView);
            song_index = itemView.findViewById(R.id.song_index);

            title = itemView.findViewById(R.id.song_name);
            des = itemView.findViewById(R.id.song_artist);
            img = itemView.findViewById(R.id.song_image);
        }
    }
}