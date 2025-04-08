package com.example.spotifyclone.features.playlist.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.playlist.adapter.ViewPagerAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class AddPlaylistBottomSheet extends BottomSheetDialogFragment {

    private List<Song> list1;
    private List<Song> list2;

    public AddPlaylistBottomSheet(List<Song> list1, List<Song> list2) {
        this.list1 = list1;
        this.list2 = list2;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_playlist_detail_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        List<List<Song>> songLists = new ArrayList<>();
        songLists.add(list1);
        songLists.add(list2);

//        ViewPagerAdapter adapter = new ViewPagerAdapter(requireContext(), 2, songLists);
//        viewPager.setAdapter(adapter);
    }
}
