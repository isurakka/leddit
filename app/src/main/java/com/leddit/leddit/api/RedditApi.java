package com.leddit.leddit.api;

import com.fasterxml.jackson.databind.ObjectMapper;

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

        System.out.print(rService.listSubreddit("javascript", "hot").getData().getChildren().get(0).getData().getSubreddit());
    }
}
