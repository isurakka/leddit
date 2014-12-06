package com.leddit.leddit.api;

import com.leddit.leddit.api.output.RedditObject;
import com.leddit.leddit.api.output.RedditProfile;

import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Jonah on 6.12.2014.
 */
public interface RedditOauthApiService
{
    @GET("/api/v1/me/.json")
    RedditProfile me();
}
