package com.example.spotifyclone.features.artist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.artist.model.FansAlsoLike;
import com.example.spotifyclone.features.artist.model.Item;
import com.example.spotifyclone.features.artist.ui.ArtistFragment;

import java.util.List;

public class ArtistSimilarAdapter extends RecyclerView.Adapter<ArtistSimilarAdapter.ViewHolder> {
    private Context context;
    private FragmentActivity FragActi;
    private List<FansAlsoLike> artistList;
    private View rootView;



    public ArtistSimilarAdapter(Context context,FragmentActivity FragActi, List<FansAlsoLike> artistList) {
        this.context = context;
        this.FragActi = FragActi;
        this.artistList = artistList;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_similar_artist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FansAlsoLike item = artistList.get(position);
        holder.tv_playlist_title.setText(item.getName());
        Glide.with(context)
                .load(item.getAvatarUrl())
                .placeholder(R.drawable.loading)
                .into(holder.artist_logo);

        holder.artist_logo.setOnClickListener(v -> {
            if (FragActi != null) {
                FragActi.getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup) rootView.getParent()).getId(), ArtistFragment.newInstance(item.getId()))
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_playlist_title;
        ImageView artist_logo;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_playlist_title = itemView.findViewById(R.id.tv_playlist_title);
            artist_logo = itemView.findViewById(R.id.artist_logo);
        }
    }
}
