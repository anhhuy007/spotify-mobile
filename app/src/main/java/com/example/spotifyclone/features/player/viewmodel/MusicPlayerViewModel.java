package com.example.spotifyclone.features.player.viewmodel;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifyclone.features.album.model.Album;
import com.example.spotifyclone.features.player.model.audio.MusicPlayerController;
import com.example.spotifyclone.features.player.model.audio.PlaybackListener;
import com.example.spotifyclone.features.player.model.playlist.PlayList;
import com.example.spotifyclone.features.player.model.playlist.RepeatMode;
import com.example.spotifyclone.features.player.model.playlist.ShuffleMode;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.player.network.SongService;
import com.example.spotifyclone.shared.model.PlayerState;
import com.example.spotifyclone.shared.repository.PlayerRepository;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * ViewModel that manages the state and business logic for the music player
 */
public class MusicPlayerViewModel extends ViewModel {
    private final MusicPlayerController playerController;
    private PlaybackListener playbackListener;

    // UI State
    private final MutableLiveData<List<Song>> upcomingSongs = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Song> currentSong = new MutableLiveData<>();
    private final MutableLiveData<String> currentAlbumId = new MutableLiveData<>();
    private final MutableLiveData<String> currentArtistId = new MutableLiveData<>();
    private final MutableLiveData<PlayList> currentPlaylist = new MutableLiveData<>(new PlayList(new ArrayList<>(), ShuffleMode.SHUFFLE_OFF));
    private final MutableLiveData<PlaybackState> playbackState = new MutableLiveData<>(PlaybackState.STOPPED);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<ShuffleMode> shuffleMode = new MutableLiveData<>(ShuffleMode.SHUFFLE_OFF);
    private final MutableLiveData<RepeatMode> repeatMode = new MutableLiveData<>(RepeatMode.REPEAT_OFF);
    private final MutableLiveData<Long> duration = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> currentDuration = new MutableLiveData<>(0L);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> currentName = new MutableLiveData<>();
    private final MutableLiveData<PlaybackSourceType> currentPlaybackSourceType = new MutableLiveData<>(PlaybackSourceType.NONE);
    public enum PlaybackSourceType {
        NONE,
        ALBUM,
        ARTIST, PLAYLIST
    }
    private final PlayerRepository playerRepository;

    // Handler for updating progress and current duration on UI
    private final Handler handler = new Handler(Looper. getMainLooper());
    private final Runnable updateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (playerController != null && playbackState.getValue() == PlaybackState.PLAYING) {
                long currentTime = playerController.getCurrentDuration();
                currentDuration.postValue(currentTime);
                handler.postDelayed(this, 1000);
            }
        }
    };

    public MusicPlayerViewModel(Context context, MusicPlayerController playerController) {
        super();
        this.playerController = playerController;
        this.playerRepository = new PlayerRepository(context.getApplicationContext());
        setupPlaybackListener();
    }

    public void restorePlayerState() {
        PlayerState state = this.playerRepository.loadPlayerState();
        setUpMusicPlayerViewModel(state);
    }
    public void setUpMusicPlayerViewModel(PlayerState state) {
        if (state == null) {
            return;
        }

        currentSong.setValue(state.getCurrentSong());
        currentDuration.setValue(state.getCurrentDuration());
        duration.setValue(state.getDuration());
        repeatMode.setValue(state.getRepeatMode());
        shuffleMode.setValue(state.getShuffleMode());
        playbackState.setValue(PlaybackState.PAUSED);

        upcomingSongs.setValue(state.getUpcomingSongs());
        setUpcomingSongs(state.getUpcomingSongs(), state.getCurrentSong(), Math.toIntExact(state.getCurrentDuration()));

        currentName.setValue(state.getCurrentName());

        PlaybackSourceType sourceType = state.getCurrentPlaybackSourceType();
        currentPlaybackSourceType.setValue(sourceType);

        if (sourceType == PlaybackSourceType.ALBUM) {
            currentAlbumId.setValue(state.getCurrentAlbumId());
        } else if (sourceType == PlaybackSourceType.ARTIST) {
            currentArtistId.setValue(state.getCurrentArtistId());
        } else if (sourceType == PlaybackSourceType.NONE) {
            currentName.setValue(null);
        }
    }

    public void setUpcomingSongs(List<Song> upcomingSongs, Song currentSong, int currentPosition) {
        if (upcomingSongs == null || currentSong == null) {
            return;
        }

        List<Song> songs = new ArrayList<>();
        songs.add(currentSong);
        songs.addAll(upcomingSongs);

        playerController.setSongs(songs, currentPosition);
    }

    public void savePlayerState() {
        Log.d("Upcoming songs", upcomingSongs.getValue().toString());
        PlayerState currentPlayerState = new PlayerState(
                currentSong.getValue(),
                upcomingSongs.getValue(),
                currentPlaylist.getValue(),
                currentArtistId.getValue(),
                currentAlbumId.getValue(),
                shuffleMode.getValue(),
                repeatMode.getValue(),
                duration.getValue(),
                currentDuration.getValue(),
                currentName.getValue(),
                currentPlaybackSourceType.getValue()
        );
        playerRepository.savePlayerState(currentPlayerState);
    }

    private void setupPlaybackListener() {
        playbackListener = new PlaybackListener() {
            @Override
            public void onStarted(Song song) {
                currentSong.postValue(song);
                playbackState.postValue(PlaybackState.PLAYING);
                updateUpcomingSongs();
                updateSongDuration();
                handler.post(updateProgressRunnable);
            }

            @Override
            public void onPaused(Song song) {
                playbackState.postValue(PlaybackState.PAUSED);
                handler.removeCallbacks(updateProgressRunnable);
            }

            @Override
            public void onCompleted(Song song) {
                playbackState.postValue(PlaybackState.COMPLETED);
                handler.removeCallbacks(updateProgressRunnable);
            }

            @Override
            public void onError(Song song, String error) {
                errorMessage.postValue(error);
                playbackState.postValue(PlaybackState.ERROR);
                handler.removeCallbacks(updateProgressRunnable);
            }
        };

        playerController.addPlaybackListener(playbackListener);
    }
    public void togglePlayPause() {
        PlaybackState currentState = playbackState.getValue();
        if (currentState == PlaybackState.PLAYING) {
            pausePlayback();
        } else {
            continuePlayback();
        }
    }
    public void togglePlayPauseAlbum(String albumId, String albumName) {
        PlaybackState currentState = playbackState.getValue();
        if (currentAlbumId.getValue() != null &&
                currentAlbumId.getValue().equals(albumId) &&
                currentPlaybackSourceType.getValue() == PlaybackSourceType.ALBUM) {
            if (currentState == PlaybackState.PLAYING) {
                pausePlayback();
            } else if (currentState == PlaybackState.PAUSED || currentState == PlaybackState.SEEKING) {
                continuePlayback();
            } else {
                playAlbum(albumId, albumName);
            }
        } else {
            playAlbum(albumId, albumName);
        }
    }
    public void playAlbum(String albumId, String albumName) {
        // Clear current playlist info
        currentPlaylist.setValue(new PlayList(new ArrayList<>(), ShuffleMode.SHUFFLE_OFF));
        currentArtistId.setValue(null);

        // Set current album info
        currentAlbumId.setValue(albumId);
        currentPlaybackSourceType.setValue(PlaybackSourceType.ALBUM);
        currentName.setValue(albumName);
        // Start playback
        playerController.playAlbum(albumId);
        playbackState.setValue(PlaybackState.LOADING);
        handler.post(updateProgressRunnable);
    }

    public void playAlbumSong(String albumId, String albumName, Song song) {
        // Clear current playlist info and
        currentPlaylist.setValue(new PlayList(new ArrayList<>(), ShuffleMode.SHUFFLE_OFF));
        currentArtistId.setValue(null);

        // Set current album info
        currentAlbumId.setValue(albumId);
        currentPlaybackSourceType.setValue(PlaybackSourceType.ALBUM);
        currentName.setValue(albumName);

        // Start playback
        playerController.playAlbumSong(albumId, song);
        playbackState.setValue(PlaybackState.LOADING);
        handler.post(updateProgressRunnable);
    }


    public void togglePlayPausePlaylist(PlayList playlist) {
        PlaybackState currentState = playbackState.getValue();
        PlayList currentList = currentPlaylist.getValue();

        if (currentList != null &&
                currentList.equals(playlist) &&
                currentPlaybackSourceType.getValue() == PlaybackSourceType.PLAYLIST) {
            if (currentState == PlaybackState.PLAYING) {
                pausePlayback();
            } else if (currentState == PlaybackState.PAUSED || currentState == PlaybackState.SEEKING) {
                continuePlayback();
            } else {
                playPlaylist(playlist);
            }
        } else {
            playPlaylist(playlist);
        }
    }

    public void continuePlayback() {
        playerController.continuePlaying();
        handler.post(updateProgressRunnable);
    }
    public void pausePlayback() {
        playerController.pause();
        handler.removeCallbacks(updateProgressRunnable);
    }
    public void playSong(Song song) {
        playerController.playSong(song);
        playbackState.setValue(PlaybackState.LOADING);
        handler.post(updateProgressRunnable);
    }
    public void togglePlayPauseArtist(String artistId, String artistName) {
        Log.d("Artist Click", "test" + artistId + artistName);
        PlaybackState currentState = playbackState.getValue();
        if (currentArtistId.getValue() != null &&
                currentArtistId.getValue().equals(artistId) &&
                currentPlaybackSourceType.getValue() == PlaybackSourceType.ARTIST) {
            if (currentState == PlaybackState.PLAYING) {
                pausePlayback();
            } else if (currentState == PlaybackState.PAUSED || currentState == PlaybackState.SEEKING) {
                continuePlayback();
            } else {
                playArtist(artistId, artistName);
            }
        } else {
            playArtist(artistId, artistName);
        }
    }
    public void playArtist(String artistId, String artistName) {
        // Clear current playlist info
        currentPlaylist.setValue(new PlayList(new ArrayList<>(), ShuffleMode.SHUFFLE_OFF));
        currentAlbumId.setValue(null);

        // Set current album info
        currentArtistId.setValue(artistId);
        currentPlaybackSourceType.setValue(PlaybackSourceType.ARTIST);
        currentName.setValue(artistName);

        // Start playback
        playerController.playArtist(artistId);
        playbackState.setValue(PlaybackState.LOADING);
        handler.post(updateProgressRunnable);
    }
    public void playArtistSong(String artistId, String artistName, String songId) {
        // Clear current playlist info
        currentPlaylist.setValue(new PlayList(new ArrayList<>(), ShuffleMode.SHUFFLE_OFF));
        currentAlbumId.setValue(null);

        // Set current album info
        currentArtistId.setValue(artistId);
        currentPlaybackSourceType.setValue(PlaybackSourceType.ARTIST);
        currentName.setValue(artistName);

        // Start playback
        playerController.playArtistSong(artistId, songId);
        playbackState.setValue(PlaybackState.LOADING);
        handler.post(updateProgressRunnable);
    }
    public void prioritizeSong(Song song) {
        playerController.prioritizeSong(song);
        playbackState.setValue(PlaybackState.LOADING);
        handler.post(updateProgressRunnable);
    }
    public void playPlaylist(PlayList playlist) {
        if (playlist == null || playlist.isEmpty()) {
            errorMessage.setValue("Cannot play empty playlist");
            return;
        }
        // Clear current album info and artist
        currentAlbumId.setValue(null);

        // Set current playlist info
        currentPlaylist.setValue(playlist);
        currentPlaybackSourceType.setValue(PlaybackSourceType.PLAYLIST);

        // Start playback
        playerController.playPlaylist(playlist);
        playbackState.setValue(PlaybackState.LOADING);
        handler.post(updateProgressRunnable);
    }

    public void stop() {
        playerController.stop();
        playbackState.setValue(PlaybackState.STOPPED);
        handler.removeCallbacks(updateProgressRunnable);
    }

    public void playNext() {
        if(!playerController.playNextSong()) {
            currentName.setValue(null);
            currentPlaybackSourceType.setValue(PlaybackSourceType.NONE);
        }
        playbackState.setValue(PlaybackState.LOADING);
    }

    public void playPrevious() {
        if (playerController.playPreviousSong()) {
            playbackState.setValue(PlaybackState.LOADING);
        } else {
            errorMessage.setValue("Do not have previous song");
        }
    }

    // Playback Settings
    public void cycleShuffleMode() {
        ShuffleMode currentMode = shuffleMode.getValue();
        ShuffleMode nextMode;

        switch (Objects.requireNonNull(currentMode)) {
            case SHUFFLE_OFF:
                nextMode = ShuffleMode.SHUFFLE_ON;
                break;
            default:
                nextMode = ShuffleMode.SHUFFLE_OFF;
        }

        shuffleMode.setValue(nextMode);
        playerController.setShuffle(nextMode);
    }
    public void cycleRepeatMode() {
        RepeatMode currentMode = repeatMode.getValue();
        RepeatMode nextMode;

        switch (Objects.requireNonNull(currentMode)) {
            case REPEAT_OFF:
                nextMode = RepeatMode.REPEAT_INFINITE;
                break;
            default:
                nextMode = RepeatMode.REPEAT_OFF;
        }

        repeatMode.setValue(nextMode);
        playerController.setRepeatMode(nextMode);
    }

        public void seekTo(int position) {
            playerController.seekTo(position);
            playbackState.setValue(PlaybackState.SEEKING);
        }

    private void updateSongDuration() {
        this.duration.setValue(playerController.getDuration());
        this.currentDuration.setValue(playerController.getCurrentDuration());
    }

    private void updateUpcomingSongs() {
        Log.d("VM", "Update upcoming songs");
        upcomingSongs.setValue(playerController.getUpcomingSongs());
    }

    // State Getters
    public LiveData<Song> getCurrentSong() {
        return currentSong;
    }
    public LiveData<List<Song>> getUpcomingSongs() {
        return upcomingSongs;
    }

    public LiveData<PlayList> getCurrentPlaylist() {
        return currentPlaylist;
    }

    public LiveData<String> getCurrentAlbumId() {
        return currentAlbumId;
    }

    public LiveData<PlaybackSourceType> getCurrentPlaybackSourceType() {
        return currentPlaybackSourceType;
    }

    public LiveData<PlaybackState> getPlaybackState() {
        return playbackState;
    }

    public LiveData<String> getError() {
        return errorMessage;
    }

    public LiveData<ShuffleMode> getShuffleMode() {
        return shuffleMode;
    }

    public LiveData<RepeatMode> getRepeatMode() {
        return repeatMode;
    }

    public LiveData<Long> getDuration() {
        return duration;
    }

    public LiveData<Long> getCurrentDuration() {
        return currentDuration;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }
    public LiveData<PlaybackSourceType> getPlayType() {
        return currentPlaybackSourceType;
    }
    public LiveData<String> getPlayName() {
        return currentName;
    }

    // Helper method to check if an album is currently playing
    public boolean isAlbumPlaying(String albumId) {
        return currentPlaybackSourceType.getValue() == PlaybackSourceType.ALBUM &&
                currentAlbumId.getValue() != null &&
                currentAlbumId.getValue().equals(albumId) &&
                playbackState.getValue() == PlaybackState.PLAYING;
    }

    // Helper method to check if a playlist is currently playing
    public boolean isPlaylistPlaying(PlayList playlist) {
        return currentPlaybackSourceType.getValue() == PlaybackSourceType.PLAYLIST &&
                currentPlaylist.getValue() != null &&
                currentPlaylist.getValue().equals(playlist) &&
                playbackState.getValue() == PlaybackState.PLAYING;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacks(updateProgressRunnable);
        try {
            if (playbackListener != null && playerController != null) {
                playerController.removePlaybackListener(playbackListener);
            }
        } catch (Exception e) {
            System.err.println("Error when removing playback listener: " + e.getMessage());
        }

        try {
            if (playerController != null) {
                playerController.close();
            }
        } catch (Exception e) {
            System.err.println("Error when closing player controller: " + e.getMessage());
        }
    }
}
