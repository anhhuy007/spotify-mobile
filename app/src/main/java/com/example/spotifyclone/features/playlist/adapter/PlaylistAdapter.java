//package com.example.spotifyclone.features.playlist.adapter;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.spotifyclone.R;
//import com.example.spotifyclone.features.playlist.inter.OnPlaylistClickListener;
//import com.example.spotifyclone.features.playlist.model.Playlist;
//import com.example.spotifyclone.features.playlist.ui.CircleCheckBox;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//
//
//public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
//
//    private final Context context;
//    private final OnPlaylistClickListener listener;
//    private List<Playlist> playlists;
//    private final HashSet<String> selectedPlaylists = new HashSet<>(); // Lưu danh sách đã check
//    private final HashSet<String> removeSongPlaylists = new HashSet<>(); // Lưu danh sách đã check>
//    private String currentSongId;
//
//    public PlaylistAdapter(Context context, List<Playlist> playlists, OnPlaylistClickListener listener, String currentSongId) {
//        this.context = context;
//        this.playlists = playlists;
//        this.listener = listener;
//        this.currentSongId=currentSongId;
//    }
//
//    @NonNull
//    @Override
//    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
//        return new ViewHolder(view, this); // Truyền adapter vào ViewHolder
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
//        holder.bind(playlists.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return (playlists != null) ? playlists.size() : 0;
//    }
//
//    public void setData(List<Playlist> playlists) {
//        this.playlists = playlists;
//        notifyDataSetChanged();
//    }
//
//
//
//    public HashSet<String> getSelectedPlaylists() {
//        return selectedPlaylists;
//    }
//    public HashSet<String> getRemoveSongPlaylists(){return removeSongPlaylists;}
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        private final ImageView playlist_cover;
//        private final TextView playlist_name, number_songs;
//        private final CircleCheckBox checkBox;
//        private final PlaylistAdapter adapter; // Giữ tham chiếu đến adapter
//
//
//        public ViewHolder(@NonNull View itemView, PlaylistAdapter adapter) {
//            super(itemView);
//            this.adapter = adapter; // Lưu adapter để truy cập `selectedPlaylists`
//
//            playlist_cover = itemView.findViewById(R.id.playlist_cover);
//            playlist_name = itemView.findViewById(R.id.playlist_name);
//            number_songs = itemView.findViewById(R.id.number_songs);
//            checkBox = itemView.findViewById(R.id.circleCheckBox);
//
//
//
//        }
//
//        public void bind(Playlist playlist) {
//            playlist_name.setText(playlist.getName());
//            number_songs.setText(playlist.getSongIds().size() + " Bài hát");
//            Glide.with(context).load(playlist.getCoverUrl()).into(playlist_cover);
//            itemView.setTag(playlist);
//
//            // Kiểm tra nếu bài hát đã có trong playlist
//            boolean isSongInPlaylist = playlist.getSongIds().contains(currentSongId);
//
//            // Đặt trạng thái checkbox dựa vào danh sách bài hát trong playlist
//            checkBox.setOnCheckedChangeListener(null); // Ngăn chặn vòng lặp sự kiện khi cập nhật UI
//            checkBox.setChecked(isSongInPlaylist&&!removeSongPlaylists.contains(playlist.getId())); // contain song id and user not recheck
//
//
//            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
////                Playlist playlist = (Playlist) itemView.getTag();
//                if (playlist != null) {
//                    if (isChecked) {
//                        if(!isSongInPlaylist)
//                        {
//                            adapter.selectedPlaylists.add(playlist.getId()); // Thêm vào danh sách
//                        }
//                    } else {
//                        if(isSongInPlaylist){
//                            removeSongPlaylists.add(playlist.getId());
//                        }
//                        else{
//                            adapter.selectedPlaylists.remove(playlist.getId()); // Xóa khỏi danh sách
//                        }
//                    }
//                }
//            });
//
//            itemView.setOnClickListener(v -> {
////                Playlist playlist = (Playlist) v.getTag();
//                if (listener != null && playlist != null) {
//                    Log.d("PlaylistAdapter", "Playlist ID: " + playlist.getId());
//                    listener.OnItemClick(playlist);
//                }
//            });
//
//
//        }
//    }
//}