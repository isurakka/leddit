package com.tidder.tidder.api.output;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonah on 7.12.2014.
 */
public class NewCaptchaResponse extends RedditError
{
    private List<List<Object>> jquery = new ArrayList<List<Object>>();

    public List<List<Object>> getJquery() {
        return jquery;
    }

    public void setJquery(List<List<Object>> jquery) {
        this.jquery = jquery;
    }

    public String getIden()
    {
        return ((List<String>)(List<?>)jquery.get(jquery.size()-1).get(jquery.get(jquery.size()-1).size()-1)).get(0);
    }
}
