package com.leddit.leddit.api.output;

/**
 * Created by Jonah on 21.11.2014.
 */

/*
    Container class for Reddit API output
*/
public class RedditPost {

    private String kind;
    private RedditPostData data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public RedditPostData getData() {
        return data;
    }

    public void setData(RedditPostData data) {
        this.data = data;
    }
}
