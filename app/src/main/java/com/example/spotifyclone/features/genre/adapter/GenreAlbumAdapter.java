package com.example.spotifyclone.features.genre.adapter;

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
import com.example.spotifyclone.features.genre.model.Album;

import java.util.List;

public class GenreAlbumAdapter extends RecyclerView.Adapter<GenreAlbumAdapter.AlbumViewHolder> {

    private List<Album> albums;
    private final Context context;


    public GenreAlbumAdapter(Context context, List<Album> albums){
        this.context=context;
        this.albums=albums;
    }
    @NonNull
    @Override
    public GenreAlbumAdapter.AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_genre_album, parent , false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {//gan data cho UI
        Album album= albums.get(position);
        holder.album_name.setText(album.getTitle());
        String songs_artist = album.getArtists_name() != null ? String.join(", ", album.getArtists_name()) : "";
        holder.album_artist.setText(songs_artist);
        Glide.with(context)
                .load(album.getCoverUrl())
                .into(holder.album_image);
    }


    @Override
    public int getItemCount() {
        if(albums!=null)
        {
            return albums.size();
        }
        return 0;
    }
    public void setData(List<Album> albums) {
        this.albums = albums;
        notifyDataSetChanged();
    }
    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        // declare UI item
        TextView album_name;
        ImageView album_image;
        TextView album_artist;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            album_name=itemView.findViewById(R.id.album_name);
            album_artist=itemView.findViewById(R.id.artist_name);
            album_image=itemView.findViewById(R.id.album_cover);
        }
    }

}


