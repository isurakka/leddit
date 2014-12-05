package com.leddit.leddit.api;

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

import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

public class RedditApi {

    private static RedditAuthorizationService aService;
    private static RedditApiService rService;
    private static RedditApi instance = null;


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

        rService = restAdapter.create(RedditApiService.class);
        aService = restAdapter.create(RedditAuthorizationService.class);

    }

    private static List<RedditComment> tmpComments;
    private static List<RedditCommentObject> tmpCommentList;

    public static List<RedditComment> getComments(RedditThread thread)
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
                    RedditApi.recursive(data.getChildren().get(j).getData().getReplies(), depth);
                }
            }
        }

        return tmpComments;
    }

    private static void recursive(RedditCommentObject commentObject, int depth)
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
                RedditApi.recursive(commentObject.getData().getChildren().get(i).getData().getReplies(), depth);
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

    public String getAuthorizationUrl()
    {
        UUID state = UUID.randomUUID();

        return "https://ssl.reddit.com/api/v1/authorize?state=" + state.toString() + "&duration=permanent&"
        + "response_type=code&scope=identity&client_id=TPdgxXER-lcR8Q&redirect_uri=http://google.fi";
    }

    public void authorizationCallback(String params)
    {

    }

    public void authorize()
    {
        /*Map<String, String> authInput = new HashMap<String, String>();
        authInput.put("state", "d99649e9-c052-426f-bd5b-3ff26a3fccd3");
        authInput.put("duration", "permanent");
        authInput.put("response_type", "code");
        authInput.put("scope", "identity");
        authInput.put("client_id", "TPdgxXER-lcR8Q");
        authInput.put("redirect_uri", "http://google.fi");

        String authorizationData = aService.authorize(authInput);*/

    }

    /*public String login(String username, String password, boolean remember)
    {
        Map loginInfo = new HashMap<String, String>();
        loginInfo.put("user", username);
        loginInfo.put("passwd", password);

        RedditLoginData loginData = rService.login(loginInfo);
    }*/
}
