package com.leddit.leddit.api.output;

/**
 * Created by Jonah on 21.11.2014.
 */
public class RedditSecureMedia {

    private String type;
    private RedditOembed oembed;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RedditOembed getOembed() {
        return oembed;
    }

    public void setOembed(RedditOembed oembed) {
        this.oembed = oembed;
    }
}
