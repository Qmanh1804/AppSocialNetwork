package com.example.Backend.Service.Story;

import com.example.Backend.Entity.Story;
import com.example.Backend.Request.Story.RequestStory;
import com.example.Backend.Request.Story.RequestStoryByUserId;
import com.example.Backend.Response.ApiResponse.ApiResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoryIml implements StoryService{

    @Autowired
    private MongoTemplate mongoTemplate;

    public StoryIml() {
    }

    //create story
    @Override
    public void createStory(RequestStory requestStory, String userId) {
        //
        ObjectId objectId = new ObjectId(userId);
        Story story = new Story();
        story.setUserId(objectId);
        story.setContentMedia(requestStory.getContentMedia());
        story.setCreatedAt(requestStory.getCreatedAt());
        mongoTemplate.insert(story,"stories");
    }

    // get list story by userId
    @Override
    public ApiResponse<List<RequestStoryByUserId>> getListStoryByUserId(String userId) {
        ObjectId objectId = new ObjectId(userId);
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("stories")
                .localField("_id")
                .foreignField("userId")
                .as("listStory");

        MatchOperation matchOperation = Aggregation.match(Criteria.where("_id").is(objectId));

        AggregationOperation unwindOperation = Aggregation.unwind("listStory");

        AggregationOperation projectOperation = Aggregation.project()
                .andExpression("_id").as("userId")
                .andExpression("username").as("userName")
                .andExpression("avatarImg").as("avtUser")
                .andExpression("listStory._id").as("idStory")
                .andExpression("listStory.contentMedia").as("contentMedia")
                .andExpression("listStory.createdAt").as("createdAt");

        Aggregation aggregation = Aggregation.newAggregation(lookupOperation, matchOperation, unwindOperation, projectOperation);

        List<RequestStoryByUserId> list = mongoTemplate.aggregate(aggregation, "users", RequestStoryByUserId.class).getMappedResults();
        return new ApiResponse<List<RequestStoryByUserId>>(true, "", list);
    }


}
