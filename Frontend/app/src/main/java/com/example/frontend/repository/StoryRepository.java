package com.example.frontend.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.frontend.request.Story.RequestCreateStory;
import com.example.frontend.request.Story.RequestStoryByUserId;
import com.example.frontend.response.ApiResponse.ApiResponse;
import com.example.frontend.service.StoryService;
import com.example.frontend.utils.CallApi;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryRepository {
    StoryService storyService;

    public StoryRepository() {
        storyService = CallApi.getRetrofitInstance().create(StoryService.class);
    }

    // create post
    public void createStory(RequestCreateStory request, String userId) {
        MutableLiveData<ApiResponse<String>> mutableLiveData = new MutableLiveData<>();

        storyService.createStory(request,userId).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<String> apiResponse = response.body();
                    mutableLiveData.setValue(apiResponse);
                } else {
                    // Xử lý khi phản hồi không thành công
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                // Xử lý khi gọi API thất bại
            }
        });
    }

    // get list post
    public MutableLiveData<ApiResponse<List<RequestStoryByUserId>>> getListStoryByUserId(String userId) {
        MutableLiveData<ApiResponse<List<RequestStoryByUserId>>> mutableLiveData = new MutableLiveData<>();
        storyService.getListStoryByUserId(userId).enqueue(new Callback<ApiResponse<List<RequestStoryByUserId>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<RequestStoryByUserId>>> call, Response<ApiResponse<List<RequestStoryByUserId>>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<List<RequestStoryByUserId>> apiResponse = response.body();
                    Gson gson = new Gson();
                    String json = gson.toJson(apiResponse);
                    Log.d("err", json);
                    mutableLiveData.setValue(apiResponse);
                } else {
                    // Xử lý khi phản hồi không thành công
                    mutableLiveData.setValue(new ApiResponse<List<RequestStoryByUserId>>(false, "Failed to get data from server", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<RequestStoryByUserId>>> call, Throwable t) {
                // Xử lý khi gọi API thất bại
                mutableLiveData.setValue(new ApiResponse<List<RequestStoryByUserId>>(false, "Error: " + t.getMessage(), null));
            }
        });
        return mutableLiveData;
    }
}
