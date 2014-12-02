package com.leddit.leddit;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Iiro on 30.11.2014.
 */
public class RedditThread {
    private String title;
    private int score;
    private String link;
    private String domain;
    private DateTime postDate;
    // TODO: RedditUser class with lazy loading instead?
    private String user;

    // TODO: Lazy loading + getters
    private String text;

    // TODO: Lazy loading + getters
    private List<RedditComment> comments;

    public RedditThread(String title, int score, String link, String domain, DateTime postDate, String user, List<RedditComment> comments)
    {
        this.title = title;
        this.score = score;
        this.link = link;
        this.domain = domain;
        this.postDate = postDate;
        this.user = user;
        this.comments = comments;
    }

    // TODO: Remove setters (type should be immutable; constructor initialization only (like RedditComment class))

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
        return comments.size();
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
