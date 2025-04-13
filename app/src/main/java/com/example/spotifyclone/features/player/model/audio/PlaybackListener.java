package com.example.spotifyclone.features.player.model.audio;

import com.example.spotifyclone.features.player.model.song.Song;

public interface PlaybackListener {
    void onStarted(Song song);
    void onPaused(Song song);
    void onCompleted(Song song);
    void onError(Song song, String error);
}