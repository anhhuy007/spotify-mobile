package com.example.spotifyclone.features.album.inter;

import android.view.View;

import java.util.List;

public interface OnSongMoreIconClickListener {
    void onMoreIconClick(String id, String song_image, String song_name, List<String>song_artists, View view);
}
