package com.leddit.leddit.api;

import com.leddit.leddit.api.output.RedditAuthorizationAccessData;

import java.util.Map;

import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.QueryMap;

/**
 * Created by Jonah on 5.12.2014.
 */
public interface RedditAuthorizationService
{
    @POST("/api/v1/access_token/.json")
    AuthState authorize(@Header("Authorization") String auth, @QueryMap Map<String, String> authOptions);
}
