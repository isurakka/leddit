package com.leddit.leddit.api.output;

/**
 * Created by Jonah on 21.11.2014.
 */

/*
    Container class for Reddit API output
*/
public class RedditSecureMedia extends RedditError {

    private String type;
    private RedditOembed oembed;
    private String event_id;

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

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }
}
