package com.example.spotifyclone.album.inter;

import com.example.spotifyclone.album.model.Album;

public interface AlbumFragmentCallbacks {
    public void onMsgFromMainToFragment(Album album); //id of album
}
