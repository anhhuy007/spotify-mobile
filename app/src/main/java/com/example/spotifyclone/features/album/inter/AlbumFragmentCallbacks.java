package com.example.spotifyclone.features.album.inter;

import com.example.spotifyclone.features.album.model.Album;

public interface AlbumFragmentCallbacks {
    public void onMsgFromMainToFragment(Album album); //id of album
}
