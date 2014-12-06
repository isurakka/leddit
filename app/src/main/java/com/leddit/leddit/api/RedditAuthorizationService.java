package com.leddit.leddit.api;

import com.leddit.leddit.api.output.RedditAuthorizationAccessData;

import java.util.Map;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.QueryMap;

/**
 * Created by Jonah on 5.12.2014.
 */
public interface RedditAuthorizationService
{
    @FormUrlEncoded
    @POST("/api/v1/access_token")
    AuthState authorize(@Header("Authorization") String auth, @Field("redirect_uri") String redirect_uri,
                        @Field("code") String code,
                        @Field("grant_type") String grant_type);
}
