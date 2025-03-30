package com.example.spotifyclone.features.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class MusicNotificationIntents {
    public static PendingIntent createPlayPauseIntent(Context context) {
        Intent intent = new Intent(MusicNotificationReceiver.ACTION_PLAY_PAUSE);
        intent.setPackage(context.getPackageName());
        return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    public static PendingIntent createNextIntent(Context context) {
        Intent intent = new Intent(MusicNotificationReceiver.ACTION_NEXT);
        intent.setPackage(context.getPackageName());
        return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    public static PendingIntent createPreviousIntent(Context context) {
        Intent intent = new Intent(MusicNotificationReceiver.ACTION_PREVIOUS);
        intent.setPackage(context.getPackageName());
        return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    public static PendingIntent createSeekIntent(Context context, long position) {
        Intent intent = new Intent(MusicNotificationReceiver.ACTION_SEEK_TO);
        intent.setPackage(context.getPackageName());
        intent.putExtra(MusicNotificationReceiver.EXTRA_SEEK_POSITION, position);
        return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }
}