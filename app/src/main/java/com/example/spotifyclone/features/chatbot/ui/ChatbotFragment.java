package com.example.spotifyclone.features.chatbot.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.chatbot.adapter.ChatAdapter;
import com.example.spotifyclone.features.chatbot.network.ChatbotService;
import com.example.spotifyclone.features.chatbot.viewmodel.ChatBotViewModel;
import com.example.spotifyclone.features.chatbot.viewmodel.ChatbotViewmodelFactory;

import java.util.ArrayList;
import java.util.List;

public class ChatbotFragment extends Fragment {

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
            }
        });


    }

    private  void setupRecyclerView(View view){
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        chatAdapter = new ChatAdapter(messages);


        recyclerView.setAdapter(chatAdapter);
    }

}
