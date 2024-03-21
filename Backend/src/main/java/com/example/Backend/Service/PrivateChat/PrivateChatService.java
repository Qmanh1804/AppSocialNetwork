package com.example.Backend.Service.PrivateChat;

import com.example.Backend.Entity.Message;
import com.example.Backend.Entity.PrivateChat;
import com.example.Backend.Request.PrivateChat.RequestCreatePrivateChat;
import com.example.Backend.Response.ApiResponse.PrivateChatResponse.PrivateChatResponse;
import com.example.Backend.Response.ApiResponse.PrivateChatResponse.PrivateChatWithMessagesResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PrivateChatService {
    PrivateChatResponse createPrivateChat(RequestCreatePrivateChat requestCreatePrivateChat);
    PrivateChatWithMessagesResponse getMessagesByPrivateChatId(String id);
}