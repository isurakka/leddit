package com.leddit.leddit.api;

/**
 * Created by Jonah on 7.12.2014.
 */

/*
    Base class for RedditThread and RedditComment
*/

public class RedditThing
{
    private String fullname;
    private Boolean likes;

    public RedditThing(String fullname, Boolean likes)
    {
        this.fullname = fullname;
        this.likes = likes;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public boolean getLikes() {
        return likes;
    }

    public void setLikes(boolean likes) {
        this.likes = likes;
    }
}
