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
import com.example.spotifyclone.features.artist.model.ItemDiscographyAlbum;

import java.util.List;

public class ItemDiscographyAlbumAdapter extends RecyclerView.Adapter<ItemDiscographyAlbumAdapter.ItemDiscographyViewHolder> {

    private Context context;
    private List<ItemDiscographyAlbum> albumList;
    private View rootView;

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public ItemDiscographyAlbumAdapter(Context context, List<ItemDiscographyAlbum> albumList) {
        this.context = context;
        this.albumList = albumList;
    }

    @NonNull
    @Override
    public ItemDiscographyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_for_discography, parent, false);
        return new ItemDiscographyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemDiscographyViewHolder holder, int position) {
        ItemDiscographyAlbum item = albumList.get(position);

        holder.tvAlbumTitle.setText(item.getName());
        holder.tvAlbumYear.setText(item.getYear());

        Glide.with(context)
                .load(item.getCoverUrl())
                .placeholder(R.drawable.loading)
                .into(holder.imgAlbumCover);

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
        return albumList.size();
    }

    public static class ItemDiscographyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAlbumCover;
        TextView tvAlbumTitle;
        TextView tvAlbumYear;

        public ItemDiscographyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAlbumCover = itemView.findViewById(R.id.imgDiscographyCover);
            tvAlbumTitle = itemView.findViewById(R.id.tvDiscographyTitle);
            tvAlbumYear = itemView.findViewById(R.id.tvDiscographyYear);
        }
    }
}