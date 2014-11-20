package com.leddit.leddit.api;

import com.leddit.leddit.api.output.RedditObject;

import java.util.List;

import retrofit.http.*;


public interface RedditApiService {

    @GET("/r/{subreddit}/{orderby}/.json")
    RedditObject listSubreddit(@Path("subreddit") String subreddit, @Path("orderby") String orderBy);

}
