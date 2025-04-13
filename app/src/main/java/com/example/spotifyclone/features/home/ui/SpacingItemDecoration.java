package com.example.spotifyclone.features.home.ui;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int spacing;
    private final boolean includeEdge;

    public SpacingItemDecoration(int spacing, boolean includeEdge) {
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();

        // Apply spacing between items
        outRect.right = spacing;
        if (position == 0) {
            outRect.left = spacing; // Add left margin only for the first item
        }

        if (includeEdge) {
            outRect.top = spacing;
            outRect.bottom = spacing;
        }
    }
}
