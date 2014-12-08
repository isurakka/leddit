package com.leddit.leddit.api;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jonah on 8.12.2014.
 */
public class RedditErrorHandler implements ErrorHandler 
{
    @Override public Throwable handleError(RetrofitError cause) 
    {
        Response r = cause.getResponse();
        
        if (r != null && r.getStatus() == 401) 
        {
            return new UnauthorizedException(cause);
        }
        
        return cause;
    }

    private class UnauthorizedException extends Throwable {
        public UnauthorizedException(RetrofitError cause) {
        }
    }
}
