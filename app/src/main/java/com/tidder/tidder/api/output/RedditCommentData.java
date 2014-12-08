package com.tidder.tidder.api.output;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonah on 4.12.2014.
 */

/*
    Container class for Reddit API output
*/
public class RedditCommentData extends RedditError
{
    private String modhash;
    private String after;
    private String before;
    private List<RedditCommentBlobData> children = new ArrayList<RedditCommentBlobData>();

    public String getModhash() {
        return modhash;
    }

    public void setModhash(String modhash) {
        this.modhash = modhash;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public List<RedditCommentBlobData> getChildren() {
        return children;
    }

    public void setChildren(List<RedditCommentBlobData> children) {
        this.children = children;
    }
}
