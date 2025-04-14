package com.example.spotifyclone;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

public class AppLifecycleHandler implements Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {
    private int activityCount = 0;
    private final SpotifyCloneApplication app;

    public AppLifecycleHandler(SpotifyCloneApplication app) {
        this.app = app;
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        activityCount++;
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        activityCount--;
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {}

    @Override
    public void onActivityResumed(@NonNull Activity activity) {}

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {}

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        app.getMusicPlayerController().close();
    }
}
