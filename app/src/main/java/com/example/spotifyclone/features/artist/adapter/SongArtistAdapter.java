package com.example.spotifyclone.features.artist.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.artist.model.PopularSong;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class SongArtistAdapter extends RecyclerView.Adapter<SongArtistAdapter.ViewHolder> {
    private final Context context;
    private final List<PopularSong> artistList;
    private final OnSongClickListener listener;

    public interface OnSongClickListener {
        void onSongClick(PopularSong song);
    }

    public SongArtistAdapter(Context context, List<PopularSong> artistList, OnSongClickListener listener) {
        this.context = context;
        this.artistList = artistList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PopularSong item = artistList.get(position);
        holder.tv_song_number.setText(Integer.toString(position + 1));
        holder.tv_song_title.setText(item.getName());
        holder.tv_song_plays.setText(item.getDescription());
        Glide.with(context)
                .load(item.getAvatarUrl())
                .placeholder(R.drawable.loading)
                .into(holder.img_song_cover);


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(item);
            }
        });

        holder.btn_more_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                View view1 = LayoutInflater.from(context).inflate(R.layout.dialog_song_options, null);
                bottomSheetDialog.setContentView(view1);
                TextView songTitle = view1.findViewById(R.id.name_dialog_song_options);
                TextView songArtist = view1.findViewById(R.id.artist_dialog_song_options);
                ImageView songThumbnail = view1.findViewById(R.id.image_dialog_song_options);

                songTitle.setText(item.getName());
                songArtist.setText(item.getDescription());
                Glide.with(context)
                        .load(item.getAvatarUrl())
                        .placeholder(R.drawable.loading)
                        .into(songThumbnail);
                bottomSheetDialog.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_song_title, tv_song_plays, tv_song_number;
        ImageView img_song_cover;

        ImageButton btn_more_options;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_song_number = itemView.findViewById(R.id.tv_song_number);
            tv_song_title = itemView.findViewById(R.id.tv_song_title);
            tv_song_plays = itemView.findViewById(R.id.tv_song_plays);
            img_song_cover = itemView.findViewById(R.id.img_song_cover);
            btn_more_options = itemView.findViewById(R.id.btn_more_options);
        }
    }
}
