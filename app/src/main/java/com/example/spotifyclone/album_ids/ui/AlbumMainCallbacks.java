package com.example.spotifyclone.album_ids.ui;

import com.example.spotifyclone.album_ids.model.Album;


public interface AlbumMainCallbacks {
    public void onMsgFromFragToMain(String sender, Album album);
}

