package com.example.spotifyclone.shared.model;

import android.util.Log;
import android.content.SharedPreferences;
import com.example.spotifyclone.features.player.model.playlist.PlayList;
import com.example.spotifyclone.features.player.model.playlist.RepeatMode;
import com.example.spotifyclone.features.player.model.playlist.ShuffleMode;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class PlayerState {
    private List<Song> upcomingSongs;
    private Song currentSong;
    private String currentAlbumId;
    private String currentArtistId;
    private PlayList currentPlaylist;
    private ShuffleMode shuffleMode;
    private RepeatMode repeatMode;
    private Long duration;
    private Long currentDuration;
    private String currentName;
    private MusicPlayerViewModel.PlaybackSourceType currentPlaybackSourceType;

    public PlayerState() {
        this.currentSong = null;
        this.upcomingSongs = new ArrayList<>();
        this.currentPlaylist = new PlayList(new ArrayList<>(), ShuffleMode.SHUFFLE_OFF);
        this.currentArtistId = null;
        this.currentAlbumId = null;
        this.shuffleMode = ShuffleMode.SHUFFLE_OFF;
        this.repeatMode = RepeatMode.REPEAT_OFF;
        this.duration = 0L;
        this.currentDuration = 0L;
        this.currentName = "";
        this.currentPlaybackSourceType = MusicPlayerViewModel.PlaybackSourceType.NONE;
    }

    public PlayerState(Song currentSong, List<Song> upcomingSongs, PlayList currentPlaylist,
                       String currentArtistId, String currentAlbumId, ShuffleMode shuffleMode,
                       RepeatMode repeatMode, Long duration, Long currentDuration, String currentName,
                       MusicPlayerViewModel.PlaybackSourceType currentPlaybackSourceType) {
        this.currentSong = currentSong;
        this.upcomingSongs = (upcomingSongs != null) ? new ArrayList<>(upcomingSongs) : new ArrayList<>();
        this.currentPlaylist = (currentPlaylist != null) ? currentPlaylist : new PlayList(new ArrayList<>(), ShuffleMode.SHUFFLE_OFF);
        this.currentArtistId = currentArtistId;
        this.currentAlbumId = currentAlbumId;
        this.shuffleMode = (shuffleMode != null) ? shuffleMode : ShuffleMode.SHUFFLE_OFF;
        this.repeatMode = (repeatMode != null) ? repeatMode : RepeatMode.REPEAT_OFF;
        this.duration =  (currentSong != null) ? duration : 0L;
        this.currentDuration = (currentSong != null) ? currentDuration : 0L;
        this.currentName = (currentName != null) ? currentName : ((currentSong != null) ? currentSong.getTitle() : "");
        this.currentPlaybackSourceType = (currentPlaybackSourceType != null) ? currentPlaybackSourceType : MusicPlayerViewModel.PlaybackSourceType.NONE;
    }

    // Getters & Setters
    public List<Song> getUpcomingSongs() { return upcomingSongs; }
    public Song getCurrentSong() { return currentSong; }
    public String getCurrentAlbumId() { return currentAlbumId; }
    public String getCurrentArtistId() { return currentArtistId; }
    public PlayList getCurrentPlaylist() { return currentPlaylist; }
    public ShuffleMode getShuffleMode() { return shuffleMode; }
    public RepeatMode getRepeatMode() { return repeatMode; }
    public Long getDuration() {return duration; }
    public Long getCurrentDuration() { return currentDuration; }
    public String getCurrentName() { return currentName; }
    public MusicPlayerViewModel.PlaybackSourceType getCurrentPlaybackSourceType() { return currentPlaybackSourceType; }

    public void setUpcomingSongs(List<Song> upcomingSongs) { this.upcomingSongs = upcomingSongs; }
    public void setCurrentSong(Song currentSong) { this.currentSong = currentSong; }
    public void setCurrentAlbumId(String albumId) {this.currentAlbumId = albumId; }
    public void setCurrentArtistId(String artistId) { this.currentArtistId = artistId; }
    public void setCurrentPlaylist(PlayList currentPlaylist) { this.currentPlaylist = currentPlaylist; }
    public void setShuffleMode(ShuffleMode shuffleMode) { this.shuffleMode = shuffleMode; }
    public void setRepeatMode(RepeatMode repeatMode) { this.repeatMode = repeatMode; }
    public void setCurrentDuration(Long currentDuration) { this.currentDuration = currentDuration; }
    public void setDuration(long duration) {
        this.duration = duration;
    }
    public void setCurrentName(String currentName) { this.currentName = currentName; }
    public void setCurrentPlaybackSourceType(MusicPlayerViewModel.PlaybackSourceType currentPlaybackSourceType) { this.currentPlaybackSourceType = currentPlaybackSourceType; }

    @Override
    public String toString() {
        return "PlayerState{" +
                "currentSong=" + this.getCurrentSong() +
                ", upcomingSongs=" + this.getUpcomingSongs() +
                ", currentAlbumId='" + this.getCurrentAlbumId() + '\'' +
                ", currentArtistId='" + this.getCurrentArtistId() + '\'' +
                ", currentPlaylist=" + this.getCurrentPlaylist() +
                ", shuffleMode=" + this.getShuffleMode() +
                ", repeatMode=" + this.getRepeatMode() +
                ", currentDuration=" + this.getCurrentDuration() +
                ", currentName='" + this.getCurrentName() + '\'' +
                ", currentPlaybackSourceType=" + this.getCurrentPlaybackSourceType() +
                '}';
    }


}
