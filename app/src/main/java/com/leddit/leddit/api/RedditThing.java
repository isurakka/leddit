package com.leddit.leddit.api;

/**
 * Created by Jonah on 7.12.2014.
 */
public class RedditThing
{
    private String fullname;

    public RedditThing(String fullname)
    {
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
