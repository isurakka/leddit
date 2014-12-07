package com.leddit.leddit.api.output;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonah on 20.11.2014.
 */

/*
    Container class for Reddit API output
*/
public class RedditObject {

    private String kind;
    private RedditData data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public RedditData getData() {
        return data;
    }

    public void setData(RedditData data) {
        this.data = data;
    }
}
