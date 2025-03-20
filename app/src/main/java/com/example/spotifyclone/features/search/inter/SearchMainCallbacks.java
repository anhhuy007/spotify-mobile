package com.example.spotifyclone.features.search.inter;

import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.search.model.SearchItem;

public interface SearchMainCallbacks {
    public void onMsgFromFragToMain(String sender, SearchItem item);

}
