package com.example.spotifyclone.features.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.MainActivity;

import java.io.IOException;
import java.net.URL;

public class PlayerNotification {
    private static final String CHANNEL_ID = "music_player_channel";
    private static final String CHANNEL_NAME = "Music Player";
    private static final int NOTIFICATION_ID = 101;
    private static final String TAG = "PlayerNotification";

    private final Context context;
    private final NotificationManager notificationManager;
    private final MediaSessionCompat mediaSession;

    public PlayerNotification(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();

        // Create MediaSession
        this.mediaSession = new MediaSessionCompat(context, "MusicService");
        mediaSession.setActive(true);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Music Player Controls");
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createMediaNotification(Song song, boolean isPlaying, long currentPosition, long duration) {
        Glide.with(context)
                .asBitmap()
                .load(song.getImageUrl())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        Bitmap scaledBitmap = createScaledBitmap(resource, 800, 450);
                        buildNotification(song, resource, isPlaying, currentPosition, duration);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private void buildNotification(Song song, Bitmap songAvatar, boolean isPlaying, long currentPosition, long duration) {
        // Ensure valid duration and current position
        duration = duration > 0 ? duration : 0;
        currentPosition = currentPosition > 0 ? currentPosition : 0;

        // Create MediaMetadata with correct duration
        MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getSingersString())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, songAvatar)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                .build();

        mediaSession.setMetadata(metadata);

        // Create Playback State with more comprehensive actions
        int state = isPlaying ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
        PlaybackStateCompat playbackState = new PlaybackStateCompat.Builder()
                .setState(state, currentPosition, 1f)
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_SEEK_TO
                )
                .addCustomAction(
                        "shuffle", "Shuffle mode", R.drawable.ic_shuffle_off
                )
                .build();

        mediaSession.setPlaybackState(playbackState);
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onCustomAction(@NonNull String action, Bundle extras) {
                super.onCustomAction(action, extras);
                if ("shuffle".equals(action)) {
                    try {
                        Log.d(TAG, "Shuffle action received");
                        MusicNotificationIntents.createShuffleIntent(context).send();
                    } catch (Exception e) {
                        Log.e("MediaSession", "Failed to handle shuffle action", e);
                    }
                }
            }

            @Override
            public void onPlay() {
                Log.d("MediaSession", "Play button pressed");
                try {
                    MusicNotificationIntents.createPlayPauseIntent(context).send();
                } catch (PendingIntent.CanceledException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onPause() {
                Log.d("MediaSession", "Pause button pressed");
                try {
                    MusicNotificationIntents.createPlayPauseIntent(context).send();
                } catch (PendingIntent.CanceledException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onSkipToNext() {
                Log.d("MediaSession", "Next button pressed");
                try {
                    Log.d("MediaSession", "Next button pressed");
                    MusicNotificationIntents.createNextIntent(context).send();
                } catch (PendingIntent.CanceledException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onSkipToPrevious() {
                Log.d("MediaSession", "Previous button pressed");
                try {
                    MusicNotificationIntents.createPreviousIntent(context).send();
                } catch (PendingIntent.CanceledException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onSeekTo(long pos) {
                try {
                    MusicNotificationIntents.createSeekIntent(context, pos).send();
                } catch (PendingIntent.CanceledException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        PendingIntent contentIntent = createContentIntent();

        // Build notification with improved media style and progress
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_spotify)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowCancelButton(true)
                )
                .setContentTitle(song.getTitle())
                .setContentText(song.getSingersString())
                .setLargeIcon(songAvatar)
                .setContentIntent(contentIntent)
                .setProgress((int) duration, (int) currentPosition, true)
                .setOnlyAlertOnce(true)
                .build();

        // Update notification
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private PendingIntent createContentIntent() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    public void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public void release() {
        if (mediaSession != null) {
            mediaSession.release();
        }
    }
}