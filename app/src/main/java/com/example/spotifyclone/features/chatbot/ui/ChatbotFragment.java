package com.example.spotifyclone.features.chatbot.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.spotifyclone.R;
import com.example.spotifyclone.features.chatbot.adapter.ChatAdapter;
import com.example.spotifyclone.features.chatbot.network.ChatbotService;
import com.example.spotifyclone.features.chatbot.viewmodel.ChatBotViewModel;
import com.example.spotifyclone.features.chatbot.viewmodel.ChatbotViewmodelFactory;
import com.example.spotifyclone.features.search.ui.SearchSuggestFragmentDirections;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatbotFragment extends Fragment {

    //UI
    private RecyclerView recyclerView;
    private EditText editText;
    private ImageButton sendButton;
    private LottieAnimationView animationView;
    private ImageView micButton;
    private AppBarLayout appBarLayout;

    private MaterialToolbar topAppBar;




    private ImageView gifView;
    // logic
    private ChatBotViewModel chatBotViewModel;
    private ChatbotService chatbotService;
    private ChatAdapter chatAdapter;
    private List<String> messages;
    private String ask;

    private static final int REQUEST_CODE_SPEECH_INPUT = 101;






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

        editText.requestFocus(); // Đặt focus vào EditText
        // Mở bàn phím
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

    }

    private void initViews(View view){
        recyclerView=view.findViewById(R.id.chatRecyclerView);
        editText=view.findViewById(R.id.inputEditText);
        sendButton=view.findViewById(R.id.sendButton);
        animationView=view.findViewById(R.id.animationView);
        micButton=view.findViewById(R.id.micButton);
        appBarLayout = view.findViewById(R.id.appBarLayout);
        topAppBar = view.findViewById(R.id.topAppBar);



    }
    private void setupViewModel() {
        chatBotViewModel = new ViewModelProvider(
                requireActivity(),
                new ChatbotViewmodelFactory(requireContext())).get(ChatBotViewModel.class);
//        chatBotViewModel.fetchChatbotResponse(ask);
        chatBotViewModel.getResponseLiveData().observe(getViewLifecycleOwner(), response -> {
            if (ask != null && !ask.isEmpty() && response != null && !response.isEmpty()) {
                messages.add(response);
                chatAdapter.setData(messages);
                recyclerView.scrollToPosition(messages.size() - 1);
                // Nếu đủ số lượng tin nhắn thì hiện AppBar
                if (messages.size() > 4 && appBarLayout.getVisibility() != View.VISIBLE) {
                    appBarLayout.setVisibility(View.VISIBLE);
                    appBarLayout.setAlpha(0f);
                    appBarLayout.animate().alpha(1f).setDuration(300).start();
                }
            }
        });

    }

    private void setupUI() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.scrollToPosition(messages.size() - 1);
                sendMessage();
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    sendMessage(); // Gọi hàm gửi
                    return true; // Đã xử lý Enter
                }
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    micButton.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.GONE);
                } else {
                    sendButton.setVisibility(View.VISIBLE);
                    micButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần xử lý
            }
        });

        micButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hãy nói điều bạn muốn tìm...");

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireContext(), "Thiết bị không hỗ trợ giọng nói", Toast.LENGTH_SHORT).show();
            }
        });


        topAppBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action=ChatbotFragmentDirections.actionChatbotFragmentToNavHome();
                Navigation.findNavController(view).navigate(action);

            }
        });


    }

    private void sendMessage() {
        animationView.setVisibility(View.GONE); // Ẩn animation nếu có
        ask = editText.getText().toString().trim();
        if (!ask.isEmpty()) {
            messages.add(ask);
            chatAdapter.setData(messages); // Update giao diện ngay
            chatBotViewModel.fetchChatbotResponse(ask); // Gọi API
            editText.setText(""); // Xóa nội dung
        }

    }

    private  void setupRecyclerView(View view){
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        chatAdapter = new ChatAdapter(messages);
        recyclerView.setAdapter(chatAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);
                Log.d("Chatbotfragment", "Spoken Text: " + spokenText);
                editText.setText(spokenText);
                editText.setSelection(spokenText.length()); // Đưa con trỏ về cuối chuỗi
            }
        }
    }

}
