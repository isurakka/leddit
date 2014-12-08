package com.leddit.leddit;

import com.leddit.leddit.api.RedditThing;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.List;

/**
 * Created by Iiro on 30.11.2014.
 */

/*
    Container class for data-ripped Reddit threads
*/
public class RedditThread extends RedditThing {
    private String title;
    private int score;
    private String link;
    private String domain;
    private DateTime postDate;
    // TODO: RedditUser class with lazy loading instead?
    private String user;
    private String subreddit;
    private String id36;
    private int num_comments;
    private boolean is_self;
    private String thumbnail_url;

    // TODO: Lazy loading + getters
    private String text;

    // TODO: Lazy loading + getters
    private List<RedditComment> comments;

    public RedditThread(String title, int score, String link, String domain, DateTime postDate,
                        String user, List<RedditComment> comments, String subreddit, String id36,
                        int num_comments, boolean is_self, String thumbnail_url, String fullname, Boolean likes)
    {
        super(fullname, likes);
        this.title = title;
        this.score = score;
        this.link = link;
        this.domain = domain;
        this.postDate = postDate;
        this.user = user;
        this.setComments(comments);
        this.subreddit = subreddit;
        this.id36 = id36;
        this.num_comments = num_comments;
        this.is_self = is_self;
        this.thumbnail_url = thumbnail_url;
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

    public int getNum_comments() {
        return num_comments;
    }

    public void setNum_comments(int num_comments) {
        this.num_comments = num_comments;
    }

    public boolean isIs_self() {
        return is_self;
    }

    public void setIs_self(boolean is_self) {
        this.is_self = is_self;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

}
