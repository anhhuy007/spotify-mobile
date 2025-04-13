package com.example.spotifyclone.shared.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class NetworkStatusLiveData extends LiveData<Boolean> {
    private final ConnectivityManager connectivityManager;
    private final ConnectivityManager.NetworkCallback networkCallback;
    public NetworkStatusLiveData(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = connectivityManager.getActiveNetwork() != null;
        postValue(isConnected);
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                postValue(true);
            }
            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                postValue(false);
            }
        };
    }
    @Override
    protected void onActive() {
        super.onActive();
        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }
    @Override
    protected void onInactive() {
        super.onInactive();
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }
}