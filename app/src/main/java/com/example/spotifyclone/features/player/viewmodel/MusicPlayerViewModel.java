package com.example.spotifyclone.features.player.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//import com.example.spotifyclone.features.notification.MusicNotificationManager;
import com.example.spotifyclone.features.player.model.audio.MusicPlayerController;
import com.example.spotifyclone.features.player.model.audio.PlaybackListener;
import com.example.spotifyclone.features.player.model.playlist.PlayList;
import com.example.spotifyclone.features.player.model.playlist.RepeatMode;
import com.example.spotifyclone.features.player.model.playlist.ShuffleMode;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.shared.model.PlayerState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ViewModel that manages the state and business logic for the music player
 */
public class MusicPlayerViewModel extends ViewModel {
    private final MusicPlayerController playerController;
    private PlaybackListener playbackListener;
    // UI State
    private final MutableLiveData<List<Song>> upcomingSongs = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<PlaybackState> playbackState = new MutableLiveData<>(PlaybackState.STOPPED);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<ShuffleMode> shuffleMode = new MutableLiveData<>(ShuffleMode.SHUFFLE_OFF);
    private final MutableLiveData<RepeatMode> repeatMode = new MutableLiveData<>(RepeatMode.REPEAT_OFF);
    private final MutableLiveData<Long> duration = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> currentDuration = new MutableLiveData<>(0L);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Song> currentSong = new MutableLiveData<>();
    private final MutableLiveData<String> currentAlbumId = new MutableLiveData<>();
    private final MutableLiveData<String> currentArtistId = new MutableLiveData<>();
    private final MutableLiveData<PlayList> currentPlaylist = new MutableLiveData<>(new PlayList(new ArrayList<>(), ShuffleMode.SHUFFLE_OFF));
    private final MutableLiveData<PlaybackSourceType> currentPlaybackSourceType = new MutableLiveData<>(PlaybackSourceType.RANDOM);
    private final MutableLiveData<String> currentName = new MutableLiveData<>();
    public enum PlaybackSourceType {
        RANDOM,
        ALBUM,
        ARTIST,
        PLAYLIST
    }

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

    public MusicPlayerViewModel(MusicPlayerController playerController) {
        super();
        this.playerController = playerController;
        setupPlaybackListener();
    }

    public void setUpMusicPlayerViewModel(PlayerState state) {
        if (state == null) {
            return;
        }

        currentSong.setValue(state.getCurrentSong());
        currentDuration.setValue(state.getCurrentDuration());
        duration.setValue(state.getDuration());

        setShuffleMode(state.getShuffleMode());
        setRepeatMode(state.getRepeatMode());

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
        } else if (sourceType == PlaybackSourceType.RANDOM) {
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

    public void playSongsFrom(String sourceId, String sourceName, PlaybackSourceType type, String prioritizedSongId) {
        currentPlaylist.setValue(new PlayList(new ArrayList<>(), ShuffleMode.SHUFFLE_OFF));
        currentAlbumId.setValue(null);
        currentArtistId.setValue(null);

        switch (type) {
            case ALBUM:
                currentAlbumId.setValue(sourceId);
                break;

            case ARTIST:
                currentArtistId.setValue(sourceId);
                break;



            default:
                Log.e("MusicPlayer", "⚠️ PlaybackSourceType không hợp lệ!");
                return;
        }

        playerController.playSongsFrom(sourceId, type, prioritizedSongId);

        currentPlaybackSourceType.setValue(type);
        currentName.setValue(sourceName);
        playbackState.setValue(PlaybackState.LOADING);
        handler.post(updateProgressRunnable);
    }

    public void togglePlayPause(String id, String name, PlaybackSourceType type) {
        PlaybackState currentState = playbackState.getValue();

        boolean isSameSource = (type == PlaybackSourceType.ALBUM && id.equals(currentAlbumId.getValue())) ||
                (type == PlaybackSourceType.ARTIST && id.equals(currentArtistId.getValue()));

        if (isSameSource && currentPlaybackSourceType.getValue() == type) {
            if (currentState == PlaybackState.PLAYING) {
                pausePlayback();
            } else if (currentState == PlaybackState.PAUSED || currentState == PlaybackState.SEEKING) {
                continuePlayback();
            } else {
                playSongsFrom(id, name, type, null);
            }
        } else {
            playSongsFrom(id, name, type, null);
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

    public void prioritizeSong(Song song) {
        playerController.prioritizeSong(song);
        playbackState.setValue(PlaybackState.LOADING);
        handler.post(updateProgressRunnable);
    }
    public void playPlaylist(PlayList playlist) {
        if (playlist == null || playlist.isEmpty()) {
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
            currentPlaybackSourceType.setValue(PlaybackSourceType.RANDOM);
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

    public void setShuffleMode(ShuffleMode mode) {
        shuffleMode.setValue(mode);
        playerController.setShuffle(mode);
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
    public void setRepeatMode(RepeatMode mode) {
        repeatMode.setValue(mode);
        playerController.setRepeatMode(mode);
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
    public LiveData<String> getCurrentArtistId() {
        return currentArtistId;
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
