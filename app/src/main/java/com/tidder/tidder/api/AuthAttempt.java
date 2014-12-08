package com.tidder.tidder.api;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Jonah on 6.12.2014.
 */

/*
    Used to authorize user
*/

public class AuthAttempt
{
    UUID state;
    String scope;

    public AuthAttempt()
    {
        this.state = UUID.randomUUID();
        scope = "modposts,identity,edit,flair,history,modconfig,modflair,modlog,"+
                "modposts,modwiki,mysubreddits,privatemessages,read,report,save,submit,"+
                "subscribe,vote,wikiedit,wikiread";
    }

    public String getAuthUrl()
    {
        return "https://ssl.reddit.com/api/v1/authorize?state=" + this.state + "&duration=permanent"
                + "&response_type=code&scope=" + this.scope + "&client_id=" + RedditApi.CLIENT_ID + "&redirect_uri=" + RedditApi.REDIRECT_URI;
    }

    public AuthState userAuthProcess(String returnedState, String code)
    {
        if(UUID.fromString(returnedState).compareTo(this.state) == 0)
        {
            System.out.println("--STATE MATCH--");
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
            System.out.println("--INVALID STATE--");
            System.out.println("Client state: " + this.state.toString());
            System.out.println("Returned state: " + returnedState);

            return null;
        }
    }

    public String getScope() {
        return scope;
    }
}