package com.example.spotifyclone.album_ids.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.spotifyclone.R;
import com.example.spotifyclone.album_ids.model.Album;
import com.example.spotifyclone.album_ids.model.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Song> songs;
    private final Context context;



    public SongAdapter(Context context, List<Song> songs){
        this.context=context;
        this.songs=songs;
    }
    @NonNull
    @Override
    public SongAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.album_song_item, parent , false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {//gan data cho UI
        Song song= songs.get(position);
        holder.song_name.setText(song.getTitle());
        String songs_artist = String.join(", ", song.getSinger_ids());
        holder.song_artist.setText(songs_artist);
        holder.more_icon.setOnClickListener(v -> {
            if (context instanceof FragmentActivity) {
                AlbumBottomSheet bottomSheet = AlbumBottomSheet.newInstance(song);
                bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), "BottomSheet");
            }
        });

        Glide.with(context)
                .load(song.getImg_url())
                .into(holder.song_image);
    }


    @Override
    public int getItemCount() {
        if(songs!=null)
        {
            return songs.size();
        }
        return 0;
    }
    public void setData(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }
    public static class SongViewHolder extends RecyclerView.ViewHolder {
        // declare UI item
        TextView song_name;
        ImageView song_image;
        TextView song_artist;
        ImageButton more_icon;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            song_name=itemView.findViewById(R.id.song_name);
            song_artist=itemView.findViewById(R.id.song_artist);
            song_image=itemView.findViewById(R.id.song_image);
            more_icon=itemView.findViewById(R.id.moreOptionsButton);
        }
    }

}


