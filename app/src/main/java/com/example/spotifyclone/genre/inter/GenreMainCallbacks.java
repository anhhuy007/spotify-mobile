package com.example.spotifyclone.genre.inter;

import com.example.spotifyclone.genre.model.Genre;

public interface GenreMainCallbacks {
    public void onMsgFromFragToMain(String sender, Genre genre);
}
