package com.example.spotifyclone.features.album.inter;

import com.example.spotifyclone.features.album.model.Album;


public interface AlbumMainCallbacks {
    public void onMsgFromFragToMain(String sender, Album album);
}

