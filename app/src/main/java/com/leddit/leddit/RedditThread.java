package com.leddit.leddit;

import org.joda.time.DateTime;

/**
 * Created by Iiro on 30.11.2014.
 */
public class RedditThread {
    private String title;
    private int score;
    private String domain;
    private DateTime postDate;
    private String user;
    private int commentCount;

    public RedditThread(String title, int score, String domain, DateTime postDate, String user, int commentCount)
    {
        this.title = title;
        this.score = score;
        this.domain = domain;
        this.postDate = postDate;
        this.user = user;
        this.commentCount = commentCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public DateTime getPostDate() {
        return postDate;
    }

    public void setPostDate(DateTime postDate) {
        this.postDate = postDate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
