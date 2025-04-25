package com.example.spotifyclone.features.player.model.playlist;

import android.util.Log;
import com.example.spotifyclone.features.player.model.song.Song;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayList {

    private static final String TAG = "PlayList";
    public List<Song> songList;
    private int currentIndex;

    public PlayList(List<Song> songs, ShuffleMode shuffleMode) {
        this.songList = new ArrayList<>(songs);
        this.currentIndex = 0;
        if (shuffleMode == ShuffleMode.SHUFFLE_ON) {
            shuffle();
        }
    }

    public void setSongList(List<Song> songs) {
        songList.addAll(songs);
    }

    public Song getCurrentSong() {
        return (currentIndex >= 0 && currentIndex < songList.size()) ? songList.get(currentIndex) : null;
    }

    public Song moveToNextSong() {
        if (currentIndex == songList.size() - 1) {
            return null;
        }
        printPlaylist();
        currentIndex++;
        return songList.get(currentIndex);
    }

    public Song getPreviousSong() {
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            return null;
        }
        return songList.get(currentIndex);
    }

    public void reset() {
        currentIndex = 0;
    }

    public boolean isEmpty() {
        return songList.isEmpty();
    }

    public int getRemainingSongs() {
        return songList.size() - currentIndex;
    }

    public void insertSong(Song song) {
        if (song == null) {
            return;
        }

        Song current = getCurrentSong();
        if (current != null && current.getTitle() != null && song.getTitle() != null) {
            if (current.getTitle().equalsIgnoreCase(song.getTitle())) {
                return;
            }
        }
        songList.add(currentIndex, song);
    }

    public void insertSongs(List<Song> songs) {
        if (songs == null || songs.isEmpty()) {
            return;
        }
        for (int i = songs.size() - 1; i >= 0; i--) {
            insertSong(songs.get(i));
        }
    }

    public void addSongs(List<Song> songs) {
        if (songs == null || songs.isEmpty()) {
            return;
        }

        for (Song song : songs) {
            if (songList.isEmpty() || !songList.get(songList.size() - 1).getTitle().equalsIgnoreCase(song.getTitle())) {
                songList.add(song);
            }
        }
        printPlaylist();
    }


    public void insertPlaylist(PlayList newPlayList) {
        for (Song song : newPlayList.songList) {
            songList.add(currentIndex++, song);
        }
    }

    public void shuffle() {
        if (songList.size() - (currentIndex + 1) > 2) {
            Collections.shuffle(songList.subList(currentIndex + 1, songList.size()));
        } else {
            Log.e(TAG, "Not enough songs to shuffle.");
        }
    }


    public void printPlaylist() {}

    public List<Song> getUpcomingSongs() {
        return songList.subList(currentIndex + 1, songList.size());
    }

    public void prioritizeSong(Song song) {
        if (song == null || songList.isEmpty()) {
            return;
        }

        if (song.equals(getCurrentSong())) {
            return;
        }

        currentIndex++;
        if (currentIndex >= songList.size()) {
            currentIndex = songList.size() - 1;
        }

        songList.add(currentIndex, song);

        for (int i = currentIndex + 1; i < songList.size(); i++) {
            if (songList.get(i).getTitle().equalsIgnoreCase(song.getTitle())) {
                songList.remove(i);
                i--;
            }
        }
    }
    public void addFirstSong(Song song) {
        if (songList == null) {
            songList = new ArrayList<>();
        }
        songList.removeIf(s -> s.getId().equals(song.getId()));
        songList.add(0, song);
    }

    public void clear() {
        songList.clear();
        currentIndex = 0;
    }

    public boolean checkNextSong() {
        return currentIndex + 1 < songList.size();
    }
}
