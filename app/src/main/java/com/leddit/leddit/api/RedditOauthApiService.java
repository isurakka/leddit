package com.leddit.leddit.api;

import com.leddit.leddit.api.output.MyRedditKarma;
import com.leddit.leddit.api.output.RedditObject;
import com.leddit.leddit.api.output.RedditProfile;
import com.leddit.leddit.api.output.VoteResponse;

import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by Jonah on 6.12.2014.
 */
public interface RedditOauthApiService
{
    @GET("/api/v1/me/.json")
    RedditProfile me();

    @GET("/api/v1/me/karma/.json")
    MyRedditKarma myKarma();

    @POST("/api/vote/.json")
    VoteResponse vote(@Field("dir") int direction, @Field("id") String fullname);
}
