package com.example.spotifyclone.features.library.adapter;

import static android.provider.Settings.System.getString;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.library.model.LibraryArtist;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LibraryArtistAdapter extends RecyclerView.Adapter<LibraryArtistAdapter.ArtistViewHolder> {

    private Context context;
    private List<LibraryArtist> artistList;
    private View rootView;

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public LibraryArtistAdapter(Context context, List<LibraryArtist> artistList) {
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artist_library, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        LibraryArtist artist = artistList.get(position);

        holder.artistName.setText(artist.getName());
        holder.artistType.setText(artist.getFlCount()+" " + context.getString(R.string.fler));

        if (artist.getImageUrl() != null && !artist.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(artist.getImageUrl())
                    .placeholder(R.drawable.loading)
                    .into(holder.artistImage);
        }

        holder.itemView.setOnClickListener(v -> {
            if (rootView != null) {
                // Get the NavController from the rootView
                NavController navController = Navigation.findNavController(rootView);

                // Create the navigation action with the required argument
                Bundle args = new Bundle();
                args.putString("ARTIST_ID", artist.getId());

                // Navigate to the ArtistFragment
                navController.navigate(R.id.artistFragment, args);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artistList != null ? artistList.size() : 0;
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {
        CircleImageView artistImage;
        TextView artistName;
        TextView artistType;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            artistImage = itemView.findViewById(R.id.artistImage);
            artistName = itemView.findViewById(R.id.artistName);
            artistType = itemView.findViewById(R.id.artistType);
        }
    }
}