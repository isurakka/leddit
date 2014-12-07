package com.leddit.leddit;

import com.leddit.leddit.api.RedditCommentType;

import org.joda.time.DateTime;

/**
 * Created by Iiro on 2.12.2014.
 */
public class RedditComment {
    private String text;

    // TODO: RedditUser class with lazy loading instead?
    private String user;

    private int score;
    private DateTime postDate;
    private int depth;

    private RedditCommentType type;

    private String fullname;

    // TODO: Information whether comment has been edited or not

    public RedditComment(int depth, String user, int score, DateTime postDate,
                         String text, RedditCommentType type, String fullname) {
        this.depth = depth;
        this.user = user;
        this.score = score;
        this.postDate = postDate;
        this.text = text;
        this.type = type;
        this.fullname = fullname;
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return user;
    }

    public int getScore() {
        return score;
    }

    public DateTime getPostDate() {
        return postDate;
    }

    public int getDepth() {
        return depth;
    }

    public RedditCommentType getType() {
        return type;
    }

    public void setType(RedditCommentType type) {
        this.type = type;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
