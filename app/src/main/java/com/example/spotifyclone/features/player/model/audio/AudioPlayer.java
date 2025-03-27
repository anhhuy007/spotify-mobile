package com.example.spotifyclone.features.player.model.audio;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.spotifyclone.features.player.model.song.Song;

public class AudioPlayer implements AutoCloseable {
    private final ExoPlayer exoPlayer;
    private Song currentSong;
    private PlaybackListener playbackListener;

    public AudioPlayer(Context context) {
        this.exoPlayer = new ExoPlayer.Builder(context).build();
        setupPlayerListeners();
    }

    private void setupPlayerListeners() {
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED && playbackListener != null) {
                    playbackListener.onCompleted(currentSong);
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (playbackListener != null) {
                    if (isPlaying) {
                        playbackListener.onStarted(currentSong);
                    } else {
                        playbackListener.onPaused(currentSong);
                    }
                }
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                if (playbackListener != null) {
                    playbackListener.onError(currentSong, error.getMessage());
                }
            }
        });
    }

    public void setPlaybackListener(PlaybackListener listener) {
        this.playbackListener = listener;
    }

        public void loadAndPlayFrom(@NonNull Song song, int currentPosition) {
            try {
                if (exoPlayer.isPlaying()) {
                    exoPlayer.stop();
                }
                currentSong = song;
                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(song.getMp3Url()));
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();

                exoPlayer.seekTo(currentPosition);
                exoPlayer.pause();

            } catch (Exception e) {
                if (playbackListener != null) {
                    playbackListener.onError(song, "Failed to load media: " + e.getMessage());
                }
            }
        }

    public void loadAndPlay(@NonNull Song song) {
        try {
            if (exoPlayer.isPlaying()) {
                exoPlayer.stop();
            }
            currentSong = song;
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(song.getMp3Url()));
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();
        } catch (Exception e) {
            if (playbackListener != null) {
                playbackListener.onError(song, "Failed to load media: " + e.getMessage());
            }
        }
    }

    public void play() {
        exoPlayer.play();
    }

    public void pause() {
        exoPlayer.pause();
    }
    public void stop() {
        exoPlayer.stop();
        currentSong = null;
    }

    public void seekTo(long position) {
        exoPlayer.seekTo(position);
    }

    public long getCurrentPosition() {
        return exoPlayer.getCurrentPosition();
    }

    public long getDuration() {
        return exoPlayer.getDuration();
    }
    public long getCurrentDuration() {
        return exoPlayer.getCurrentPosition();
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public boolean isPlaying() {
        return exoPlayer.isPlaying();
    }

    @Override
    public void close() {
        try {
            stop();
            exoPlayer.release();
        } catch (Exception e) {
            // Log error if needed
        }
    }


}
