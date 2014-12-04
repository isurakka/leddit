package com.leddit.leddit.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.leddit.leddit.RedditComment;
import com.leddit.leddit.RedditThread;
import com.leddit.leddit.api.output.RedditCommentData;
import com.leddit.leddit.api.output.RedditCommentObject;
import com.leddit.leddit.api.output.RedditObject;
import com.leddit.leddit.api.output.RedditPostData;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

public class RedditApi {

    RedditApiService rService;

    public RedditApi()
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        JacksonConverter converter = new JacksonConverter(mapper);
        RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint("http://www.reddit.com/").setConverter(converter)
            .build();

        rService = restAdapter.create(RedditApiService.class);

        /*List<RedditCommentObject> commentObjectList = rService.listThreadComments("test", "2o9mug", null);

        for(int i = 0; i < commentObjectList.size(); i++)
        {
            RedditCommentData data = commentObjectList.get(i).getData();

            for(int j = 0; j < data.getChildren().size(); j++)
            {

                System.out.println(data.getChildren().get(i).getData().getBody());

            }
        }*/

        List<RedditThread> threadList = this.getThreads("games", "hot");

        for(int i = 0; i < threadList.size(); i++)
        {
            RedditThread thread = threadList.get(i);

            System.out.println("{\n" +
                    "\tTitle: " + thread.getTitle() +
                    "\n\tDomain: " + thread.getDomain() +
                    "\n\tLink: " + thread.getLink() +
                    "\n\tUser: " + thread.getUser() +
                    "\n\tComments: " + thread.getCommentCount() +
                    "\n\tDate: " + thread.getPostDate() +
                    "\n\tScore: " + thread.getScore() +
                    "\n}");

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

            List<RedditComment> comments = new ArrayList<RedditComment>();

            thread = new RedditThread(title, score, link, domain, postDate, user, comments);
            tmpList.add(thread);
        }

        return tmpList;
    }
}
