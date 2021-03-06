package com.leddit.leddit.api;

import android.util.Base64;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.leddit.leddit.RedditComment;
import com.leddit.leddit.RedditThread;
import com.leddit.leddit.api.output.MyRedditKarma;
import com.leddit.leddit.api.output.NewCaptchaResponse;
import com.leddit.leddit.api.output.RedditAuthStateProxy;
import com.leddit.leddit.api.output.RedditCommentData;
import com.leddit.leddit.api.output.RedditCommentObject;
import com.leddit.leddit.api.output.RedditObject;
import com.leddit.leddit.api.output.RedditPostData;
import com.leddit.leddit.api.output.RedditPostResponse;
import com.leddit.leddit.api.output.RedditProfile;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.JacksonConverter;

/**
 * Created by Jonah on 21.11.2014.
 */

/*
    Public interface for Reddit API calls
    Container for all APIservices and RESTadapters
*/

public class RedditApi {

    //Define client id and redirect url for calls
    public static final String CLIENT_ID = "TPdgxXER-lcR8Q";
    public static final String REDIRECT_URI = "http://google.fi";

    //Define services
    private RedditOauthApiService oService;
    private RedditAuthorizationService aService;
    private RedditApiService rService;

    private AuthState redditAuthState;

    //Singleton instance
    private static RedditApi instance = null;

    public static RedditApi getInstance() {
        if (instance == null) {
            instance = new RedditApi();
        }
        return instance;
    }

    //Singleton
    private RedditApi()
    {
        redditAuthState = new AuthState();

        //Create new objectmapper for JSON converter
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        //Create new converter for adapters
        JacksonConverter converter = new JacksonConverter(mapper);

        //Create adapter for normal api
        RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://www.reddit.com/").setConverter(converter)
                .build();

        //Create adapter for secure api
        RestAdapter restAdapter2 = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("https://ssl.reddit.com/").setConverter(converter)
                .build();

        //Create new request interceptor, so we can inject bearer token to outgoing requests
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                System.out.println("Oauth API Request intercepted and bearer token (" + redditAuthState.getAccess_token() + ") injected!");

                if(Utility.hasExpired(redditAuthState.getExpires_in()))
                {
                    System.out.println("Token needs refresh, doing that now!");
                    refreshToken(); //Refresh bearer token
                }
                else
                {
                    System.out.println("Token not yet expired!");
                }

                //Inject token to secure request
                request.addHeader("Authorization", "bearer " + redditAuthState.getAccess_token());
            }
        };

        //Create adapter for oauth api
        RestAdapter restAdapter3 = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(requestInterceptor)
                .setEndpoint("https://oauth.reddit.com/").setConverter(converter)
                .build();


        //Instantiate services
        rService = restAdapter.create(RedditApiService.class);
        aService = restAdapter2.create(RedditAuthorizationService.class);
        oService = restAdapter3.create(RedditOauthApiService.class);

    }

    private List<AuthStateListener> listeners = new ArrayList<AuthStateListener>();

    private List<RedditComment> tmpComments;
    private List<RedditCommentObject> tmpCommentList;

    public List<RedditComment> getComments(RedditThread thread, String sorting)
    {
        tmpComments = new ArrayList<RedditComment>();
        List<RedditCommentObject> tmpCommentList;

        //If sorting not specified, fall back to "hot"
        if(sorting == null || sorting.length() == 0)
            sorting = "hot";

        //Use different service depending on auth state
        if (isAuth()) {
            tmpCommentList = oService.listThreadComments(thread.getSubreddit(), thread.getId36(), sorting);
        } else {
            tmpCommentList = rService.listThreadComments(thread.getSubreddit(), thread.getId36(), sorting);
        }

        //Construct a list of RedditComment -objects
        for (int i = 0; i < tmpCommentList.size(); i++)
        {
            RedditCommentData data = tmpCommentList.get(i).getData();

            for (int j = 0; j < data.getChildren().size(); j++)
            {
                int depth = 0;

                DateTime commentPostDate = new DateTime(DateTimeZone.UTC);
                commentPostDate.plus(data.getChildren().get(j).getData().getCreated_utc());

                int f_likes = checkLikes(data.getChildren().get(j).getData().isLikes());


                tmpComments.add(new RedditComment(depth, data.getChildren().get(j).getData().getAuthor(),
                        data.getChildren().get(j).getData().getScore(), commentPostDate,
                        data.getChildren().get(j).getData().getBody(),
                        Utility.parseCommentType(data.getChildren().get(i).getKind()),
                        "t1_" + data.getChildren().get(j).getData().getId(), f_likes));

                if (data.getChildren().get(j).getData().getReplies().getData() != null)
                {
                    recursive(data.getChildren().get(j).getData().getReplies(), depth);
                }
            }
        }

        return tmpComments;
    }

    //Comments have depth and comments can have comments, so we need to recursively loop comments
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

            int f_likes = checkLikes(commentObject.getData().getChildren().get(i).getData().isLikes());

            tmpComments.add(new RedditComment(depth, commentObject.getData().getChildren().get(i).getData().getAuthor(),
                    commentObject.getData().getChildren().get(i).getData().getScore(), commentPostDate,
                    commentObject.getData().getChildren().get(i).getData().getBody(),
                    Utility.parseCommentType(commentObject.getData().getChildren().get(i).getKind()),
                    "t1_" + commentObject.getData().getChildren().get(i).getData().getId(), f_likes));

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
            if(isAuth()) {
                o = oService.listSubreddit(subreddit, sorting);
            }else
            {
                o = rService.listSubreddit(subreddit, sorting);
            }
        }
        else
        {
            if(isAuth()) {
                o = oService.listSubredditWithTime(subreddit, sorting, sorting, timeScale);
            }else
            {
                o = rService.listSubredditWithTime(subreddit, sorting, sorting, timeScale);
            }
        }


        RedditThread thread;

        //Construct a list of RedditThread objects
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
            Boolean likes = postData.getLikes();
            int f_likes = checkLikes(likes);

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
                    sub, id36, num_comments, is_self, thumbnail, fullname, f_likes);
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
            if(isAuth()) {
                o = oService.frontPage(sorting);
            }else
            {
                o = rService.frontPage(sorting);
            }
        }
        else
        {
            if(isAuth()) {
                o = oService.frontPageWithTimescale(sorting, sorting, timeScale);
            }else
            {
                o = rService.frontPageWithTimescale(sorting, sorting, timeScale);
            }
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
            Boolean likes = postData.getLikes();
            int f_likes = checkLikes(likes);

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
                    sub, id36, num_comments, is_self, thumbnail, fullname, f_likes);
            tmpList.add(thread);
        }

        return tmpList;
    }

    //Convert Boolean to int between -1 and 1
    private int checkLikes(Boolean likes)
    {
        int f_likes;

        if(likes == null)
        {
            f_likes = 0;
        }
        else if(likes == true)
        {
            f_likes = 1;
        }
        else
        {
            f_likes = -1;
        }

        return f_likes;
    }

    public AuthState authorize(String state, String code)
    {
        String auth = getBasicAuthString();
        RedditAuthStateProxy authStateProxy;

        authStateProxy = aService.authorize(auth, REDIRECT_URI, code, "authorization_code");


        AuthState a = new AuthState();
        a.setAccess_token(authStateProxy.getAccess_token());
        a.setToken_type(authStateProxy.getToken_type());
        a.setExpires_in(DateTime.now(DateTimeZone.UTC).plusMinutes(58));
        a.setScope(authStateProxy.getScope());
        a.setRefresh_token(authStateProxy.getRefresh_token());

        this.redditAuthState = a;


        //Inform authstate listeners that the authstate has changed
        for(int i = 0; i < listeners.size(); i++)
        {
            listeners.get(i).onAuthStateChanged();
        }

        return a;
    }

    public RedditProfile getProfile()
    {
        if(redditAuthState.getAccess_token() != null)
        {
            RedditProfile p;

            p = oService.me();

            p.setActual_created_utc(new DateTime(p.getCreated_utc() * 1000, DateTimeZone.UTC));
            return p;
        }
        else
        {
            System.out.println("NULL TOKEN");
            return null;
        }
    }

    public MyRedditKarma getKarma()
    {
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

        if(isCaptchaNeeded())
        {
            //System.out.println(getCaptcha());
        }
        else
        {
            System.out.println("Captcha not needed");
        }
    }

    private boolean refreshToken()
    {
        String auth = getBasicAuthString();


        String refresh_token = redditAuthState.getRefresh_token();

        if(refresh_token.length() > 0)
        {
            RedditAuthStateProxy p;

            p = aService.refresh(auth, refresh_token, "refresh_token");

            if(p != null)
            {
                this.redditAuthState.setAccess_token(p.getAccess_token());
                this.redditAuthState.setToken_type(p.getToken_type());
                this.redditAuthState.setExpires_in(DateTime.now(DateTimeZone.UTC).plusMinutes(58));
                this.redditAuthState.setScope(p.getScope());

                for(int i = 0; i < listeners.size(); i++)
                {
                    listeners.get(i).onAuthStateChanged();
                }

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
        return oService.newCaptcha().getIden();
    }

    public String getCaptcha(String iden)
    {
        return "http://www.reddit.com/captcha/" + iden + ".png";
    }

    private String getBasicAuthString()
    {
        String auth = new String(Base64.encodeToString((CLIENT_ID + ":").getBytes(), Base64.DEFAULT));
        return "Basic " + auth;
    }

    public boolean submitSelfPost(String subreddit, String title, String text, String captcha, String iden)
    {
        if(captcha == null || iden == null)
        {
            captcha = "";
            iden = "";
        }

        RedditPostResponse r;

        r = oService.submitPost("json", captcha, "", iden, RedditPostKind.SELF, true, true, subreddit, text, RedditPostThen.COMMENTS, title, "");

        if(r.getJson().getErrors() != null)
        {
            if(r.getJson().getErrors().size() > 0)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return true;
        }
    }

    public boolean submitLinkPost(String subreddit, String title, String url, String captcha, String iden)
    {
        if(captcha == null || iden == null)
        {
            captcha = "";
            iden = "";
        }

        RedditPostResponse r;

        r = oService.submitPost("json", captcha, "", iden, RedditPostKind.LINK, true, true, subreddit, "", RedditPostThen.COMMENTS, title, url);

        if(r.getJson().getErrors() != null)
        {
            if(r.getJson().getErrors().size() > 0)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return true;
        }
    }

    private boolean isAuth()
    {
        if(redditAuthState == null ||redditAuthState.getAccess_token() == null ||redditAuthState.getAccess_token().length() == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public void logout()
    {
        this.setRedditAuthState(null);
    }

    public AuthState getRedditAuthState() {
        return redditAuthState;
    }

    public void setRedditAuthState(AuthState redditAuthState) {
       this.redditAuthState = redditAuthState;
    }

    public void addOnAuthStateChangedListener(AuthStateListener listener)
    {
        if (listeners == null)
        {
            listeners = new ArrayList<AuthStateListener>();
        }

        listeners.add(listener);
    }
}
