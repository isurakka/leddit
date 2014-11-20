package com.leddit.leddit.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leddit.leddit.api.output.RedditObject;

import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

public class RedditApi {

    public RedditApi()
    {
        JacksonConverter converter = new JacksonConverter(new ObjectMapper());
        RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint("http://www.reddit.com/").setConverter(converter)
            .build();

        RedditApiService rService = restAdapter.create(RedditApiService.class);

        RedditObject o = rService.listSubreddit("games", "hot");

        for(int i = 0; i < o.getData().getChildren().size(); i++)
        {
            System.out.println(o.getData().getChildren().get(i).getData().getTitle());
        }

    }
}
