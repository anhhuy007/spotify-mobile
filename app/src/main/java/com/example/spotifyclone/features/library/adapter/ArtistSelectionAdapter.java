package com.example.spotifyclone.features.library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.follow.model.Follow;
import com.example.spotifyclone.features.library.model.SelectableArtist;
import com.example.spotifyclone.features.library.viewModel.ArtistSelectionViewModel;

import java.util.List;

public class ArtistSelectionAdapter extends RecyclerView.Adapter<ArtistSelectionAdapter.ArtistViewHolder> {

    private Context context;
    private List<SelectableArtist> artistList;
    private OnArtistClickListener listener;
    private ArtistSelectionViewModel viewModel;
    private String currentUserId;

    public interface OnArtistClickListener {
        void onArtistClick(SelectableArtist artist, int position);
    }

    public ArtistSelectionAdapter(Context context, List<SelectableArtist> artistList,
                                  OnArtistClickListener listener,
                                  ArtistSelectionViewModel viewModel,
                                  String currentUserId) {
        this.context = context;
        this.artistList = artistList;
        this.listener = listener;
        this.viewModel = viewModel;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artist_selection, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        SelectableArtist artist = artistList.get(position);

        // Set artist name
        holder.artistName.setText(artist.getName());

        // Load artist image
        if (artist.getImageUrl() != null && !artist.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(artist.getImageUrl())
                    .placeholder(R.drawable.loading)
                    .into(holder.artistImage);
        }

        // Show or hide checkmark based on follow status
        if (artist.isFollowed()) {
            holder.followedCheckmark.setVisibility(View.VISIBLE);
        } else {
            holder.followedCheckmark.setVisibility(View.GONE);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
//            if (listener != null) {
//                artist.setFollowed(!artist.isFollowed());
//                notifyItemChanged(position);
//                listener.onArtistClick(artist, position);
//            }

            if (artist.isFollowed()){
                Follow follow = new Follow();
                follow.setUserId(currentUserId);
                follow.setArtistId(artist.getId());

                viewModel.deleteFollower(follow);
            } else {
                Follow follow = new Follow();
                follow.setUserId(currentUserId);
                follow.setArtistId(artist.getId());

                viewModel.addFollower(follow);
            }

        });
    }

    @Override
    public int getItemCount() {
        return artistList != null ? artistList.size() : 0;
    }

    public void updateArtistFollowStatus(int position, boolean isFollowed) {
        if (position >= 0 && position < artistList.size()) {
            artistList.get(position).setFollowed(isFollowed);
            notifyItemChanged(position);
        }
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {
        ImageView artistImage;
        TextView artistName;
        ImageView followedCheckmark;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            artistImage = itemView.findViewById(R.id.artistImage);
            artistName = itemView.findViewById(R.id.artistName);
            followedCheckmark = itemView.findViewById(R.id.followedCheckmark);
        }
    }
}