package com.leddit.leddit.api;

import android.util.Base64;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.leddit.leddit.RedditComment;
import com.leddit.leddit.RedditThread;
import com.leddit.leddit.api.output.RedditAuthorizationData;
import com.leddit.leddit.api.output.RedditCommentData;
import com.leddit.leddit.api.output.RedditCommentObject;
import com.leddit.leddit.api.output.RedditLoginData;
import com.leddit.leddit.api.output.RedditObject;
import com.leddit.leddit.api.output.RedditPostData;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.JacksonConverter;

public class RedditApi {

    public static final String CLIENT_ID = "TPdgxXER-lcR8Q";
    public static final String REDIRECT_URI = "http://google.fi";

    private RedditOauthApiService oService;
    private RedditAuthorizationService aService;
    private RedditApiService rService;
    private static RedditApi instance = null;

    private AuthState redditAuthState;


    public static RedditApi getInstance() {
        if (instance == null) {
            instance = new RedditApi();
        }
        return instance;
    }

    private RedditApi()
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        JacksonConverter converter = new JacksonConverter(mapper);

        RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://www.reddit.com/").setConverter(converter)
                .build();

        RestAdapter restAdapter2 = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("https://ssl.reddit.com/").setConverter(converter)
                .build();

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                System.out.println("Oauth API Request intercepted and bearer token (" + redditAuthState.getAccess_token() + ") injected!");

                //String auth = new String(Base64.encodeToString("TPdgxXER-lcR8Q:".getBytes(), Base64.DEFAULT));
                //String fullAuth = "Basic " + auth;

                request.addHeader("Authorization", redditAuthState.getAccess_token());
            }
        };

        RestAdapter restAdapter3 = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(requestInterceptor)
                .setEndpoint("https://ssl.reddit.com/").setConverter(converter)
                .build();

        rService = restAdapter.create(RedditApiService.class);
        aService = restAdapter2.create(RedditAuthorizationService.class);
        oService = restAdapter3.create(RedditOauthApiService.class);
    }

    private List<RedditComment> tmpComments;
    private List<RedditCommentObject> tmpCommentList;

    public List<RedditComment> getComments(RedditThread thread)
    {
        tmpComments = new ArrayList<RedditComment>();
        List<RedditCommentObject> tmpCommentList = rService.listThreadComments(thread.getSubreddit(), thread.getId36(), null);

        for (int i = 0; i < tmpCommentList.size(); i++)
        {
            RedditCommentData data = tmpCommentList.get(i).getData();

            for (int j = 0; j < data.getChildren().size(); j++)
            {
                int depth = 0;

                DateTime commentPostDate = new DateTime(DateTimeZone.UTC);
                commentPostDate.plus(data.getChildren().get(j).getData().getCreated_utc());

                tmpComments.add(new RedditComment(depth, data.getChildren().get(j).getData().getAuthor(),
                        data.getChildren().get(j).getData().getScore(), commentPostDate,
                        data.getChildren().get(j).getData().getBody()));

                if (data.getChildren().get(j).getData().getReplies().getData() != null)
                {
                    recursive(data.getChildren().get(j).getData().getReplies(), depth);
                }
            }
        }

        return tmpComments;
    }

    private void recursive(RedditCommentObject commentObject, int depth)
    {
        if(commentObject == null)
        {
            return;
        }
            depth++;

        for (int i = 0; i < commentObject.getData().getChildren().size(); i++)
        {
            DateTime commentPostDate = new DateTime(DateTimeZone.UTC);
            commentPostDate.plus(commentObject.getData().getChildren().get(i).getData().getCreated_utc());

            tmpComments.add(new RedditComment(depth, commentObject.getData().getChildren().get(i).getData().getAuthor(),
                    commentObject.getData().getChildren().get(i).getData().getScore(), commentPostDate,
                    commentObject.getData().getChildren().get(i).getData().getBody()));

            if (commentObject.getData().getChildren().get(i).getData().getReplies().getData() != null)
            {
                recursive(commentObject.getData().getChildren().get(i).getData().getReplies(), depth);
            }
        }
    }

    public List<RedditThread> getThreads(String subreddit, String sorting)
    {
        List<RedditThread> tmpList = new ArrayList<RedditThread>();
        RedditObject o = rService.listSubreddit(subreddit, sorting);
        RedditThread thread;

        for(int i = 0; i < o.getData().getChildren().size(); i++)
        {
            RedditPostData postData = o.getData().getChildren().get(i).getData();

            String title = postData.getTitle();
            int score = postData.getScore();
            String link = postData.getUrl();
            String domain = postData.getDomain();
            DateTime postDate = new DateTime(DateTimeZone.UTC);
            postDate.plus(postData.getCreated_utc());
            String user = postData.getAuthor();
            String sub = postData.getSubreddit();
            String id36 = postData.getId();

            List<RedditComment> comments = new ArrayList<RedditComment>();

            thread = new RedditThread(title, score, link, domain, postDate, user, comments, sub, id36);
            tmpList.add(thread);
        }

        return tmpList;
    }

    public List<RedditThread> getFrontpage(String sorting)
    {
        List<RedditThread> tmpList = new ArrayList<RedditThread>();
        RedditObject o = rService.frontPage(sorting);
        RedditThread thread;

        for(int i = 0; i < o.getData().getChildren().size(); i++)
        {
            RedditPostData postData = o.getData().getChildren().get(i).getData();

            String title = postData.getTitle();
            int score = postData.getScore();
            String link = postData.getUrl();
            String domain = postData.getDomain();
            DateTime postDate = new DateTime(DateTimeZone.UTC);
            postDate.plus(postData.getCreated_utc());
            String user = postData.getAuthor();
            String sub = postData.getSubreddit();
            String id36 = postData.getId();

            List<RedditComment> comments = new ArrayList<RedditComment>();

            thread = new RedditThread(title, score, link, domain, postDate, user, comments, sub, id36);
            tmpList.add(thread);
        }

        return tmpList;
    }

    public AuthState authorize(String state, String code)
    {
        String auth = new String(Base64.encodeToString((CLIENT_ID + ":").getBytes(), Base64.DEFAULT));
        String fullAuth = "Basic " + auth;

        AuthState authState = aService.authorize(fullAuth, REDIRECT_URI, code, "authorization_code");
        this.redditAuthState = authState;
        return authState;
    }

    public void oauthCallTest()
    {
        System.out.println("TEST");
    }

    public AuthState getRedditAuthState() {
        return redditAuthState;
    }

    public void setRedditAuthState(AuthState redditAuthState) {
       this.redditAuthState = redditAuthState;
    }
}
