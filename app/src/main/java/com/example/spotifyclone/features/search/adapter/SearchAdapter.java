package com.example.spotifyclone.features.search.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.album.adapter.AlbumSongAdapter;
import com.example.spotifyclone.features.album.ui.AlbumBottomSheet;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.search.inter.OnSearchItemClickListener;
import com.example.spotifyclone.features.search.model.SearchItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.BaseViewHolder> {
    private List<SearchItem> items;
    private final Context context;
    private final OnSearchItemClickListener listener;

    private static final int TYPE_SONG = 0;
    private static final int TYPE_ALBUM = 1;
    private static final int TYPE_ARTIST = 2;
    private static final int TYPE_GENRE = 3;

    public SearchAdapter(Context context, List<SearchItem> items,  OnSearchItemClickListener listener) {
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
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == TYPE_SONG) {
            view = inflater.inflate(R.layout.item_search_song, parent, false);
            return new SongViewHolder(view, listener);
        } else if (viewType == TYPE_ALBUM) {
            view = inflater.inflate(R.layout.item_search_album, parent, false);
            return new AlbumViewHolder(view, listener);
        } else if (viewType == TYPE_ARTIST) {
            view = inflater.inflate(R.layout.item_search_artist, parent, false);
            return new ArtistViewHolder(view, listener);
        } else {
            view = inflater.inflate(R.layout.item_search_genre, parent, false);
            return new GenreViewHolder(view, listener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
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
    public static class SongViewHolder extends BaseViewHolder {
        private final ImageView search;
        private final TextView search_name;
        private final TextView search_type;
        private final ImageButton moreOptionsButton;

        public SongViewHolder(@NonNull View itemView, OnSearchItemClickListener listener) {
            super(itemView);
            search = itemView.findViewById(R.id.search);
            search_name = itemView.findViewById(R.id.search_name);
            search_type = itemView.findViewById(R.id.search_type);
            moreOptionsButton=itemView.findViewById(R.id.moreOptionsButton);
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
            moreOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof FragmentActivity) {
//                        AlbumBottomSheet bottomSheet = AlbumBottomSheet.newInstance(item.getImage_url(), item.getName(), item.getArtists_name());
//                        bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), "BottomSheet");

                    }

                }
            });
            itemView.setTag(item);
        }
    }

    public static class AlbumViewHolder extends BaseViewHolder {
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

    public static class ArtistViewHolder extends BaseViewHolder {
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

    public static class GenreViewHolder extends BaseViewHolder {
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


