package com.example.spotifyclone.features.search.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.search.inter.OnSearchItemClickListener;
import com.example.spotifyclone.features.search.model.SearchItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<SearchItem> items;
    private final Context context;
    private final OnSearchItemClickListener listener;

    public SearchAdapter(Context context, List<SearchItem> items, OnSearchItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;

    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search, parent, false);

        return new SearchAdapter.ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        holder.bind(items.get(position), context);
    }

    @Override
    public int getItemCount() {
        return items.size();
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // declare UI item
        private final ImageView search;
        private final TextView search_name;
        private final TextView search_type;



        public ViewHolder(@NonNull View itemView, OnSearchItemClickListener listener) {
            super(itemView);
            search=itemView.findViewById(R.id.search);
            search_name=itemView.findViewById(R.id.search_name);
            search_type=itemView.findViewById(R.id.search_type);
//            itemView.setOnClickListener(v -> {
//                SearchItem item = (SearchItem) v.getTag();
//                if (listener != null && item != null) {
//                    listener.OnItemClick(item);
//                }
//            });
        }

        public void bind(SearchItem item, Context context) {
            search_name.setText(item.getName());
            search_type.setText(item.getType()+" + "+ String.join(" ,",item.getArtists_name()));
            RequestOptions options=new RequestOptions();

            switch (item.getType()) {
                case "artist":
                    options = options.transform(new CircleCrop()); // Ảnh tròn
                    break;
            }

            Log.d("search", item.getImage_url());
            Glide.with(context)
                    .load(item.getImage_url())
                    .into(search);
            itemView.setTag(item);
        }


    }}


