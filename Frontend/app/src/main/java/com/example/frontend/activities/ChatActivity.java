package com.example.frontend.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.adapter.PrivateChatAdapter;
import com.example.frontend.fragments.Fragment_search_ClickAccount;
import com.example.frontend.fragments.SettingFragment;
import com.example.frontend.request.PrivateChat.RequestPrivateChat;
import com.example.frontend.response.ApiResponse.ApiResponse;
import com.example.frontend.response.Message.MessageWithSenderInfo;
import com.example.frontend.response.PrivateChat.PrivateChatResponse;
import com.example.frontend.response.PrivateChat.PrivateChatWithMessagesResponse;
import com.example.frontend.utils.PusherClient;
import com.example.frontend.utils.SharedPreferenceLocal;
import com.example.frontend.viewModel.Message.MessageViewModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pusher.client.Pusher;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity     {
    private TextView username_recipient;
    private ImageButton btn_back_main_chat;
    private CircleImageView imgAvatar;
    private ImageButton btnSend;
    private Pusher pusher;
    private RecyclerView recyclerView;
    private PrivateChatAdapter adapter;
    private MessageViewModel messageViewModel;
    private EditText inputMessage;
    private String recipientUserId;
    private String recipientAvatar;
    private String recipientId;
    private String userId;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        context = this;
        recipientUserId = getIntent().getStringExtra("recipientUserId");
        recipientAvatar = getIntent().getStringExtra("recipientAvater");
        recipientId = getIntent().getStringExtra("recipientID");
        userId = SharedPreferenceLocal.read(getApplicationContext(), "userId");


        username_recipient = findViewById(R.id.other_username);
        btn_back_main_chat = findViewById(R.id.back_btn);
        imgAvatar = findViewById(R.id.imgAvatar);
        recyclerView = findViewById(R.id.chat_recycler_view);
        btnSend = findViewById(R.id.message_send_btn);
        inputMessage = findViewById(R.id.chat_message_input);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PrivateChatAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);


        username_recipient.setText(recipientUserId);
        if (recipientAvatar != null) {
            Glide.with(getApplicationContext())
                    .load(Uri.parse(recipientAvatar))
                    .into(imgAvatar);
        }else {
            Glide.with(getApplicationContext())
                    .load(R.drawable.baseline_account_circle_24) // Ảnh mặc định trong thư mục drawable
                    .into(imgAvatar);
        }
        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        btn_back_main_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Bundle để chứa ID của người nhận
                Intent intent = new Intent(getApplicationContext(), FragmentReplacerActivity.class);
                // Thêm dữ liệu cho Intent để FragmentReplacerActivity biết cần thay thế fragment nào
                intent.putExtra("fragment_to_load", "Profile_recipetn");
                intent.putExtra("userId", recipientId);
                // Bắt đầu activity với Intent đã tạo
                startActivity(intent);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage();
            }
        });

        loadInitialMessages();
    }

    private void loadInitialMessages() {
        messageViewModel.getMessagesByPrivate(userId, recipientId).observe(this, new Observer<ApiResponse<PrivateChatWithMessagesResponse>>() {
            @Override
            public void onChanged(ApiResponse<PrivateChatWithMessagesResponse> response) {
                if (response != null && response.isSuccess()) {
                    PrivateChatWithMessagesResponse privateChatResponse = response.getData();
                    List<MessageWithSenderInfo> messages = privateChatResponse.getMessages();
                    if (messages != null && !messages.isEmpty()) {
                        adapter.setListMessage(messages);
                    }
                } else {
                    String errorMessage = "Failed to load initial messages";
                    if (response != null && response.getMessage() != null) {
                        errorMessage += ": " + response.getMessage();
                    }
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
//    private void sendMessage() {
//        String message = inputMessage.getText().toString();
//        RequestPrivateChat requestPrivateChat = new RequestPrivateChat(userId, recipientId, message);
//        messageViewModel.sendMessage(requestPrivateChat).observe(this, new Observer<ApiResponse<PrivateChatWithMessagesResponse>>() {
//            @Override
//            public void onChanged(ApiResponse<PrivateChatWithMessagesResponse> response) {
//                if (response != null && response.isSuccess()) {
//                    inputMessage.setText(null);
//                    pusher = PusherClient.init();
//                    pusher.connect();
//                    pusher.subscribe("newmess")
//                            .bind("send", (channelName, eventName, data) -> {
//                                try {
//                                    Gson gson = new GsonBuilder()
//                                            .setDateFormat("MMM dd, yyyy, hh:mm:ss a")
//                                            .create();
//                                    String jsonData = data.toString();
//                                    PrivateChatWithMessagesResponse privateChatResponse = gson.fromJson(jsonData, PrivateChatWithMessagesResponse.class);
//                                    List<MessageWithSenderInfo> messages = privateChatResponse.getMessages();
//                                    if (messages != null && !messages.isEmpty()) {
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//
//                                                adapter.setListMessage(messages);
//
//
//
//                                            }
//                                        });
//                                    }
//                                } catch (Exception e) {
//                                    Log.d("trycatch", new Gson().toJson(e));
//                                }
//                            });
//                } else {
//                    // Xử lý khi gửi tin nhắn không thành công
//                    String errorMessage = "Request failed";
//                    if (response != null && response.getMessage() != null) {
//                        errorMessage += ": " + response.getMessage();
//                    }
//                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//    }
private void sendMessage() {
    String message = inputMessage.getText().toString();
    RequestPrivateChat requestPrivateChat = new RequestPrivateChat(userId, recipientId, message);
    messageViewModel.sendMessage(requestPrivateChat).observe(this, new Observer<ApiResponse<PrivateChatWithMessagesResponse>>() {
        @Override
        public void onChanged(ApiResponse<PrivateChatWithMessagesResponse> response) {
            if (response != null && response.isSuccess()) {
                inputMessage.setText(null);
                pusher = PusherClient.init();
                pusher.connect();
                pusher.subscribe("newmess")
                        .bind("send", (channelName, eventName, data) -> {
                            try {
                                Gson gson = new GsonBuilder()
                                        .setDateFormat("MMM dd, yyyy, hh:mm:ss a")
                                        .create();
                                String jsonData = data.toString();
                                PrivateChatWithMessagesResponse privateChatResponse = gson.fromJson(jsonData, PrivateChatWithMessagesResponse.class);
                                List<MessageWithSenderInfo> messages = privateChatResponse.getMessages();
                                if (messages != null && !messages.isEmpty()) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.setListMessage(messages);
                                            // Gửi Broadcast Intent với dữ liệu tin nhắn mới
                                            Intent intent = new Intent("NEW_MESSAGE_ACTION");
                                            Gson gson = new Gson();
                                            String messagesJson = gson.toJson(messages); // Chuyển đổi danh sách tin nhắn thành chuỗi JSON
                                            intent.putExtra("new_message", messagesJson); // Đặt chuỗi JSON vào Intent
                                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                Log.d("trycatch", new Gson().toJson(e));
                            }
                        });
            } else {
                // Xử lý khi gửi tin nhắn không thành công
                String errorMessage = "Request failed";
                if (response != null && response.getMessage() != null) {
                    errorMessage += ": " + response.getMessage();
                }
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    });
}


}