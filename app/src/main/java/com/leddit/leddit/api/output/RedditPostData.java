package com.leddit.leddit.api.output;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Jonah on 21.11.2014.
 */

/*
    Container class for Reddit API output
*/
public class RedditPostData {

    private String domain;
    private String banned_by;
    private RedditEmbedMedia media_embed;
    private String subreddit;
    private String selftext_html;
    private String selftext;
    private int likes;
    private String[] user_reports;
    private RedditSecureMedia secure_media;
    private String link_flair_text;
    private String id;
    private int gilded;
    private RedditSecureMediaEmbed secure_media_embed;
    private boolean clicked;
    private String report_reasons;
    private String author;
    private RedditSecureMedia media;
    private int score;
    private String approved_by;
    private boolean over_18;
    private boolean hidden;
    private String thumbnail;
    private String subreddit_id;

    @JsonIgnore
    private boolean edited;

    private String link_flair_css_class;
    private String author_flair_css_class;
    private int downs;
    private String[] mod_reports;
    private boolean saved;
    private boolean is_self;
    private String name;
    private String permalink;
    private boolean stickied;
    private long created;
    private String url;
    private String author_flair_text;
    private String title;
    private long created_utc;
    private int ups;
    private int num_comments;
    private boolean visited;
    private int num_reports;
    private String distinguished;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getBanned_by() {
        return banned_by;
    }

    public void setBanned_by(String banned_by) {
        this.banned_by = banned_by;
    }

    public RedditEmbedMedia getMedia_embed() {
        return media_embed;
    }

    public void setMedia_embed(RedditEmbedMedia media_embed) {
        this.media_embed = media_embed;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getSelftext_html() {
        return selftext_html;
    }

    public void setSelftext_html(String selftext_html) {
        this.selftext_html = selftext_html;
    }

    public String getSelftext() {
        return selftext;
    }

    public void setSelftext(String selftext) {
        this.selftext = selftext;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String[] getUser_reports() {
        return user_reports;
    }

    public void setUser_reports(String[] user_reports) {
        this.user_reports = user_reports;
    }

    public RedditSecureMedia getSecure_media() {
        return secure_media;
    }

    public void setSecure_media(RedditSecureMedia secure_media) {
        this.secure_media = secure_media;
    }

    public String getLink_flair_text() {
        return link_flair_text;
    }

    public void setLink_flair_text(String link_flair_text) {
        this.link_flair_text = link_flair_text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getGilded() {
        return gilded;
    }

    public void setGilded(int gilded) {
        this.gilded = gilded;
    }

    public RedditSecureMediaEmbed getSecure_media_embed() {
        return secure_media_embed;
    }

    public void setSecure_media_embed(RedditSecureMediaEmbed secure_media_embed) {
        this.secure_media_embed = secure_media_embed;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public String getReport_reasons() {
        return report_reasons;
    }

    public void setReport_reasons(String report_reasons) {
        this.report_reasons = report_reasons;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public RedditSecureMedia getMedia() {
        return media;
    }

    public void setMedia(RedditSecureMedia media) {
        this.media = media;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getApproved_by() {
        return approved_by;
    }

    public void setApproved_by(String approved_by) {
        this.approved_by = approved_by;
    }

    public boolean isOver_18() {
        return over_18;
    }

    public void setOver_18(boolean over_18) {
        this.over_18 = over_18;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getSubreddit_id() {
        return subreddit_id;
    }

    public void setSubreddit_id(String subreddit_id) {
        this.subreddit_id = subreddit_id;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public String getLink_flair_css_class() {
        return link_flair_css_class;
    }

    public void setLink_flair_css_class(String link_flair_css_class) {
        this.link_flair_css_class = link_flair_css_class;
    }

    public String getAuthor_flair_css_class() {
        return author_flair_css_class;
    }

    public void setAuthor_flair_css_class(String author_flair_css_class) {
        this.author_flair_css_class = author_flair_css_class;
    }

    public int getDowns() {
        return downs;
    }

    public void setDowns(int downs) {
        this.downs = downs;
    }

    public String[] getMod_reports() {
        return mod_reports;
    }

    public void setMod_reports(String[] mod_reports) {
        this.mod_reports = mod_reports;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public boolean isIs_self() {
        return is_self;
    }

    public void setIs_self(boolean is_self) {
        this.is_self = is_self;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public boolean isStickied() {
        return stickied;
    }

    public void setStickied(boolean stickied) {
        this.stickied = stickied;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor_flair_text() {
        return author_flair_text;
    }

    public void setAuthor_flair_text(String author_flair_text) {
        this.author_flair_text = author_flair_text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCreated_utc() {
        return created_utc;
    }

    public void setCreated_utc(long created_utc) {
        this.created_utc = created_utc;
    }

    public int getUps() {
        return ups;
    }

    public void setUps(int ups) {
        this.ups = ups;
    }

    public int getNum_comments() {
        return num_comments;
    }

    public void setNum_comments(int num_comments) {
        this.num_comments = num_comments;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public int getNum_reports() {
        return num_reports;
    }

    public void setNum_reports(int num_reports) {
        this.num_reports = num_reports;
    }

    public String isDistinguished() {
        return distinguished;
    }

    public void setDistinguished(String distinguished) {
        this.distinguished = distinguished;
    }
}
