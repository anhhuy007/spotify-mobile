package com.example.spotifyclone.shared.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.spotifyclone.R;
import com.example.spotifyclone.shared.utils.NetworkStatusLiveData;

import javax.annotation.Nullable;

public abstract class BaseOnlineFragment extends Fragment {
    private NetworkStatusLiveData networkStatusLiveData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkStatusLiveData = new NetworkStatusLiveData(requireContext());

        networkStatusLiveData.observe(this, isConnected -> {
            if (!isConnected) {
                // Handle offline state
                onOffline();
            }
        });
    }

    protected void onOffline() {
        Log.d("BaseOnlineFragment", "Offline state detected --> navigating to offline fragment");
//        NavHostFragment.findNavController(this).navigate(R.id.action_global_offlineFragment);
    }
}
