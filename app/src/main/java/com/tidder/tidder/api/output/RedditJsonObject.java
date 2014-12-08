package com.tidder.tidder.api.output;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonah on 8.12.2014.
 */
public class RedditJsonObject
{
    private String captcha;
    private double ratelimit;
    private List<List<String>> errors = new ArrayList<List<String>>();
    private RedditJsonData data;

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public double getRatelimit() {
        return ratelimit;
    }

    public void setRatelimit(double ratelimit) {
        this.ratelimit = ratelimit;
    }

    public List<List<String>> getErrors() {
        return errors;
    }

    public void setErrors(List<List<String>> errors) {
        this.errors = errors;
    }

    public RedditJsonData getData() {
        return data;
    }

    public void setData(RedditJsonData data) {
        this.data = data;
    }
}
