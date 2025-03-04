package com.example.spotifyclone.album_ids.ui;

import com.example.spotifyclone.album_ids.model.Album;

public interface AlbumFragmentCallbacks {
    public void onMsgFromMainToFragment(Album album); //id of album
}
