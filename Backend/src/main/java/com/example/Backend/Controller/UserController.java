package com.example.Backend.Controller;

import com.example.Backend.Entity.Notification;
import com.example.Backend.Entity.model.NotificationOfUser;
import com.example.Backend.Entity.model.User;
import com.example.Backend.Request.User.*;
import com.example.Backend.Response.ApiResponse.ApiResponse;
import com.example.Backend.Service.User.UserService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/createAccount")
    public ResponseEntity<ApiResponse<String>> createAccount(@RequestBody RequestCreateAccount requestCreateAccount) throws Exception{
        userService.createAccount(requestCreateAccount);
        ApiResponse<String> apiResponse = new ApiResponse<String>(true, "Tạo tài khoản thành công","");
        return new ResponseEntity<ApiResponse<String>>(apiResponse,HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<User>> loginAccount(@RequestBody RequestLogin requestLogin) throws Exception {
        ApiResponse<User> user = userService.loginAccount(requestLogin);
        return new ResponseEntity<ApiResponse<User>>(user, HttpStatus.OK);
    }

    @GetMapping("/sendOTP")
    public ResponseEntity<ApiResponse<String>> sendOTP( @RequestParam String email) throws Exception {
        System.out.println(email);
        ApiResponse<String> apiResponse = userService.sendOtp(email);
        return new ResponseEntity<ApiResponse<String>>(apiResponse, HttpStatus.OK);
    }
    @GetMapping("/sendOTP_forgotpassword")
    public ResponseEntity<ApiResponse<String>> sendOTP_forgotpassword( @RequestParam String email) throws Exception {
        System.out.println(email);
        ApiResponse<String> apiResponse = userService.sendOTP_forgotpassword(email);
        return new ResponseEntity<ApiResponse<String>>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/allUsers")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() throws Exception {
        ApiResponse<List<User>> apiResponse = userService.getAllUsers();
        return new ResponseEntity<ApiResponse<List<User>>>(apiResponse, HttpStatus.OK);
    }
    @GetMapping("/allUsersByFollows")
    public ResponseEntity<ApiResponse<List<RequestGetAllUserByFollows>>> getAllUsersByFollows(@RequestParam String id) throws Exception {
        ApiResponse<List<RequestGetAllUserByFollows>> apiResponse = userService.getAllUserByFollows(id);
        ApiResponse<List<RequestGetAllUserByFollows>> errApiResponse = new ApiResponse<>();
        errApiResponse.setData(null);
        errApiResponse.setMessage("No data");
        errApiResponse.setStatus(false);
        if(apiResponse.getData().isEmpty())  return new ResponseEntity<ApiResponse<List<RequestGetAllUserByFollows>>>(errApiResponse, HttpStatus.NOT_FOUND);
        return new ResponseEntity<ApiResponse<List<RequestGetAllUserByFollows>>>(apiResponse, HttpStatus.OK);
    }
    @PostMapping("/changePW")
    public ResponseEntity<ApiResponse<User>> changePW(@RequestBody RequestForgetPass requestForgetPass) throws Exception {
        ApiResponse<User> user = userService.changePW(requestForgetPass);
        return new ResponseEntity<ApiResponse<User>>(user, HttpStatus.OK);
    }
    @PostMapping("/changePass")
    public ResponseEntity<ApiResponse<User>> changePass(@RequestBody RequestChangePasword requestChangePasword) throws Exception {
        ApiResponse<User> apiResponse = userService.changePassword(requestChangePasword);
        return new ResponseEntity<ApiResponse<User>>(apiResponse, HttpStatus.OK);
    }
    @GetMapping("/detailUserById")
    public ResponseEntity<ApiResponse<User>> getDetailUserById(@RequestParam String id ) throws Exception {
        User user = new User();
        user.setId((id));
        ApiResponse<User> apiResponse = userService.getDetailUserById(user);
        return new ResponseEntity<ApiResponse<User>>(apiResponse, HttpStatus.OK);
    }
    @PostMapping("/updateUser")
    public ResponseEntity<ApiResponse<User>> updateUser(@RequestBody RequestUpdateUser requestUpdateUser) throws Exception {
        ApiResponse<User> apiResponse = userService.updateUser(requestUpdateUser);
        return new ResponseEntity<ApiResponse<User>>(apiResponse, HttpStatus.OK);
    }
    @GetMapping("/getListUserName")
    public ResponseEntity<ApiResponse<List<String>>> getListUserName() throws Exception {
        ApiResponse<List<String>> apiResponse = userService.getListUserName();
        return new ResponseEntity<ApiResponse<List<String>>>(apiResponse, HttpStatus.OK);
    }
    @GetMapping("/getUser_privateChat")
    public ResponseEntity<ApiResponse<List<User>>> getUser_privatechat(@RequestParam String u) throws Exception {
        ApiResponse<List<User>> apiResponse = userService.findUser_privatechat(u);
        return new ResponseEntity<ApiResponse<List<User>>>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/addNotification")
    public ResponseEntity<ApiResponse<String>> addNotification(@RequestBody RequestNotification notification)throws Exception {
        userService.addNotification(notification);
        return new ResponseEntity<ApiResponse<String>>(new ApiResponse<String>(true, "Đã tạo thông báo",null), HttpStatus.OK);
    }

    @PostMapping("/updateTokenFCM")
    public ResponseEntity<ApiResponse<String>> updateTokenFCM(@RequestBody RequestUpdateTokenFCM updateTokenFCM)throws Exception {
        userService.updateTokenFCM(updateTokenFCM);
        return new ResponseEntity<ApiResponse<String>>(new ApiResponse<String>(true, "Đã tạo update tokenFCM",null), HttpStatus.OK);
    }

    @GetMapping("/getNotification")
    public ResponseEntity<ApiResponse<List<NotificationOfUser>>> getNotification(@RequestParam String id ) throws Exception {
        return new ResponseEntity<ApiResponse<List<NotificationOfUser>>>(new ApiResponse<>(true, "",userService.getNotificationById(id)), HttpStatus.OK);
    }

    @GetMapping("/getTokenFCM")
    public ResponseEntity<ApiResponse<String>> getTokenFCM(@RequestParam String id ) throws Exception {
        return new ResponseEntity<ApiResponse<String>>(new ApiResponse<>(true, "",userService.getTokenFCM(id)), HttpStatus.OK);
    }
}
