package com.example.spotifyclone;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

public class AppLifecycleHandler implements Application.ActivityLifecycleCallbacks {
    private int activityCount = 0;
    private final SpotifyCloneApplication app;

    public AppLifecycleHandler(SpotifyCloneApplication app) {
        this.app = app;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        activityCount++;
        Log.d("AppLifecycleHandler", "App is in foreground");
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        activityCount--;
        if (activityCount == 0) {
            Log.d("AppLifecycleHandler", "App is in background or closing");
            app.getMusicPlayerController().close();
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {}

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity,@NonNull  Bundle outState) {}
}
