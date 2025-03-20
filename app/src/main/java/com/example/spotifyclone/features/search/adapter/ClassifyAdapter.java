package com.example.spotifyclone.features.search.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;

import com.example.spotifyclone.features.search.inter.OnClassifyItemClickListener;

import java.util.List;

public class ClassifyAdapter extends RecyclerView.Adapter<ClassifyAdapter.ViewHolder> {

    private List<String> types;
    private final Context context;
    private final OnClassifyItemClickListener listener;

    public ClassifyAdapter(Context context, List<String> types, OnClassifyItemClickListener listener) {
        this.context = context;
        this.types = types;
        this.listener = listener;

        if (listener == null) {
            Log.e("ClassifyAdapter", "Listener is NULL!");
        } else {
            Log.d("ClassifyAdapter", "Listener initialized successfully");
        }

    }

    @NonNull
    @Override
    public ClassifyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_classify, parent, false);
        return new ClassifyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassifyAdapter.ViewHolder holder, int position) {
        String type = types.get(position);
        holder.bind(type, listener);
    }

    @Override
    public int getItemCount() {
        return (types != null) ? types.size() : 0;
    }

    // Cập nhật dữ liệu và refresh RecyclerView
    public void setData(List<String> newTypes) {
        this.types = newTypes;
        notifyDataSetChanged(); // Thêm notify để cập nhật UI
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textType = itemView.findViewById(R.id.textType);
        }

        public void bind(String type, OnClassifyItemClickListener listener) {
            textType.setText(type);

            itemView.setOnClickListener(v -> {
                Log.d("ClassifyAdapter", "Item clicked: " + type);
                if (listener != null) {
                    listener.OnItemClick(type);
                } else {
                    Log.e("ClassifyAdapter", "Listener is NULL when item clicked!");
                }
            });
        }
    }
}
