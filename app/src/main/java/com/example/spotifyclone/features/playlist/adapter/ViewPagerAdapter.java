package com.example.spotifyclone.features.playlist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.album.adapter.AlbumSongAdapter;
import com.example.spotifyclone.features.album.inter.OnSongMoreIconClickListener;
import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.genre.adapter.GenreAdapter;
import com.example.spotifyclone.features.genre.model.Genre;
import com.example.spotifyclone.features.home.adapter.SongAdapter;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.search.inter.OnClassifyItemClickListener;

import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private final List<List<Song>> songLists;

    private final OnSongMoreIconClickListener listener;
    private final Context context;
    private int page; //for text view

    public ViewPagerAdapter(List<List<Song>> songLists, int page, OnSongMoreIconClickListener listener, Context context) {
        this.songLists = songLists;
        this.page = page;
        this.listener=listener;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bottom_sheet_playlist_detail_add_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerAdapter.ViewHolder holder, int position) {
        List<Song> currentList = songLists.get(position);

        // Ví dụ set TextView title cho mỗi page
        holder.title.setText("hehe");

        // Gắn RecyclerView bên trong mỗi page
        AlbumSongAdapter adapter = new AlbumSongAdapter(context, currentList, 3, listener ); // bạn tự tạo adapter cho bài hát
        holder.recyclerView.setAdapter(adapter);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }


    @Override
    public int getItemCount() {
        return 2; //contain 2 pages
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }
    
}
