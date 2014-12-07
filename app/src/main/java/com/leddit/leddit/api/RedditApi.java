package com.leddit.leddit.api;

import android.util.Base64;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.leddit.leddit.RedditComment;
import com.leddit.leddit.RedditThread;
import com.leddit.leddit.api.output.CaptchaIdenResponse;
import com.leddit.leddit.api.output.CaptchaNeededResponse;
import com.leddit.leddit.api.output.MyRedditKarma;
import com.leddit.leddit.api.output.NewCaptchaResponse;
import com.leddit.leddit.api.output.RedditAuthStateProxy;
import com.leddit.leddit.api.output.RedditCommentData;
import com.leddit.leddit.api.output.RedditCommentObject;
import com.leddit.leddit.api.output.RedditObject;
import com.leddit.leddit.api.output.RedditPostData;
import com.leddit.leddit.api.output.RedditPostSubmitResponse;
import com.leddit.leddit.api.output.RedditProfile;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

/**
 * Created by Jonah on 21.11.2014.
 */

/*
    Public interface for Reddit API calls
    Container for all APIservices and RESTadapters
*/

public class RedditApi implements AuthStateListener {

    public static final String CLIENT_ID = "TPdgxXER-lcR8Q";
    public static final String REDIRECT_URI = "http://google.fi";

    private RedditOauthApiService oService;
    private RedditAuthorizationService aService;
    private RedditApiService rService;
    private static RedditApi instance = null;

    private AuthState redditAuthState;


    public static RedditApi getInstance() {
        if (instance == null) {
            instance = new RedditApi();
        }
        return instance;
    }

    private RedditApi()
    {
        redditAuthState = new AuthState();
        redditAuthState.addListener(this);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        JacksonConverter converter = new JacksonConverter(mapper);

        RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://www.reddit.com/").setConverter(converter)
                .build();

        RestAdapter restAdapter2 = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("https://ssl.reddit.com/").setConverter(converter)
                .build();

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                System.out.println("Oauth API Request intercepted and bearer token (" + redditAuthState.getAccess_token() + ") injected!");

                if(Utility.hasExpired(redditAuthState.getExpires_in()))
                {
                    System.out.println("Token needs refresh, doing that now!");
                    refreshToken();
                }
                else
                {
                    System.out.println("Token not yet expired!");
                }

                request.addHeader("Authorization", "bearer " + redditAuthState.getAccess_token());
            }
        };

        RestAdapter restAdapter3 = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(requestInterceptor)
                .setEndpoint("https://oauth.reddit.com/").setConverter(converter)
                .build();

        rService = restAdapter.create(RedditApiService.class);
        aService = restAdapter2.create(RedditAuthorizationService.class);
        oService = restAdapter3.create(RedditOauthApiService.class);




    }

    private List<RedditComment> tmpComments;
    private List<RedditCommentObject> tmpCommentList;

    public List<RedditComment> getComments(RedditThread thread)
    {
        tmpComments = new ArrayList<RedditComment>();
        List<RedditCommentObject> tmpCommentList = rService.listThreadComments(thread.getSubreddit(), thread.getId36(), null);

        for (int i = 0; i < tmpCommentList.size(); i++)
        {
            RedditCommentData data = tmpCommentList.get(i).getData();

            for (int j = 0; j < data.getChildren().size(); j++)
            {
                int depth = 0;

                DateTime commentPostDate = new DateTime(DateTimeZone.UTC);
                commentPostDate.plus(data.getChildren().get(j).getData().getCreated_utc());

                tmpComments.add(new RedditComment(depth, data.getChildren().get(j).getData().getAuthor(),
                        data.getChildren().get(j).getData().getScore(), commentPostDate,
                        data.getChildren().get(j).getData().getBody(),
                        Utility.parseCommentType(data.getChildren().get(i).getKind()),
                        "t1_" + data.getChildren().get(j).getData().getId()));

                if (data.getChildren().get(j).getData().getReplies().getData() != null)
                {
                    recursive(data.getChildren().get(j).getData().getReplies(), depth);
                }
            }
        }

        return tmpComments;
    }

    private void recursive(RedditCommentObject commentObject, int depth)
    {
        if(commentObject == null)
        {
            return;
        }
            depth++;

        for (int i = 0; i < commentObject.getData().getChildren().size(); i++)
        {
            DateTime commentPostDate = new DateTime(DateTimeZone.UTC);
            commentPostDate.plus(commentObject.getData().getChildren().get(i).getData().getCreated_utc());

            tmpComments.add(new RedditComment(depth, commentObject.getData().getChildren().get(i).getData().getAuthor(),
                    commentObject.getData().getChildren().get(i).getData().getScore(), commentPostDate,
                    commentObject.getData().getChildren().get(i).getData().getBody(),
                    Utility.parseCommentType(commentObject.getData().getChildren().get(i).getKind()),
                    "t1_" + commentObject.getData().getChildren().get(i).getData().getId()));

            if (commentObject.getData().getChildren().get(i).getData().getReplies().getData() != null)
            {
                recursive(commentObject.getData().getChildren().get(i).getData().getReplies(), depth);
            }
        }
    }

    public List<RedditThread> getThreads(String subreddit, String sorting, String timeScale)
    {
        List<RedditThread> tmpList = new ArrayList<RedditThread>();
        RedditObject o;

        if(timeScale == null)
        {
            o = rService.listSubreddit(subreddit, sorting);
        }
        else
        {
            o = rService.listSubredditWithTime(subreddit, sorting, sorting, timeScale);
        }

        RedditThread thread;

        for(int i = 0; i < o.getData().getChildren().size(); i++)
        {
            RedditPostData postData = o.getData().getChildren().get(i).getData();

            String title = postData.getTitle();
            int score = postData.getScore();
            String link = postData.getUrl();
            String domain = postData.getDomain();
            DateTime postDate = new DateTime(postData.getCreated_utc() * 1000, DateTimeZone.UTC);
            postDate.plus(postData.getCreated_utc());
            String user = postData.getAuthor();
            String sub = postData.getSubreddit();
            String id36 = postData.getId();
            int num_comments = postData.getNum_comments();
            boolean is_self = postData.isIs_self();
            String thumbnail;
            String fullname = "t3_" + postData.getId();

            if(postData.getThumbnail() != "")
            {
                if(postData.getThumbnail().equals("self"))
                {
                    thumbnail = "http://i.imgur.com/LAOXHdN.png";
                }
                else if(postData.getThumbnail().equals("nsfw"))
                {
                    thumbnail = "http://i.imgur.com/cgDTKfV.png";
                }
                else
                {
                    thumbnail = postData.getThumbnail();
                }
            }
            else
            {
                thumbnail = null;
            }

            List<RedditComment> comments = new ArrayList<RedditComment>();

            thread = new RedditThread(title, score, link, domain, postDate, user, comments,
                    sub, id36, num_comments, is_self, thumbnail, fullname);
            tmpList.add(thread);
        }

        return tmpList;
    }

    public List<RedditThread> getFrontpage(String sorting, String timeScale)
    {
        List<RedditThread> tmpList = new ArrayList<RedditThread>();
        RedditObject o;

        if(timeScale == null)
        {
            o = rService.frontPage(sorting);
        }
        else
        {
            o = rService.frontPageWithTimescale(sorting, sorting, timeScale);
        }


        RedditThread thread;

        for(int i = 0; i < o.getData().getChildren().size(); i++)
        {
            RedditPostData postData = o.getData().getChildren().get(i).getData();

            String title = postData.getTitle();
            int score = postData.getScore();
            String link = postData.getUrl();
            String domain = postData.getDomain();
            DateTime postDate = new DateTime(postData.getCreated_utc() * 1000, DateTimeZone.UTC);
            String user = postData.getAuthor();
            String sub = postData.getSubreddit();
            String id36 = postData.getId();
            int num_comments = postData.getNum_comments();
            boolean is_self = postData.isIs_self();
            String thumbnail;
            String fullname = "t3_" + postData.getId();

            if(postData.getThumbnail() != "")
            {
                if(postData.getThumbnail().equals("self"))
                {
                    thumbnail = "http://i.imgur.com/LAOXHdN.png";
                }
                else if(postData.getThumbnail().equals("nsfw"))
                {
                    thumbnail = "http://i.imgur.com/cgDTKfV.png";
                }
                else
                {
                    thumbnail = postData.getThumbnail();
                }
            }
            else
            {
                thumbnail = null;
            }

            List<RedditComment> comments = new ArrayList<RedditComment>();

            thread = new RedditThread(title, score, link, domain, postDate, user, comments,
                    sub, id36, num_comments, is_self, thumbnail, fullname);
            tmpList.add(thread);
        }

        return tmpList;
    }

    public AuthState authorize(String state, String code)
    {
        String auth = getBasicAuthString();

        RedditAuthStateProxy authStateProxy = aService.authorize(auth, REDIRECT_URI, code, "authorization_code");

        AuthState a = new AuthState();
        a.setAccess_token(authStateProxy.getAccess_token());
        a.setToken_type(authStateProxy.getToken_type());
        a.setExpires_in(DateTime.now(DateTimeZone.UTC).plusMinutes(58));
        a.setScope(authStateProxy.getScope());
        a.setRefresh_token(authStateProxy.getRefresh_token());

        this.redditAuthState = a;
        return a;
    }

    public RedditProfile getProfile()
    {
        if(redditAuthState.getAccess_token() != null)
        {
            return oService.me();
        }
        else
        {
            System.out.println("NULL TOKEN");
            return null;
        }
    }

    public MyRedditKarma getKarma()
    {
        System.out.println(oService.myKarma());
        return oService.myKarma();
    }

    public boolean voteItem(int vote, RedditThing thing)
    {
        if(redditAuthState.getAccess_token() != null)
        {
            oService.vote(vote, thing.getFullname());
            return true;
        }
        else
        {
            System.out.println("NULL TOKEN");
            return false;
        }
    }

    public void oauthCallTest()
    {

        System.out.println("Access token: " + redditAuthState.getAccess_token());
        System.out.println("Type: " + redditAuthState.getToken_type());
        System.out.println("Expire: " + redditAuthState.getExpires_in());
        System.out.println("Scope: " + redditAuthState.getScope());
        System.out.println("Refresh token: " + redditAuthState.getRefresh_token());
        refreshToken();
    }

    private boolean refreshToken()
    {
        String auth = getBasicAuthString();


        String refresh_token = redditAuthState.getRefresh_token();

        if(refresh_token.length() > 0)
        {
            RedditAuthStateProxy p = aService.refresh(auth, refresh_token, "refresh_token");

            if(p != null)
            {
                this.redditAuthState.setAccess_token(p.getAccess_token());
                this.redditAuthState.setToken_type(p.getToken_type());
                this.redditAuthState.setExpires_in(DateTime.now(DateTimeZone.UTC).plusMinutes(58));
                this.redditAuthState.setScope(p.getScope());

                System.out.println("REFRESH: Token refresh success!");
                return true;
            }
            else
            {
                System.out.println("REFRESH: Refreshed authstate returned null!");
                return false;
            }
        }
        else
        {
            System.out.println("REFRESH: Original refresh_token is null!");
            return false;
        }
    }

    public boolean isCaptchaNeeded()
    {
        return oService.needsCaptcha();
    }

    public String getIden()
    {
        NewCaptchaResponse n = oService.newCaptcha();
        return "";
    }

    public String getCaptcha(String iden)
    {
        CaptchaIdenResponse c = oService.iden(iden);
        return "";
    }

    private String getBasicAuthString()
    {
        String auth = new String(Base64.encodeToString((CLIENT_ID + ":").getBytes(), Base64.DEFAULT));
        return "Basic " + auth;
    }

    public void submitSelfPost(String subreddit, String title, String text, String captcha, String iden)
    {
        if(captcha == null || iden == null)
        {
            captcha = "";
            iden = "";
        }

        oService.submitPost("json", captcha, "", iden, RedditPostKind.SELF, true, true, subreddit, text, RedditPostThen.COMMENTS, title, "");
    }

    public void submitLinkPost(String subreddit, String title, String url, String captcha, String iden)
    {
        if(captcha == null || iden == null)
        {
            captcha = "";
            iden = "";
        }

        oService.submitPost("json", captcha, "", iden, RedditPostKind.LINK, true, true, subreddit, "", RedditPostThen.COMMENTS, title, url);
    }

    public AuthState getRedditAuthState() {
        return redditAuthState;
    }

    public void setRedditAuthState(AuthState redditAuthState) {
       this.redditAuthState = redditAuthState;
    }

    @Override
    public void authStateChanged()
    {
        System.out.println("ACCESS TOKEN HAS BEEN CHANGED");
    }
}
