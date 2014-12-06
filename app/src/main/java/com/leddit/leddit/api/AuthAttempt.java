package com.leddit.leddit.api;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Jonah on 6.12.2014.
 */
public class AuthAttempt
{
    UUID state;

    public AuthAttempt()
    {
        this.state = UUID.randomUUID();
    }

    public String getAuthUrl()
    {
        return "https://ssl.reddit.com/api/v1/authorize?state=" + this.state.toString() + "&duration=permanent&"
                + "response_type=code&scope=identity&client_id=TPdgxXER-lcR8Q&redirect_uri=http://google.fi";
    }

    public AuthState userAuthProcess(String returnedState, String code)
    {
        if(returnedState == this.state.toString())
        {
            System.out.println(returnedState);
            System.out.println(code);

            AuthState s = RedditApi.getInstance().authorize(returnedState, code);

            System.out.println(s.getAccess_token());
            System.out.println(s.getToken_type());
            System.out.println(s.getExpires_in());
            System.out.println(s.getScope());
            System.out.println(s.getRefresh_token());

            return s;
        }
        else
        {
            return null;
        }

    }
}