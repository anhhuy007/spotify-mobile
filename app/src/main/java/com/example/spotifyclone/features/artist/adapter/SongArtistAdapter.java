package com.example.spotifyclone.features.artist.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.artist.model.artistDetail;
import com.example.spotifyclone.features.artist.ui.ArtistOverallUI;

import java.util.List;

public class SongArtistAdapter extends RecyclerView.Adapter<SongArtistAdapter.ViewHolder> {
    private Context context;
    private List<artistDetail> artistList;

    public SongArtistAdapter(Context context, List<artistDetail> artistList) {
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        artistDetail item = artistList.get(position);
        holder.tv_song_number.setText(Integer.toString(position+1) );
        holder.tv_song_title.setText(item.getName());
        holder.tv_song_plays.setText(item.getDescription());
        Glide.with(context)
                .load(item.getAvatarUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.img_song_cover);

    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_song_title,tv_song_plays,tv_song_number;
        ImageView img_song_cover;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_song_number = itemView.findViewById(R.id.tv_song_number);
            tv_song_title = itemView.findViewById(R.id.tv_song_title);
            tv_song_plays = itemView.findViewById(R.id.tv_song_plays);
            img_song_cover = itemView.findViewById(R.id.img_song_cover);
        }
    }
}
