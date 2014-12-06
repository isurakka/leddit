package com.leddit.leddit;

import com.leddit.leddit.api.RedditApi;
import com.leddit.leddit.api.Utility;

import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;

import java.util.List;

/**
 * Created by Jonah on 20.11.2014.
 */
/*public class Main {
    public static void main(String[] args) {

        RedditApi api = RedditApi.getInstance();

        /*List<RedditComment> comments = RedditApi.getComments();

        for (int i = 0; i < comments.size(); i++)
        {
            System.out.println(pad(comments.get(i).getDepth()) + "Score: " + comments.get(i).getScore() + ", Author: " + comments.get(i).getUser() + ", Date: " + comments.get(i).getPostDate());
            System.out.println(pad(comments.get(i).getDepth()) + comments.get(i).getText() + "\n");

        }*/

        /*List<RedditThread> threadList = api.getThreads("games", "hot");

        for(int i = 0; i < threadList.size(); i++)
        {
            RedditThread thread = threadList.get(i);

            System.out.println("{\n" +
                    "\tTitle: " + thread.getTitle() +
                    "\n\tDomain: " + thread.getDomain() +
                    "\n\tLink: " + thread.getLink() +
                    "\n\tUser: " + thread.getUser() +
                    "\n\tComments: " + thread.getCommentCount() +
                    "\n\tDate: " + thread.getPostDate() +
                    "\n\tScore: " + thread.getScore() +
                    "\n\tPosted: " + Utility.redditTimePeriod(thread.getPostDate(), DateTime.now(DateTimeZone.UTC)) + " ago" +
                    "\n}\n");

        }
    }

    private static String pad(int pad)
    {
        String p = "";

        for(int i = 0; i < pad; i++)
        {
            p += "    ";
        }
        return p;
    }
}*/
