package com.example.spotifyclone.features.library.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.chatbot.adapter.ChatAdapter;
import com.example.spotifyclone.features.chatbot.network.ChatbotService;
import com.example.spotifyclone.features.chatbot.viewmodel.ChatBotViewModel;
import com.example.spotifyclone.features.chatbot.viewmodel.ChatbotViewmodelFactory;
import com.example.spotifyclone.features.follow.model.FollowCountResponse;
import com.example.spotifyclone.features.follow.viewModel.FollowedArtistsCountViewModel;
import com.example.spotifyclone.features.playlist.adapter.ProfilePlaylistAdapter;
import com.example.spotifyclone.features.playlist.model.Playlist;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModel;
import com.example.spotifyclone.features.playlist.viewmodel.PlaylistViewModelFactory;
import com.example.spotifyclone.features.profile.ui.EditProfileFragment;
import com.example.spotifyclone.features.profile.ui.ProfileFragmentDirections;
import com.example.spotifyclone.features.profile.viewmodel.ProfileViewModel;
import com.example.spotifyclone.shared.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends Fragment {

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    private PlaylistViewModel playlistViewModel;
//    private ProfilePlaylistAdapter playlistAdapter;
//    private RecyclerView playlistRecyclerView; //
//
//
//    public LibraryFragment() {
//        // Required empty public constructor
//    }
//
//
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment SettingFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static LibraryFragment newInstance(String param1, String param2) {
//        LibraryFragment fragment = new LibraryFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_library, container, false);
//    }
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        initializeViews(view);
//        setupObservers();
//        setupRecyclerView(view);
//
//    }
//
//    private void initializeViews(View view) {
//
//
//        playlistRecyclerView=view.findViewById(R.id.playlist_recycler_view);
//
//
//    }
//
//    private void setupObservers() {
//        // Initialize ViewModels
//
//        // Playlist Viewmodel
//        playlistViewModel = new ViewModelProvider(
//                this,
//                new PlaylistViewModelFactory(requireContext())
//        ).get(PlaylistViewModel.class);
//
//        playlistViewModel.fetchPlaylists();
//        playlistViewModel.getUserPlaylist().observe(getViewLifecycleOwner(), playlists -> {
//            playlistAdapter.setData(playlists);
//        });
//
//    }
//
//
//
//
//    private void setupRecyclerView(View view) {
//        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//
//        playlistAdapter = new ProfilePlaylistAdapter(requireContext(), new ArrayList<>(), playlist -> {
//            // Chuyển đến
//            navigateToPlaylistDetail(playlist);
//        }, "currentUser.getUsername()"); // add song Id,
//
//        playlistRecyclerView.setAdapter(playlistAdapter);
//    }
//
//    private void navigateToPlaylistDetail(Playlist playlist){
//
//        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
//
//        LibraryFragmentDirections.ActionLibToPlaylistDetailFragment action=
//                LibraryFragmentDirections.actionLibToPlaylistDetailFragment(
//                        "currentUser.getUsername()",
//                        "currentUser.getAvatarUrl()",
//                        playlist.getId(),
//                        playlist.getName(),
//                        playlist.getCoverUrl()
//                );
//
//
////        Navigation.findNavController(requireView()).navigate(action);
//
//        navController.navigate(action);
//
//    }
//UI
    private RecyclerView recyclerView;
    private EditText editText;
    private Button sendButton;


    // logic
    private ChatBotViewModel chatBotViewModel;
    private ChatbotService chatbotService;
    private ChatAdapter chatAdapter;
    private List<String> messages;
    private String ask;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messages=new ArrayList<>();

        initViews(view);
        setupViewModel();
        setupUI();
        setupRecyclerView(view);

    }



    private void initViews(View view){
        recyclerView=view.findViewById(R.id.chatRecyclerView);
        editText=view.findViewById(R.id.inputEditText);
        sendButton=view.findViewById(R.id.sendButton);

    }
    private void setupViewModel() {
        chatBotViewModel = new ViewModelProvider(
                requireActivity(),
                new ChatbotViewmodelFactory(requireContext())).get(ChatBotViewModel.class);
//        chatBotViewModel.fetchChatbotResponse(ask);
        chatBotViewModel.getResponseLiveData().observe(getViewLifecycleOwner(), response -> {
            if(!ask.isEmpty()&&!response.isEmpty())
            {
                messages.add(response);
                chatAdapter.setData(messages);

            }

        });
    }

    private void setupUI() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ask = editText.getText().toString();
                messages.add(ask);
                chatBotViewModel.fetchChatbotResponse(ask);
//                String answer=chatBotViewModel.getResponseLiveData();
//                messages.add(answer);

//                Log.d("Chatbot", "Onclick"+ask+answer);
//                chatAdapter.setData(messages);
            }
        });


    }

    private  void setupRecyclerView(View view){
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        chatAdapter = new ChatAdapter(messages);


        recyclerView.setAdapter(chatAdapter);
    }








}