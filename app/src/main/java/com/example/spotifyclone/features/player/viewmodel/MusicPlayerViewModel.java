package com.example.spotifyclone.features.player.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifyclone.features.player.model.audio.MusicPlayerController;
import com.example.spotifyclone.features.player.model.audio.PlaybackListener;
import com.example.spotifyclone.features.player.model.playlist.PlayList;
import com.example.spotifyclone.features.player.model.playlist.RepeatMode;
import com.example.spotifyclone.features.player.model.playlist.ShuffleMode;
import com.example.spotifyclone.features.player.model.song.PlaybackState;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.player.network.SongService;
import com.google.gson.Gson;

import java.io.IOException;
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
    private final SongService songService;
    private PlaybackListener playbackListener;

    // UI State
    private final MutableLiveData<List<Song>> upcomingSongs = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Song> currentSong = new MutableLiveData<>();
    private final MutableLiveData<PlayList> currentPlaylist = new MutableLiveData<>(new PlayList(new ArrayList<>(), ShuffleMode.SHUFFLE_OFF));
    private final MutableLiveData<List<Song>> topSongs = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<PlaybackState> playbackState = new MutableLiveData<>(PlaybackState.STOPPED);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<ShuffleMode> shuffleMode = new MutableLiveData<>(ShuffleMode.SHUFFLE_OFF);
    private final MutableLiveData<RepeatMode> repeatMode = new MutableLiveData<>(RepeatMode.REPEAT_OFF);
    private final MutableLiveData<Long> duration = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> currentDuration = new MutableLiveData<>(0L);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

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

    public MusicPlayerViewModel(MusicPlayerController playerController, SongService songService) {
        this.playerController = playerController;
        this.songService = songService;
        setupPlaybackListener();
        fetchSongs();
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

    public void fetchSongs() {
        isLoading.setValue(true);

        try {
            Call<List<Song>> call = songService.getSongs();
            Log.d("API_CALL", "Calling API: " + call.request().url().toString());

            call.enqueue(new Callback<List<Song>>() {
                @Override
                public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                    isLoading.postValue(false);
                    try {
                        if (response.isSuccessful()) {
                            List<Song> songs = response.body();
                            Gson gson = new Gson();
                            String jsonResponse = gson.toJson(songs);
                            Log.d("API_RESPONSE", "Received response: " + jsonResponse);

                            if (songs != null) {
                                topSongs.postValue(songs);
                                Log.d("API_RESPONSE", "Received " + songs.size() + " songs");
                                for (Song song : songs) {
                                    Log.d("API_RESPONSE", "Song: " + song.getTitle());
                                }
                            } else {
                                errorMessage.postValue("No songs found");
                            }
                        } else {
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage.postValue("API Error: " + response.errorBody().string());
                                } else {
                                    errorMessage.postValue("Unknown API Error");
                                }
                            } catch (IOException e) {
                                errorMessage.postValue("Error reading API response: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        errorMessage.postValue("Error parsing API response: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<List<Song>> call, Throwable t) {
                    isLoading.postValue(false);
                    errorMessage.postValue("API Error: " + t.getMessage());
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            isLoading.setValue(false);
            errorMessage.postValue("API Error: " + e.getMessage());
        }
    }

    // Playback Controls
    public void togglePlayPause(Song song) {
        PlaybackState currentState = playbackState.getValue();
        if (currentSong.getValue() != null && currentSong.getValue().equals(song)) {
            if (currentState == PlaybackState.PLAYING) {
                pausePlayback();
            } else if (currentState == PlaybackState.PAUSED || currentState == PlaybackState.SEEKING) {
                continuePlayback();
            } else {
                playSong(song);
            }
        } else {
            playSong(song);
        }
    }
    public void togglePlayPause() {
        PlaybackState currentState = playbackState.getValue();
        if (currentState == PlaybackState.PLAYING) {
            pausePlayback();
        } else {
            continuePlayback();
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
        playerController.playPlaylist(playlist);
        playbackState.setValue(PlaybackState.LOADING);
    }

    public void stop() {
        playerController.stop();
        playbackState.setValue(PlaybackState.STOPPED);
        handler.removeCallbacks(updateProgressRunnable);
    }

    public void playNext() {
        playerController.playNextSong();
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
        upcomingSongs.setValue(playerController.getUpcomingSongs());
    }

    // State Getters
    public LiveData<Song> getCurrentSong() {
        return currentSong;
    }
    public LiveData<List<Song>> getUpcomingSongs() {
        return upcomingSongs;
    }

    public LiveData<List<Song>> getTopSongs() {
        return topSongs;
    }
    public LiveData<PlayList> getCurrentPlaylist() {
        return currentPlaylist;
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
