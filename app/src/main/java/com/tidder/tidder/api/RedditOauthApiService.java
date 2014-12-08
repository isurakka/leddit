package com.tidder.tidder.api;

import com.tidder.tidder.api.output.MyRedditKarma;
import com.tidder.tidder.api.output.NewCaptchaResponse;
import com.tidder.tidder.api.output.RedditCommentObject;
import com.tidder.tidder.api.output.RedditObject;
import com.tidder.tidder.api.output.RedditPostResponse;
import com.tidder.tidder.api.output.RedditProfile;
import com.tidder.tidder.api.output.VoteResponse;

import java.util.List;
import java.util.Map;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.mime.TypedFile;

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

    @FormUrlEncoded
    @POST("/api/vote/.json")
    VoteResponse vote(@Field("dir") int direction, @Field("id") String fullname);

    @GET("/api/needs_captcha.json")
    boolean needsCaptcha();

    @POST("/api/new_captcha")
    NewCaptchaResponse newCaptcha();

    @GET("/captcha/iden")
    TypedFile iden(@Query("iden") String iden);

    @FormUrlEncoded
    @POST("/api/submit")
    RedditPostResponse submitPost(
            @Field("api_type") String api_type,
            @Field("captcha") String captcha,
            @Field("extension") String extension,
            @Field("iden") String iden,
            @Field("kind") String kind,
            @Field("resubmit") boolean resubmit,
            @Field("sendreplies") boolean sendreplies,
            @Field("sr") String subreddit,
            @Field("text") String text,
            @Field("then") String then,
            @Field("title") String title,
            @Field("url") String url
    );

    @GET("/r/{subreddit}/{orderby}/.json")
    RedditObject listSubreddit(@Path("subreddit") String subreddit, @Path("orderby") String orderBy);

    @GET("/r/{subreddit}/{orderby}/.json")
    RedditObject listSubredditWithTime(@Path("subreddit") String subreddit, @Path("orderby") String orderBy,
                                       @Query("sort") String sort, @Query("t") String timeScale);

    @GET("/{orderby}/.json")
    RedditObject frontPage(@Path("orderby") String orderBy);

    @GET("/{orderby}/.json")
    RedditObject frontPageWithTimescale(@Path("orderby") String orderBy, @Query("sort") String sort, @Query("t") String timeScale);

    @GET("/r/{subreddit}/comments/{article}/.json")
    List<RedditCommentObject> listThreadComments(@Path("subreddit") String subreddit, @Path("article") String article,
                                                 @Query("sort") String sort);

}
