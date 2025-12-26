package com.tanmaybuilds.lnmtrack.Activities;

import android.os.Bundle;
import android.util.Log; // Logging ke liye zaroori import
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.tanmaybuilds.lnmtrack.R;
import com.tanmaybuilds.lnmtrack.Adapters.ChatAdapter;
import com.tanmaybuilds.lnmtrack.DataModels.ChatMessage;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import java.util.ArrayList;
import java.util.List;

public class AIChatActivity extends AppCompatActivity {
    // Unique TAG for Logcat filtering
    private static final String TAG = "LNM_AI_DEBUG";

    private GenerativeModelFutures model;
    private EditText messageInput;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aichat);
        Log.d(TAG, "AIChatActivity Created");

        // 1. Initialize Gemini Model (Note: "gemini-1.5-flash" use karein, 2.5 abhi beta mein ho sakta hai)
        try {
            GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", "AIzaSyBVa89jRYA43WaCxTcRQOIW1WzVoSWXr5Q");
            model = GenerativeModelFutures.from(gm);
            Log.d(TAG, "Gemini Model Initialized Successfully");
        } catch (Exception e) {
            Log.e(TAG, "Model Initialization Failed: " + e.getMessage());
        }

        // 2. Setup RecyclerView
        recyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(chatAdapter);

        // 3. Send Button Logic
        findViewById(R.id.sendBtn).setOnClickListener(v -> {
            String prompt = messageInput.getText().toString().trim();
            if (!prompt.isEmpty()) {
                Log.d(TAG, "User sent message: " + prompt);
                addToChat(prompt, ChatMessage.SENT_BY_USER);
                messageInput.setText("");
                askGemini(prompt);
            } else {
                Log.w(TAG, "Empty message attempt");
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(v -> {
            Log.d(TAG, "Back button pressed");
            finish();
        });
    }

    private void addToChat(String message, String sentBy) {
        runOnUiThread(() -> {
            messageList.add(new ChatMessage(message, sentBy));
            chatAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.smoothScrollToPosition(messageList.size() - 1);
        });
    }

    private void askGemini(String userPrompt) {
        Log.i(TAG, "Requesting Gemini for prompt: " + userPrompt);
        addToChat("Typing...", ChatMessage.SENT_BY_BOT);

        String fullPrompt = "You are LNM-AI, a helpful assistant for students of LNMIIT Jaipur. " +
                "Answer this query professionally and concisely: " + userPrompt;

        Content content = new Content.Builder().addText(fullPrompt).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String aiResponse = result.getText();
                Log.d(TAG, "Gemini Success. Response: " + aiResponse);

                runOnUiThread(() -> {
                    // Pehle "Typing..." wala index pakdo
                    int typingIndex = messageList.size() - 1;

                    if (typingIndex >= 0 && messageList.get(typingIndex).getMessage().equals("Typing...")) {
                        // 1. List se remove karo
                        messageList.remove(typingIndex);
                        // 2. Adapter ko batao ki item remove hua hai (Crash fix yahan hai!)
                        chatAdapter.notifyItemRemoved(typingIndex);
                        Log.d(TAG, "Typing indicator removed");
                    }

                    // 3. Ab real response add karo
                    addToChat(aiResponse, ChatMessage.SENT_BY_BOT);
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Log.e(TAG, "Gemini API Error: " + t.getMessage());
                runOnUiThread(() -> {
                    int typingIndex = messageList.size() - 1;
                    if (typingIndex >= 0) {
                        messageList.remove(typingIndex);
                        chatAdapter.notifyItemRemoved(typingIndex);
                    }
                    addToChat("Sorry, I'm having trouble connecting. Try again!", ChatMessage.SENT_BY_BOT);
                });
            }
        }, ContextCompat.getMainExecutor(this));
    }
}