package com.example.Backend.Service.Story;

import com.example.Backend.Request.Story.RequestStory;
import com.example.Backend.Request.Story.RequestStoryByUserId;
import com.example.Backend.Response.ApiResponse.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StoryService {

    // create story
    void createStory(RequestStory requestStory, String userId);

    // get list story by userId
    ApiResponse<List<RequestStoryByUserId>> getListStoryByUserId(String userId);
}
