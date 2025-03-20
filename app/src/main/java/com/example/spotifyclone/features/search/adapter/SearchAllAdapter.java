package com.example.spotifyclone.features.search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.search.inter.OnSearchItemClickListener;
import com.example.spotifyclone.features.search.model.SearchItem;

import java.util.ArrayList;
import java.util.List;

public class SearchAllAdapter extends RecyclerView.Adapter<SearchAllAdapter.BaseViewHolder> {
    private List<SearchItem> items;
    private final Context context;
    private final OnSearchItemClickListener listener;

    private static final int TYPE_SONG = 0;
    private static final int TYPE_ALBUM = 1;
    private static final int TYPE_ARTIST = 2;
    private static final int TYPE_GENRE = 3;

    public SearchAllAdapter(Context context, List<SearchItem> items,  OnSearchItemClickListener listener) {
        this.items = items;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        SearchItem item = items.get(position);
        switch (item.getType()) {
            case "song": return TYPE_SONG;
            case "album": return TYPE_ALBUM;
            case "artist": return TYPE_ARTIST;
            default: return TYPE_GENRE;
        }
    }

    public void setData(List<SearchItem> items) {
        if (items == null) {
            // Handle null data
            this.items = new ArrayList<>();
        } else {
            this.items = items;
        }


        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public SearchAllAdapter.BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == TYPE_SONG) {
            view = inflater.inflate(R.layout.item_searchall_song, parent, false);
            return new SearchAllAdapter.SongViewHolder(view, listener);
        } else if (viewType == TYPE_ALBUM) {
            view = inflater.inflate(R.layout.item_searchall_album, parent, false);
            return new SearchAllAdapter.AlbumViewHolder(view, listener);
        } else if (viewType == TYPE_ARTIST) {
            view = inflater.inflate(R.layout.item_searchall_artist, parent, false);
            return new SearchAllAdapter.ArtistViewHolder(view, listener);
        } else {
            view = inflater.inflate(R.layout.item_searchall_genre, parent, false);
            return new SearchAllAdapter.GenreViewHolder(view, listener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAllAdapter.BaseViewHolder holder, int position) {
        holder.bind(items.get(position), context);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ======= Tạo lớp cha cho ViewHolder =======
    public static abstract class BaseViewHolder extends RecyclerView.ViewHolder {
        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void bind(SearchItem item, Context context);
    }

    // ======= Các ViewHolder con kế thừa BaseViewHolder =======
    public static class SongViewHolder extends SearchAllAdapter.BaseViewHolder {
        private final ImageView search;
        private final TextView search_name;
        private final TextView search_type;

        public SongViewHolder(@NonNull View itemView, OnSearchItemClickListener listener) {
            super(itemView);
            search = itemView.findViewById(R.id.search);
            search_name = itemView.findViewById(R.id.search_name);
            search_type = itemView.findViewById(R.id.search_type);
            itemView.setOnClickListener(v -> {
                SearchItem item = (SearchItem) v.getTag();
                if (listener != null && item != null) {
                    listener.OnItemClick(item);
                }
            });
        }

        @Override
        public void bind(SearchItem item, Context context) {
            search_name.setText(item.getName());
            if (item.getArtists_name() != null) {
                search_type.setText(item.getType() + " + " + String.join(", ", item.getArtists_name()));
            }
            Glide.with(context).load(item.getImage_url()).into(search);
            itemView.setTag(item);
        }
    }

    public static class AlbumViewHolder extends SearchAllAdapter.BaseViewHolder {
        private final ImageView search;
        private final TextView search_name;
        private final TextView search_type;

        public AlbumViewHolder(@NonNull View itemView, OnSearchItemClickListener listener) {
            super(itemView);
            search = itemView.findViewById(R.id.search);
            search_name = itemView.findViewById(R.id.search_name);
            search_type = itemView.findViewById(R.id.search_type);
            itemView.setOnClickListener(v -> {
                SearchItem item = (SearchItem) v.getTag();
                if (listener != null && item != null) {
                    listener.OnItemClick(item);
                }
            });
        }

        @Override
        public void bind(SearchItem item, Context context) {
            search_name.setText(item.getName());
            if (item.getArtists_name() != null) {
                search_type.setText(item.getType() + " + " + String.join(", ", item.getArtists_name()));
            }
            Glide.with(context).load(item.getImage_url()).into(search);
            itemView.setTag(item);
        }
    }

    public static class ArtistViewHolder extends SearchAllAdapter.BaseViewHolder {
        private final ImageView search;
        private final TextView search_name;
        private final TextView search_type;

        public ArtistViewHolder(@NonNull View itemView, OnSearchItemClickListener listener) {
            super(itemView);
            search = itemView.findViewById(R.id.search);
            search_name = itemView.findViewById(R.id.search_name);
            search_type = itemView.findViewById(R.id.search_type);
            itemView.setOnClickListener(v -> {
                SearchItem item = (SearchItem) v.getTag();
                if (listener != null && item != null) {
                    listener.OnItemClick(item);
                }
            });
        }

        @Override
        public void bind(SearchItem item, Context context) {
            search_name.setText(item.getName());
            if (item.getArtists_name() != null) {
                search_type.setText(item.getType() + " + " + String.join(", ", item.getArtists_name()));
            }
            Glide.with(context).load(item.getImage_url()).apply(new RequestOptions().transform(new CircleCrop())).into(search);
            itemView.setTag(item);
        }
    }

    public static class GenreViewHolder extends SearchAllAdapter.BaseViewHolder {
        private final ImageView search;
        private final TextView search_name;
        private final TextView search_type;

        public GenreViewHolder(@NonNull View itemView, OnSearchItemClickListener listener) {
            super(itemView);
            search = itemView.findViewById(R.id.search);
            search_name = itemView.findViewById(R.id.search_name);
            search_type = itemView.findViewById(R.id.search_type);
            itemView.setOnClickListener(v -> {
                SearchItem item = (SearchItem) v.getTag();
                if (listener != null && item != null) {
                    listener.OnItemClick(item);
                }
            });
        }

        @Override
        public void bind(SearchItem item, Context context) {
            search_name.setText(item.getName());
            if (item.getArtists_name() != null) {
                search_type.setText(item.getType() + " + " + String.join(", ", item.getArtists_name()));
            }
            Glide.with(context).load(item.getImage_url()).into(search);
            itemView.setTag(item);
        }
    }

}
