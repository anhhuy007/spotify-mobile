package com.example.spotifyclone.features.genre.inter;

import com.example.spotifyclone.features.genre.model.Genre;

public interface GenreFragmentCallbacks {
    public void onMsgFromMainToFragment(Genre genre);
}
