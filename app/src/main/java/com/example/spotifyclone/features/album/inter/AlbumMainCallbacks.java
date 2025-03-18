package com.example.spotifyclone.album.inter;

import com.example.spotifyclone.album.model.Album;


public interface AlbumMainCallbacks {
    public void onMsgFromFragToMain(String sender, Album album);
}

