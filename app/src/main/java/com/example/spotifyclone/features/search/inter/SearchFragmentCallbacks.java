package com.example.spotifyclone.features.search.inter;

import com.example.spotifyclone.features.search.model.SearchItem;

public interface SearchFragmentCallbacks {
    public void onMsgFromMainToFrag(SearchItem item);
}
