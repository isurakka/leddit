package com.leddit.leddit.api;

import com.leddit.leddit.api.output.CaptchaIdenResponse;
import com.leddit.leddit.api.output.CaptchaNeededResponse;
import com.leddit.leddit.api.output.MyRedditKarma;
import com.leddit.leddit.api.output.NewCaptchaResponse;
import com.leddit.leddit.api.output.RedditObject;
import com.leddit.leddit.api.output.RedditProfile;
import com.leddit.leddit.api.output.VoteResponse;

import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Jonah on 6.12.2014.
 */

/*
    Defines paths and rules for all Reddit API Oauth calls
*/

public interface RedditOauthApiService
{
    @GET("/api/v1/me/.json")
    RedditProfile me();

    @GET("/api/v1/me/karma/.json")
    MyRedditKarma myKarma();

    @POST("/api/vote/.json")
    VoteResponse vote(@Field("dir") int direction, @Field("id") String fullname);

    @GET("/api/needs_captcha.json")
    CaptchaNeededResponse needsCaptcha();

    @POST("/api/new_captcha")
    NewCaptchaResponse newCaptcha();

    @GET("/captcha/iden")
    CaptchaIdenResponse iden(@Query("iden") String iden);
}
