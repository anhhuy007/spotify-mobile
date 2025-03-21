package com.example.spotifyclone.features.album.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Import Button
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.album.ui.AlbumBottomSheet;
import com.example.spotifyclone.features.player.model.song.Song;

import java.util.List;

public class AlbumSongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> { // Thay đổi RecyclerView.ViewHolder

    private List<Song> songs;
    private final Context context;
    private int visibleSongCount;
    private final int initialVisibleSongCount;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOAD_MORE = 1;
    private boolean isExpanded = false;
    private enum ViewType {
        ITEM,
        LOAD_MORE,
        COLLAPSE
    }



    public AlbumSongAdapter(Context context, List<Song> songs, int initialVisibleSongCount) { // Thêm initialVisibleSongCount
        this.context = context;
        this.songs = songs;
        this.initialVisibleSongCount = initialVisibleSongCount;
        this.visibleSongCount = Math.min(initialVisibleSongCount, songs.size()); // Khởi tạo visibleSongCount
    }

    public static class CollapseViewHolder extends RecyclerView.ViewHolder {
        Button buttonCollapse;

        public CollapseViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonCollapse = itemView.findViewById(R.id.buttonCollapse); // ID của nút "Thu gọn" trong collapse_layout.xml
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewType viewTypeEnum = ViewType.values()[viewType];

        switch (viewTypeEnum) {
            case ITEM:
                View view = LayoutInflater.from(context).inflate(R.layout.activity_album_song_item, parent, false);
                return new SongViewHolder(view);
            case LOAD_MORE:
                View loadMoreView = LayoutInflater.from(context).inflate(R.layout.item_load_more, parent, false);
                return new LoadMoreViewHolder(loadMoreView);
            case COLLAPSE:
                View collapseView = LayoutInflater.from(context).inflate(R.layout.item_hide, parent, false); // Tạo layout cho nút "Thu gọn"
                return new CollapseViewHolder(collapseView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SongViewHolder) {
            // Gán dữ liệu cho item bài hát
            SongViewHolder songHolder = (SongViewHolder) holder;
            Song song = songs.get(position);
            songHolder.song_name.setText(song.getTitle());
            String songs_artist = String.join(", ", song.getSingersString());
            songHolder.song_artist.setText(songs_artist);
            songHolder.more_icon.setOnClickListener(v -> {
                if (context instanceof FragmentActivity) {
                    AlbumBottomSheet bottomSheet = AlbumBottomSheet.newInstance(song);
                    bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), "BottomSheet");
                }
            });

            Glide.with(context)
                    .load(song.getImageUrl())
                    .into(songHolder.song_image);

        } else if (holder instanceof LoadMoreViewHolder) {
            // Xử lý sự kiện click cho nút "Xem thêm"
            LoadMoreViewHolder loadMoreHolder = (LoadMoreViewHolder) holder;
            loadMoreHolder.buttonLoadMore.setOnClickListener(v -> {
                showMoreSongs();
            });

        } else if (holder instanceof CollapseViewHolder) {
            // Xử lý sự kiện click cho nút "Thu gọn"
            CollapseViewHolder collapseHolder = (CollapseViewHolder) holder;
            collapseHolder.buttonCollapse.setOnClickListener(v -> {
                collapseSongs();
            });
        }
    }


    @Override
    public int getItemCount() {
        if (!isExpanded && visibleSongCount < songs.size()) {
            return visibleSongCount + 1; // Thêm 1 cho nút "Xem thêm"
        } else if (isExpanded) {
            return songs.size() + 1; // Thêm 1 cho nút "Thu gọn"
        }
        return songs.size();
    }

    @Override
    // decide display type: normal, loadmore or hide
    public int getItemViewType(int position) {
        if (!isExpanded && position >= visibleSongCount) { // Nếu chưa mở rộng và ở vị trí nút "Xem thêm"
            return ViewType.LOAD_MORE.ordinal();
        } else if (isExpanded && position == songs.size()) { // Nếu đã mở rộng và ở vị trí nút "Thu gọn"
            return ViewType.COLLAPSE.ordinal();
        }
        return ViewType.ITEM.ordinal(); // Item bài hát thông thường
    }



    public void setData(List<Song> songs) {
        this.songs = songs;
        this.visibleSongCount = Math.min(initialVisibleSongCount, songs.size());
        notifyDataSetChanged();
    }

    public void showMoreSongs() {
        if (!isExpanded) { // Chỉ mở rộng nếu chưa mở rộng
            isExpanded = true;
            visibleSongCount = songs.size(); // Hiển thị tất cả các bài hát
            notifyDataSetChanged();  // Cập nhật toàn bộ RecyclerView
        }
    }
    public void collapseSongs() {
        if (isExpanded) { // Chỉ thu gọn nếu đã mở rộng
            isExpanded = false;
            visibleSongCount = initialVisibleSongCount; // Trở lại số lượng ban đầu
            notifyDataSetChanged();  // Cập nhật toàn bộ RecyclerView
        }
    }


    public static class SongViewHolder extends RecyclerView.ViewHolder {
        // declare UI item
        TextView song_name;
        ImageView song_image;
        TextView song_artist;
        ImageButton more_icon;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.song_name);
            song_artist = itemView.findViewById(R.id.song_artist);
            song_image = itemView.findViewById(R.id.song_image);
            more_icon = itemView.findViewById(R.id.moreOptionsButton);
        }
    }
    // Load More ViewHolder
    public static class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        Button buttonLoadMore;

        public LoadMoreViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonLoadMore = itemView.findViewById(R.id.buttonLoadMore); // ID của nút "Xem thêm" trong load_more_layout.xml
        }
    }



}