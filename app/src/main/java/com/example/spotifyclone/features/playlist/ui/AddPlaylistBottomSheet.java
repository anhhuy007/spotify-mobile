package com.example.spotifyclone.features.playlist.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.album.inter.OnSongMoreIconClickListener;
import com.example.spotifyclone.features.album.viewmodel.AlbumViewModel;
import com.example.spotifyclone.features.player.model.song.Song;
import com.example.spotifyclone.features.playlist.adapter.ViewPagerAdapter;
import com.example.spotifyclone.features.playlist.inter.OnSongClickListner;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModel;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class AddPlaylistBottomSheet extends BottomSheetDialogFragment {

    private String playlistId;
    private ViewPager2 viewPager;
    private List<Song> list1;
    private List<Song> list2;
    private boolean isList1Ready = false;
    private boolean isList2Ready = false;

    private PlaylistViewModel viewModelList1;
    private PlaylistViewModel viewModelList2;

    public AddPlaylistBottomSheet(){}

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

        viewPager = view.findViewById(R.id.viewPager);


        Indicator(view);


        // Using viewmodel
        AddPlaylistBottomSheetArgs args = AddPlaylistBottomSheetArgs.fromBundle(getArguments());
        playlistId=args.getPlaylistId();
        viewModelList1=new ViewModelProvider(
                requireActivity(),
                new PlaylistViewModelFactory(requireContext())).get(PlaylistViewModel.class);
        viewModelList2=new ViewModelProvider(
                requireActivity(),
                new PlaylistViewModelFactory(requireContext())).get(PlaylistViewModel.class);

        viewModelList1.fetchPopularSongs(playlistId);
        viewModelList2.fetchPopularSongs(playlistId);
        viewModelList1.getPopularSongs().observe(getViewLifecycleOwner(), songs -> {
            list1=songs;
            isList1Ready=true;
            tryInitAdapter();
        });

        viewModelList2.getPopularSongs().observe(getViewLifecycleOwner(), songs -> {
            list2=songs;
            isList2Ready=true;
            tryInitAdapter();
        });
    }
    private void tryInitAdapter() {
        if (isList1Ready && isList2Ready) {
            SetData(list1, list2);
        }
    }

    public void SetData(List<Song> list1, List<Song> list2){
        // set data to adapter
        List<List<Song>> songLists = new ArrayList<>();
        songLists.add(list1);
        songLists.add(list2);
        //

        ViewPagerAdapter adapter = new ViewPagerAdapter(songLists, 1, requireContext(), new OnSongClickListner() {
            @Override
            public void OnAddClickSong(Song song){
                new AlertDialog.Builder(requireContext())
                        .setTitle("Thêm bài hát vào Playlist")
                        .setMessage("Bạn có chắc muốn thêm không?")
                        .setPositiveButton("Thêm", (dialog, which) -> {
                            viewModelList1.addSongToPlaylist(playlistId, song.getId());
                        })
                        .setNegativeButton("Hủy", null)
                        .show();

            }
            public void OnRemoveClickSong(Song song){}
            public void OnPlaySong(Song song){} // for playing song

        });
        viewPager.setAdapter(adapter);


    }

    public void Indicator(View view){
        ImageView inv1 = view.findViewById(R.id.inv1);
        ImageView inv2 = view.findViewById(R.id.inv2);

        // Đặt trạng thái mặc định: indicator đầu tiên được chọn
        inv1.setImageResource(R.drawable.ic_indicator_active);
        inv2.setImageResource(R.drawable.ic_indicator_inactive);

        // Lắng nghe sự kiện thay đổi trang
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == 0) {
                    inv1.setImageResource(R.drawable.ic_indicator_active);
                    inv2.setImageResource(R.drawable.ic_indicator_inactive);
                } else if (position == 1) {
                    inv1.setImageResource(R.drawable.ic_indicator_inactive);
                    inv2.setImageResource(R.drawable.ic_indicator_active);
                }
            }
        });

    }
}
