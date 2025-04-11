package com.example.spotifyclone.features.playlist.adapter;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.playlist.inter.OnSongClickListner;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModel;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModelFactory;

import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private final List<List<Song>> songLists;
    private final OnSongClickListner listener;
    private final Context context;
    private final int page; // Có thể dùng để hiển thị tiêu đề khác nhau theo page
    private PlaylistSongAdapter adapter;

    public ViewPagerAdapter(List<List<Song>> songLists, int page, Context context, OnSongClickListner listener) {
        this.songLists = songLists;
        this.page = page;
        this.context = context;
        this.listener = listener;
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

        // Thiết lập tiêu đề tùy theo page
        if (position == 0) {
            holder.title.setText("Bài hát mới");


        } else if (position == 1) {
            holder.title.setText("Mới phát gần đây");
        }

        // Set up RecyclerView cho mỗi trang
//        PlaylistSongAdapter adapter = new PlaylistSongAdapter(context, currentList, listener, 0);

        // set up vỉewmodel

        adapter = new PlaylistSongAdapter(context, currentList, listener, PlaylistSongAdapter.ADD_TYPE);

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return songLists.size(); // Đảm bảo linh hoạt nếu số trang thay đổi
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
