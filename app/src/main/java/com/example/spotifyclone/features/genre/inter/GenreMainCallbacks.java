package com.example.spotifyclone.features.genre.inter;

import com.example.spotifyclone.features.genre.model.Genre;

public interface GenreMainCallbacks {
    public void onMsgFromFragToMain(String sender, Genre genre);
}
