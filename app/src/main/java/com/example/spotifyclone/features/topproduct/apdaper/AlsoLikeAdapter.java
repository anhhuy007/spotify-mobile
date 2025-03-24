package com.example.spotifyclone.features.topproduct.apdaper;

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
import com.example.spotifyclone.features.topproduct.model.AlsoLike;

import java.util.List;


public class AlsoLikeAdapter extends RecyclerView.Adapter<AlsoLikeAdapter.ViewHolder> {
    private Context context;
    private List<AlsoLike> artistList;

    public AlsoLikeAdapter(Context context, List<AlsoLike> artistList) {
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public AlsoLikeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_album_item, parent, false);
        return new AlsoLikeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlsoLikeAdapter.ViewHolder holder, int position) {
        AlsoLike item = artistList.get(position);
        holder.title.setText(item.getName());
        holder.des.setText(item.getDescription());
        Glide.with(context)
                .load(item.getAvatarUrl())
                .placeholder(R.drawable.loading)
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,des;
        ImageView img;


        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.album_name);
            des = itemView.findViewById(R.id.artist_name);
            img = itemView.findViewById(R.id.album_cover);
        }
    }
}