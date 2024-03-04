package com.example.Backend.Service.User;

import com.example.Backend.Entity.model.User;
import com.example.Backend.Request.User.RequestCreateAccount;
import com.example.Backend.Request.User.RequestLogin;
import com.example.Backend.Response.ApiResponse.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void createAccount (RequestCreateAccount requestCreateAccount) throws Exception;
    ApiResponse<User> loginAccount (RequestLogin requestCreateLogin) throws Exception;
    String sendOtp(String email);
}