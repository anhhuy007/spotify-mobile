package com.example.spotifyclone.features.player.model.playlist;

import android.util.Log;
import com.example.spotifyclone.features.player.model.song.Song;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PlayList {

    private static final String TAG = "PlayList";
    public List<Song> songList;
    private int currentIndex;

    public PlayList(List<Song> songs, ShuffleMode shuffleMode) {
        Log.d(TAG, "Initializing PlayList with " + songs.size() + " songs.");
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
        Song currentSong = (currentIndex >= 0 && currentIndex < songList.size()) ? songList.get(currentIndex) : null;
        Log.d(TAG, "Getting current song: " + (currentSong != null ? currentSong.getTitle() : "None"));
        return currentSong;
    }

    public Song getNextSong() {
        if (currentIndex == songList.size() - 1) {
            return null;
        }
        currentIndex++;
        Song nextSong = songList.get(currentIndex);
        Log.d(TAG, "Getting next song: " + nextSong.getTitle());
        return nextSong;
    }

    public Song getPreviousSong() {
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            currentIndex = songList.size() - 1;
        }
        Song previousSong = songList.get(currentIndex);
        Log.d(TAG, "Getting previous song: " + previousSong.getTitle());
        return previousSong;
    }

    public void reset() {
        Log.d(TAG, "Resetting playlist index.");
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
        printPlaylist();
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
    }


    public void insertPlaylist(PlayList newPlayList) {
        Log.d(TAG, "Inserting new playlist with " + newPlayList.songList.size() + " songs.");
        for (Song song : newPlayList.songList) {
            Log.d(TAG, "Adding song: " + song.getTitle());
            songList.add(currentIndex++, song);
        }
    }

    public void shuffle() {
        if (songList.size() - (currentIndex + 1) > 2) {
            Collections.shuffle(songList.subList(currentIndex + 1, songList.size()));
        } else {
            Log.d(TAG, "Not enough songs to shuffle.");
        }
    }


    public void printPlaylist() {
        Log.d(TAG, "Playlist:");
        for (int i = 0; i < songList.size(); i++) {
            Log.d(TAG, "Song " + i + ": " + songList.get(i).getTitle());
        }
    }

    public List<Song> getUpcomingSongs() {
        Log.d("Upcoming index", currentIndex + 1 + "");
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

        Log.d(TAG, "Prioritized song: " + song.getTitle() + " at index " + currentIndex);
        printPlaylist();
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
}
