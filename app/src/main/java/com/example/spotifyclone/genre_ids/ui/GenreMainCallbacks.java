package com.example.spotifyclone.genre_ids.ui;

import com.example.spotifyclone.genre_ids.model.Genre;

public interface GenreMainCallbacks {
    public void onMsgFromFragToMain(String sender, Genre genre);
}
