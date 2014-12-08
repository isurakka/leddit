package com.tidder.tidder.api.output;

/**
 * Created by Jonah on 4.12.2014.
 */

/*
    Container class for Reddit API output
*/
public class RedditCommentBlobData extends RedditError
{
    private String kind;
    private RedditCommentBlob data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public RedditCommentBlob getData() {
        return data;
    }

    public void setData(RedditCommentBlob data) {
        this.data = data;
    }
}
