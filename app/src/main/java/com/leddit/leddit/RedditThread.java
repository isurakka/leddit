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
    private String subreddit;
    private String id36;

    // TODO: Lazy loading + getters
    private String text;

    // TODO: Lazy loading + getters
    private List<RedditComment> comments;

    public RedditThread(String title, int score, String link, String domain, DateTime postDate, String user, List<RedditComment> comments, String subreddit, String id36)
    {
        this.title = title;
        this.score = score;
        this.link = link;
        this.domain = domain;
        this.postDate = postDate;
        this.user = user;
        this.setComments(comments);
        this.subreddit = subreddit;
        this.id36 = id36;
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
        return getComments() != null ? getComments().size() : 0;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getId36() {
        return id36;
    }

    public void setId36(String id36) {
        this.id36 = id36;
    }

    public List<RedditComment> getComments() {
        return comments;
    }

    public void setComments(List<RedditComment> comments) {
        this.comments = comments;
    }
}
