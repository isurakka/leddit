package com.leddit.leddit.api;

import org.joda.time.DateTime;

/**
 * Created by Jonah on 6.12.2014.
 */
public class AuthState
{
    String access_token;
    String token_type;
    DateTime expires_in;
    String scope;
    String refresh_token;

    public AuthState(String access_token, String token_type, DateTime expires_in, String scope, String refresh_token)
    {
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.scope = scope;
        this.refresh_token = refresh_token;
    }
}
