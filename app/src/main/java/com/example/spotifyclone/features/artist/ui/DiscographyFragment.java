package com.example.spotifyclone.features.artist.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.artist.adapter.ItemDiscographyAlbumAdapter;
import com.example.spotifyclone.features.artist.adapter.ItemDiscographyEPAdapter;
import com.example.spotifyclone.features.artist.viewModel.ListDiscographyAlbumViewModel;
import com.example.spotifyclone.features.artist.viewModel.ListDiscographyEPViewModel;

public class DiscographyFragment extends Fragment {

    private RecyclerView rvAlbums, rvEPs, rvCollection, rvHave;
    private Context context;
    private ImageButton btnBackDiscography;
    private String artistId;

    public static DiscographyFragment newInstance(String artistId) {
        DiscographyFragment fragment = new DiscographyFragment();
        Bundle args = new Bundle();
        args.putString("ARTIST_ID", artistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistId = getArguments().getString("ARTIST_ID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discography, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = requireContext();

        if (artistId == null || artistId.isEmpty()) {
            Toast.makeText(context, "Invalid Artist ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize RecyclerViews
        rvAlbums = view.findViewById(R.id.rvAlbums);
        rvEPs = view.findViewById(R.id.rvEPs);
        rvCollection = view.findViewById(R.id.rvCollection);
        rvHave = view.findViewById(R.id.rvHave);
        btnBackDiscography = view.findViewById(R.id.btnBackDiscography);

        // Set layout managers
        rvAlbums.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvEPs.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvCollection.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvHave.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        btnBackDiscography.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        //rvAlbum
        ListDiscographyAlbumViewModel listDiscographyViewModelAlbum = new ViewModelProvider(this,
                new ListDiscographyAlbumViewModel.Factory(requireActivity().getApplication(), artistId, 1))
                .get(ListDiscographyAlbumViewModel.class);
        listDiscographyViewModelAlbum.getListDiscography().observe(getViewLifecycleOwner(), item -> {
            if (item != null) {
                ItemDiscographyAlbumAdapter rvPopularSongsAdapter = new ItemDiscographyAlbumAdapter(context, item);
                rvAlbums.setAdapter(rvPopularSongsAdapter);
            }
            ViewGroup.LayoutParams params = rvAlbums.getLayoutParams();
            params.height = (int) (116 * requireContext().getResources().getDisplayMetrics().density * item.size());
            rvAlbums.setLayoutParams(params);
        });
        listDiscographyViewModelAlbum.fetchItems();

        //rvEP
        ListDiscographyEPViewModel listDiscographyViewModelEP = new ViewModelProvider(this,
                new ListDiscographyEPViewModel.Factory(requireActivity().getApplication(), artistId, 1))
                .get(ListDiscographyEPViewModel.class);
        listDiscographyViewModelEP.getListDiscography().observe(getViewLifecycleOwner(), item -> {
            if (item != null) {
                ItemDiscographyEPAdapter rvPopularSongsAdapter = new ItemDiscographyEPAdapter(context, item);
                rvEPs.setAdapter(rvPopularSongsAdapter);
            }
            ViewGroup.LayoutParams params = rvEPs.getLayoutParams();
            params.height = (int) (116 * requireContext().getResources().getDisplayMetrics().density * item.size());
            rvEPs.setLayoutParams(params);
        });
        listDiscographyViewModelEP.fetchItems();

        //rvCollection
        ListDiscographyAlbumViewModel listDiscographyViewModelCollection = new ViewModelProvider(this,
                new ListDiscographyAlbumViewModel.Factory(requireActivity().getApplication(), artistId, 3))
                .get(ListDiscographyAlbumViewModel.class);
        listDiscographyViewModelCollection.getListDiscography().observe(getViewLifecycleOwner(), item -> {
            if (item != null) {
                ItemDiscographyAlbumAdapter rvPopularSongsAdapter = new ItemDiscographyAlbumAdapter(context, item);
                rvCollection.setAdapter(rvPopularSongsAdapter);
            }
            ViewGroup.LayoutParams params = rvCollection.getLayoutParams();
            params.height = (int) (116 * requireContext().getResources().getDisplayMetrics().density * item.size());
            rvCollection.setLayoutParams(params);
        });
        listDiscographyViewModelCollection.fetchItems();

        //rvHave
        ListDiscographyEPViewModel listDiscographyViewModelHave = new ViewModelProvider(this,
                new ListDiscographyEPViewModel.Factory(requireActivity().getApplication(), artistId, 1))
                .get(ListDiscographyEPViewModel.class);
        listDiscographyViewModelHave.getListDiscography().observe(getViewLifecycleOwner(), item -> {
            if (item != null) {
                ItemDiscographyEPAdapter rvPopularSongsAdapter = new ItemDiscographyEPAdapter(context, item);
                rvHave.setAdapter(rvPopularSongsAdapter);
            }
            ViewGroup.LayoutParams params = rvHave.getLayoutParams();
            params.height = (int) (116 * requireContext().getResources().getDisplayMetrics().density * item.size());
            rvHave.setLayoutParams(params);
        });
        listDiscographyViewModelHave.fetchItems();

        setupTabListeners(view);
    }

    private void setupTabListeners(View view) {
        view.findViewById(R.id.tabAlbum).setOnClickListener(v -> {
            // Scroll to album section
            scrollToView(rvAlbums);
        });

        view.findViewById(R.id.tabEP).setOnClickListener(v -> {
            // Scroll to EP section
            scrollToView(rvEPs);
        });

        view.findViewById(R.id.tabCollection).setOnClickListener(v -> {
            // Scroll to collection section
            scrollToView(rvCollection);
        });

        view.findViewById(R.id.tabHave).setOnClickListener(v -> {
            // Scroll to have section
            scrollToView(rvHave);
        });
    }

    private void scrollToView(View view) {
        // Find NestedScrollView
        androidx.core.widget.NestedScrollView scrollView = view.getParent().getParent() instanceof androidx.core.widget.NestedScrollView ?
                (androidx.core.widget.NestedScrollView) view.getParent().getParent() : null;

        if (scrollView != null) {
            // Find the title element (TextView) before RecyclerView
            ViewGroup parentLayout = (ViewGroup) view.getParent();
            int index = parentLayout.indexOfChild(view);

            // Assuming the title TextView is always before RecyclerView
            if (index > 0) {
                View header = parentLayout.getChildAt(index - 1);
                // Scroll to the title position
                scrollView.post(() -> scrollView.smoothScrollTo(0, header.getTop()));
            } else {
                // If title not found, scroll to RecyclerView
                scrollView.post(() -> scrollView.smoothScrollTo(0, view.getTop()));
            }
        }
    }
}