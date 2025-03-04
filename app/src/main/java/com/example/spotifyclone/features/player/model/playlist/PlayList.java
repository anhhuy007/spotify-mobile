package com.example.spotifyclone.features.player.model.playlist;

import android.util.Log;
import com.example.spotifyclone.features.player.model.song.Song;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayList {

    private static final String TAG = "PlayList";
    public final List<Song> songList;
    private int currentIndex;

    public PlayList(List<Song> songs, ShuffleMode shuffleMode) {
        Log.d(TAG, "Initializing PlayList with " + songs.size() + " songs.");
        this.songList = new ArrayList<>(songs);
        this.currentIndex = 0;
        if (shuffleMode == ShuffleMode.SHUFFLE_ON) {
            shuffle();
        }
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
        Log.d(TAG, "Shuffling playlist from index " + currentIndex);
        Collections.shuffle(songList.subList(currentIndex +  1, songList.size()));
    }

    public void printPlaylist() {
        Log.d(TAG, "Playlist:");
        for (int i = 0; i < songList.size(); i++) {
            Log.d(TAG, "Song " + i + ": " + songList.get(i).getTitle());
        }
    }

    public List<Song> getUpcomingSongs() {
        return songList.subList(currentIndex + 1, songList.size());
    }
}
