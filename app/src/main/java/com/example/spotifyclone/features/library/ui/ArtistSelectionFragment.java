package com.example.spotifyclone.features.library.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.spotifyclone.features.library.viewModel.LibraryArtistsViewModel;
import com.example.spotifyclone.features.library.viewModel.LibraryPlaylistsViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.features.library.adapter.ArtistSelectionAdapter;
import com.example.spotifyclone.features.library.model.SelectableArtist;
import com.example.spotifyclone.features.library.viewModel.ArtistSelectionViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ArtistSelectionFragment extends BottomSheetDialogFragment implements ArtistSelectionAdapter.OnArtistClickListener {

    private RecyclerView artistsRecyclerView;
    private MaterialButton doneButton;
    private View rootView;
    private ArtistSelectionAdapter adapter;
    private ArtistSelectionViewModel viewModel;
    private List<SelectableArtist> selectedArtists = new ArrayList<>();

    private LibraryArtistsViewModel playlistViewModel;


    public static ArtistSelectionFragment newInstance() {
        return new ArtistSelectionFragment();
    }

    public void setViewModel(LibraryArtistsViewModel viewModel) {
        this.playlistViewModel = viewModel;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setSkipCollapsed(true);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                // Set the height to match the screen height
                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                bottomSheet.setLayoutParams(layoutParams);
            }
        });
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_artist_selection, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this,
                new ArtistSelectionViewModel.Factory(requireActivity().getApplication()))
                .get(ArtistSelectionViewModel.class);
        viewModel.setSubViewModel(playlistViewModel);

        // Initialize views
        artistsRecyclerView = view.findViewById(R.id.artistsRecyclerView);
        doneButton = view.findViewById(R.id.doneButton);

        AuthRepository authRepository = new AuthRepository(requireContext());
        String currentUserId = authRepository.getUser().getId();
        // Setup RecyclerView
        artistsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        // Set up observers
        viewModel.getArtistsList().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) {
                adapter = new ArtistSelectionAdapter(
                        requireContext(),
                        artists,
                        this,
                        viewModel,
                        currentUserId
                );
                artistsRecyclerView.setAdapter(adapter);
            }
        });

        // Setup click listeners
        doneButton.setOnClickListener(v -> onDoneButtonClicked());

        // Fetch artists data
        viewModel.fetchSuggestedArtists();
    }

    private void onDoneButtonClicked() {
        dismiss(); // Use dismiss() instead of navigating back
    }

    @Override
    public void onArtistClick(SelectableArtist artist, int position) {
        if (artist.isFollowed()) {
            selectedArtists.add(artist);
        } else {
            // Remove from selected artists
            for (int i = 0; i < selectedArtists.size(); i++) {
                if (selectedArtists.get(i).getId().equals(artist.getId())) {
                    selectedArtists.remove(i);
                    break;
                }
            }
        }

        // Update UI if needed
        adapter.updateArtistFollowStatus(position, artist.isFollowed());
    }
}