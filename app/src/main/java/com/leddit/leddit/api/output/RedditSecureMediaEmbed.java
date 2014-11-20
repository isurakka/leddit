package com.leddit.leddit.api.output;

/**
 * Created by Jonah on 21.11.2014.
 */
public class RedditSecureMediaEmbed {

    private String content;
    private  int width;
    private int height;
    private boolean scrolling;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isScrolling() {
        return scrolling;
    }

    public void setScrolling(boolean scrolling) {
        this.scrolling = scrolling;
    }
}
