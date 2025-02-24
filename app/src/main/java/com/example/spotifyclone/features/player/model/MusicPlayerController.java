package com.example.spotifyclone.features.player.model;

import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicPlayerController {
    private static MusicPlayerController instance;
    private final AudioPlayer audioPlayer;
    private final Context context;
    private final List<PlaybackListener> externalListeners;
    private PlayList playList;

    /**
     * Private constructor for singleton pattern
     */
    private MusicPlayerController(Context context) {
        this.context = context;
        this.audioPlayer = new AudioPlayer(context);
        this.externalListeners = new ArrayList<>();
        this.playList = new PlayList(new ArrayList<>(), PlayMode.NORMAL);

        setupInternalPlaybackListener();
    }

    /**
     * Get singleton instance
     */
    public static synchronized MusicPlayerController getInstance(Context context) {
        if (instance == null) {
            instance = new MusicPlayerController(context);
        }
        return instance;
    }

    /**
     * Setup internal playback listener to handle playback events
     */
    private void setupInternalPlaybackListener() {
        audioPlayer.setPlaybackListener(new PlaybackListener() {
            @Override
            public void onStarted(Song song) {
                notifyPlaybackStarted(song);
            }

            @Override
            public void onPaused(Song song) {
                notifyPlaybackPaused(song);
            }

            @Override
            public void onCompleted(Song song) {
                playNextSong();
            }

            @Override
            public void onError(Song song, String error) {
                notifyPlaybackError(error);
                playNextSong();
            }
        });
    }


    /**
     * Play or resume playback
     */
    public void play() {
        Song currentSong = playList.getCurrentSong();
        if (currentSong != null) {
            audioPlayer.loadAndPlay(currentSong);
        }
    }

    /**
     * Pause playback
     */
    public void pause() {
        audioPlayer.pause();
    }

    /**
     * Stop playback completely
     */
    public void stop() {
        audioPlayer.stop();
    }

    /**
     * Play the next song
     */
    public void playNextSong() {
        Song nextSong = playList.getNextSong();
        if (nextSong != null) {
            audioPlayer.loadAndPlay(nextSong);
        }
    }

    /**
     * Play the previous song
     */
    public void playPreviousSong() {
        Song previousSong = playList.getPreviousSong();
        if (previousSong != null) {
            audioPlayer.loadAndPlay(previousSong);
        }
    }

    /**
     * Set shuffle mode
     */
    public void setShuffle(boolean enabled) {
        playList.setPlayMode(enabled ? PlayMode.SHUFFLE : PlayMode.NORMAL);
    }

    /**
     * Set repeat mode
     */
    public void setRepeatMode(PlayMode mode) {
        playList.setPlayMode(mode);
    }

    /**
     * Get repeat mode
     */
    public PlayMode getRepeatMode() {
        return playList.getPlayMode();
    }

    /**
     * Check if music is playing
     */
    public boolean isPlaying() {
        return audioPlayer.isPlaying();
    }

    /**
     * Get current song
     */
    public Song getCurrentSong() {
        return playList.getCurrentSong();
    }

    /**
     * Notify all listeners that playback started
     */
    private void notifyPlaybackStarted(Song song) {
        for (PlaybackListener listener : externalListeners) {
            listener.onStarted(song);
        }
    }

    /**
     * Notify all listeners that playback paused
     */
    private void notifyPlaybackPaused(Song song) {
        for (PlaybackListener listener : externalListeners) {
            listener.onPaused(song);
        }
    }

    /**
     * Notify all listeners of a playback error
     */
    private void notifyPlaybackError(String error) {
        Song currentSong = getCurrentSong();
        for (PlaybackListener listener : externalListeners) {
            listener.onError(currentSong, error);
        }
    }
}
