package com.leddit.leddit.api;

/**
 * Created by Jonah on 7.12.2014.
 */

/*
    Enum for Reddit vote direction
*/

public class RedditVote
{
    private RedditVote()
    {

    }

    public static final int UP = 1;
    public static final int CANCEL = 0;
    public static final int DOWN = -1;
}
