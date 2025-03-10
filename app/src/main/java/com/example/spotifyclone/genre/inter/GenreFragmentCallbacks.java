package com.example.spotifyclone.genre.inter;

import com.example.spotifyclone.genre.model.Genre;

public interface GenreFragmentCallbacks {
    public void onMsgFromMainToFragment(Genre genre);
}
