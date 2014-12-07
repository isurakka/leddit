package com.leddit.leddit.api.output;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Jonah on 4.12.2014.
 */

/*
    Container class for Reddit API output
*/
public class RedditCommentObject extends RedditError
{
    private String kind;
    private RedditCommentData data;

    public RedditCommentObject()
    {
    }

    public RedditCommentObject(String kind, RedditCommentData data)
    {
        this.kind = kind;
        this.data = data;
    }

    public RedditCommentObject(String s)
    {
        this.kind = null;
        this.data = null;
    }


    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public RedditCommentData getData() {
        return data;
    }

    public void setData(RedditCommentData data) {
        this.data = data;
    }
}
