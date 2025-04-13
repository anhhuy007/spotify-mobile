package com.example.spotifyclone.features.playlist.inter;

import com.example.spotifyclone.features.player.model.song.Song;

public interface OnSongClickListner {
    void OnAddClickSong(Song song);
    void OnRemoveClickSong(Song song);
    void OnPlaySong(Song song); // for playing song
}
