package com.example.spotifyclone.features.chatbot.ui;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.example.spotifyclone.features.chatbot.model.ChatMessage;
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
    private List<ChatMessage> messages;
    private String ask;
    private static final int REQUEST_CODE_SPEECH_INPUT = 101;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        messages = new ArrayList<>();

        initViews(view);
        setupViewModel();
        setupUI();
        setupRecyclerView(view);

        // Mở bàn phím
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.chatRecyclerView);
        editText = view.findViewById(R.id.inputEditText);
        sendButton = view.findViewById(R.id.sendButton);
        animationView = view.findViewById(R.id.animationView);
        micButton = view.findViewById(R.id.micButton);
        appBarLayout = view.findViewById(R.id.appBarLayout);
        topAppBar = view.findViewById(R.id.topAppBar);
    }

    private void setupViewModel() {
        chatBotViewModel = new ViewModelProvider(
                requireActivity(),
                new ChatbotViewmodelFactory(requireContext())).get(ChatBotViewModel.class);

        chatBotViewModel.getResponseLiveData().observe(getViewLifecycleOwner(), response -> {
            if (ask != null && !ask.isEmpty() && response != null && !response.isEmpty()) {
                // show typing animation
                addMessage("Đang suy nghĩ...", ChatMessage.TYPE_TYPING);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    // remove typing animation
                    messages.remove(messages.size() - 1);

                    // add AI message
                    addMessage(response, ChatMessage.TYPE_BOT);

                    // scroll to the last message
                    chatAdapter.setData(messages);
                    recyclerView.scrollToPosition(messages.size() - 1);
                }, 1500); // ⏱ Delay 1.5 giây
            }
        });

    }

    private void setupUI() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                NavDirections action = ChatbotFragmentDirections.actionChatbotFragmentToNavHome();
                Navigation.findNavController(view).navigate(action);
            }
        });
    }

    private void sendMessage() {
        animationView.setVisibility(View.GONE); // Ẩn animation nếu có
        ask = editText.getText().toString().trim();
        if (!ask.isEmpty()) {
            // create user message
            addMessage(ask, ChatMessage.TYPE_USER);
            recyclerView.scrollToPosition(messages.size() - 1); // Scroll xuống cuối sau khi gửi
            chatBotViewModel.fetchChatbotResponse(ask); // Gọi API
            editText.setText(""); // Xóa nội dung
        }

    }

    private void setupRecyclerView(View view) {
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

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    updateEditTextWithAnimation(spokenText);
                    sendMessage();
                }, 500);
            }
        }
    }

    // Hiệu ứng fade-in khi cập nhật EditText
    private void updateEditTextWithAnimation(String text) {
        editText.setAlpha(0f);
        editText.setText(text);
        editText.setSelection(text.length());

        editText.animate()
                .alpha(1f)
                .setDuration(300)
                .start();
    }

    private void addMessage(String message, int type) {
        ChatMessage chatMessage = new ChatMessage(message, type);
        messages.add(chatMessage);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }
}
