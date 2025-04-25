package com.example.spotifyclone.shared.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.spotifyclone.R;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.User;
import com.example.spotifyclone.shared.network.ApiService;
import com.example.spotifyclone.shared.network.RetrofitClient;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        sendTokenToServer(this, token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().isEmpty()) {
            return;
        }

        remoteMessage.getData();
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        showNotification(title, body);
    }

    private void showNotification(String title, String message) {
        String channelId = "spotify_channel"; // Channel ID must match when building notification

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Spotify Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo_horizontal)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        manager.notify(1, builder.build());
    }

    public void sendTokenToServer(Context context, String token) {
         ApiService apiService = RetrofitClient.getClient(context).create(ApiService.class);
         Call<APIResponse<User>> call = apiService.updateFCMToken(token);
         call.enqueue(new Callback<APIResponse<User>>() {
                @Override
                public void onResponse(Call<APIResponse<User>> call, retrofit2.Response<APIResponse<User>> response) {
                }

                @Override
                public void onFailure(Call<APIResponse<User>> call, Throwable t) {
                }
         });
    }
}
