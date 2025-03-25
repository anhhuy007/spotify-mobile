package com.example.spotifyclone.features.notification;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.player.viewmodel.MusicPlayerViewModel;

import java.io.IOException;
import java.net.URL;

public class MusicNotificationManager {
    private static final String CHANNEL_ID = "music_player_channel";
    private static final int NOTIFICATION_ID = 101;

    private final Context context;
    private final NotificationManager notificationManager;
    private final MusicPlayerViewModel viewModel;

    public MusicNotificationManager(Context context, MusicPlayerViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Player",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Controls music playback");
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("RemoteViewLayout")
    public void showNotification(Song song, boolean isPlaying, boolean isShuffleEnabled) {
        RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.music_notification);

        // Set song information
        notificationLayout.setTextViewText(R.id.notification_song_title, song.getTitle());
        notificationLayout.setTextViewText(R.id.notification_song_artist, song.getSingersString());
        notificationLayout.setImageViewBitmap(R.id.notification_background, getSongArt(song.getImageUrl()));

        // Set play/pause button state
        int playPauseIcon = isPlaying ? R.drawable.ic_pause_circle : R.drawable.ic_play_circle;
        notificationLayout.setImageViewResource(R.id.notification_play_pause, playPauseIcon);

        // Set shuffle button state
        int shuffleIcon = isShuffleEnabled ? R.drawable.ic_shuffle_on : R.drawable.ic_shuffle_off;
        notificationLayout.setImageViewResource(R.id.notification_shuffle, shuffleIcon);

        // Create pending intents for media controls
        notificationLayout.setOnClickPendingIntent(R.id.notification_prev,
                createPendingIntent("ACTION_PREVIOUS"));
        notificationLayout.setOnClickPendingIntent(R.id.notification_play_pause,
                createPendingIntent("ACTION_PLAY_PAUSE"));
        notificationLayout.setOnClickPendingIntent(R.id.notification_next,
                createPendingIntent("ACTION_NEXT"));
        notificationLayout.setOnClickPendingIntent(R.id.notification_shuffle,
                createPendingIntent("ACTION_SHUFFLE"));
//        notificationLayout.setOnClickPendingIntent(R.id.notification_close,
//                createPendingIntent("ACTION_CLOSE"));

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_music_note)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayout);

        // Show notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void updateNotificationProgress(Song song, long currentPosition, long totalDuration) {
        // Update progress bar or time if your notification layout supports it
        // This might require adding progress bar to your notification layout
    }

    private PendingIntent createPendingIntent(String action) {
        Intent intent = new Intent(context, MusicNotificationReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(
                context,
                action.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private Bitmap getSongArt(String url) {
        try {
            return BitmapFactory.decodeStream(new URL(url).openStream());
        } catch (IOException e) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avatar);
        }
    }

    public void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}