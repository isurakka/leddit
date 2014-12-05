package com.leddit.leddit.api;

import com.leddit.leddit.api.output.RedditAuthorizationAllowAccessData;
import com.leddit.leddit.api.output.RedditAuthorizationData;
import com.leddit.leddit.api.output.RedditLoginData;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.QueryMap;

/**
 * Created by Jonah on 5.12.2014.
 */
public interface RedditAuthorizationService
{
    @GET("/api/v1/authorize/")
    RedditAuthorizationData authorize(@QueryMap Map<String, String> authOptions);

    @POST("/api/v1/authorize/")
    RedditAuthorizationAllowAccessData allowAccess(@QueryMap Map<String, String> authOptions);
}
