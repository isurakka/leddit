package com.leddit.leddit.api;

import com.leddit.leddit.api.output.RedditCommentObject;
import com.leddit.leddit.api.output.RedditLoginData;
import com.leddit.leddit.api.output.RedditObject;

import java.util.List;
import java.util.Map;

import retrofit.http.*;


public interface RedditApiService {

    @GET("/r/{subreddit}/{orderby}/.json")
    RedditObject listSubreddit(@Path("subreddit") String subreddit, @Path("orderby") String orderBy);

    @GET("/r/{subreddit}/comments/{article}/.json")
    List<RedditCommentObject> listThreadComments(@Path("subreddit") String subreddit, @Path("article") String article, @QueryMap Map<String, String> options);

    @POST("/api/login/.json")
    RedditLoginData login(@QueryMap Map<String, String> loginOptions);
}
