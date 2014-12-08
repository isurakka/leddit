package com.leddit.leddit;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.leddit.leddit.api.AuthAttempt;
import com.leddit.leddit.api.AuthState;
import com.leddit.leddit.api.AuthStateListener;
import com.leddit.leddit.api.RedditApi;
import com.leddit.leddit.api.RedditThing;
import com.leddit.leddit.api.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init image loader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
            .build();
        ImageLoader.getInstance().init(config);


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        FileInputStream fis = null;
        try {
            fis = openFileInput("authState");
            ObjectInputStream is = new ObjectInputStream(fis);
            AuthState authState = (AuthState)is.readObject();
            is.close();
            fis.close();

            if (authState != null)
            {
                RedditApi.getInstance().setRedditAuthState(authState);
                mNavigationDrawerFragment.RemoveLogin();
                Log.d("authState", "Loaded authState from file");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        RedditApi.getInstance().addOnAuthStateChangedListener(new AuthStateListener() {
            @Override
            public void onAuthStateChanged() {
                saveAuthState(RedditApi.getInstance().getRedditAuthState());
            }
        });

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.registerReceiver(
                openCommentsReceiver,
                new IntentFilter("open-comments"));
        bm.registerReceiver(
                authorizedReceiver,
                new IntentFilter("authorized"));
        bm.registerReceiver(
                voteReceiver,
                new IntentFilter("vote-thread"));

        ViewSubreddit(null, "hot", null);
    }

    private BroadcastReceiver openCommentsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("receiver", "Activity got message");

            ThreadListFragment threadListFragment = (ThreadListFragment)getFragmentManager().findFragmentByTag("threadListFragment");
            //threadListFragment

            int threadPosition = intent.getIntExtra("threadPosition", 0);
            ViewComments(threadListFragment.threads.get(threadPosition), "hot", null);
        }
    };

    private BroadcastReceiver authorizedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("receiver", "Got authorized broadcast");

            AuthState authState = intent.getParcelableExtra("authState");

            saveAuthState(authState);

            mNavigationDrawerFragment.RemoveLogin();

            ViewSubreddit(null, "hot", null);
        }
    };

    VoteTask voteTask;
    private BroadcastReceiver voteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("receiver", "Got authorized broadcast");

            ThreadListFragment threadListFragment = (ThreadListFragment)getFragmentManager().findFragmentByTag("threadListFragment");
            int threadPosition = intent.getIntExtra("threadPosition", 0);
            RedditThread thread = threadListFragment.threads.get(threadPosition);
            int vote = intent.getIntExtra("vote", 0);

            if (thread.getLikes() == vote && thread.getLikes() != 0)
            {
                vote = 0;
            }

            if (voteTask != null && voteTask.getStatus() == AsyncTask.Status.RUNNING)
            {
                voteTask.cancel(true);
            }

            voteTask = new VoteTask(thread);
            voteTask.execute(vote);

            Log.d("receiver", "Got vote" + new Integer(vote).toString());
        }
    };

    private void saveAuthState(AuthState authState)
    {
        try {
            FileOutputStream fos = MainActivity.this.openFileOutput("authState", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(authState);
            os.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNavigationDrawerSubredditSelected(String listName, String item) {
        if (listName == "subreddits")
        {
            ViewSubreddit(item, "hot", null);
        }
        else if (listName == "actions")
        {
            // TODO: Checking via displayed string is a bad way to do this
            if (item == "Login")
            {
                Log.d("authorizeFragment", "GOING");

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AuthorizeFragment.newInstance(), "authorizeFragment")
                        .commit();
            }
            else if (item == "Front page")
            {
                ViewSubreddit(null, "hot", null);
            }
            else if (item == "Test")
            {
                new TestTask().execute();
            }
        }
    }

    private String lastSubreddit;

    private enum FragmentState
    {
        COMMENTS,
        SUBREDDIT,
        OTHER
    }

    private FragmentState getFragmentState()
    {
        FragmentManager fm = getFragmentManager();

        if (fm.findFragmentByTag("threadListFragment") != null) {
            return FragmentState.SUBREDDIT;
        }

        if (fm.findFragmentByTag("commentsFragment") != null) {
            return FragmentState.COMMENTS;
        }

        return FragmentState.OTHER;
    }

    public void ViewSubreddit(String subreddit, String sorting, String timescale)
    {
        // TODO: Use sorting

        Log.d("subreddit", subreddit != null ? subreddit : "null");
        Log.d("sorting", sorting != null ? sorting : "null");
        Log.d("timescale", timescale != null ? timescale : "null");

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ThreadListFragment.newInstance(subreddit, sorting, timescale), "threadListFragment")
                .commit();

        lastSubreddit = subreddit;
    }

    public void ViewComments(RedditThread thread, String sorting, String timescale)
    {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, CommentsFragment.newInstance(thread, sorting, timescale), "commentsFragment")
                .addToBackStack(null)
                .commit();
    }

    public void onSectionAttached(String item) {
        mTitle = item;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);

        // TODO: The string should probably not be hard coded here
        actionBar.setTitle(mTitle == null ? "Front page" : mTitle);
    }

    private String lastSorting = "hot";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();

            final Spinner sortSpinner = (Spinner)menu.findItem(R.id.menuSort).getActionView();
            sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    CharSequence item = (CharSequence)sortSpinner.getItemAtPosition(position);
                    Log.d("spinner click", item.toString());

                    if (item == lastSorting || getFragmentState() == FragmentState.OTHER)
                    {
                        return;
                    }

                    RedditSortData sortData = LayoutUtility.sortingOptionToSortData(item.toString());
                    ViewSubreddit(lastSubreddit, sortData.sorting, sortData.timescale);
                    lastSorting = sortData.sorting;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {

            ThreadListFragment threadList = (ThreadListFragment)getFragmentManager().findFragmentByTag("threadListFragment");
            if (threadList != null)
            {
                threadList.Refresh();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(openCommentsReceiver);
        super.onDestroy();
    }

    class VoteTask extends AsyncTask<Integer, Void, Boolean>
    {
        RedditThing thing;
        Integer oldLikes = null;

        public VoteTask(RedditThing thing)
        {
            this.thing = thing;
        }

        @Override
        protected Boolean doInBackground(Integer... vote) {
            Log.d("VoteTask", "START");

            oldLikes = thing.getLikes();
            thing.setLikes(vote[0]);
            return RedditApi.getInstance().voteItem(vote[0], thing);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (oldLikes != null)
            {
                thing.setLikes(oldLikes);
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            Log.d("VoteTask", "END " + success.toString());
        }
    }

    class TestTask extends AsyncTask<Void, Void, Void>
    {
        public TestTask()
        {

        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("TestTask", "oauthCallTest START");
            RedditApi.getInstance().oauthCallTest();
            return null;
        }

        @Override
        protected void onPostExecute(Void asd) {
            Log.d("TestTask", "oauthCallTest END");
        }
    }

    public static class ThreadListFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String SUBREDDIT_NAME = "subreddit_name";
        private static final String SORTING = "subreddit_sorting";
        private static final String TIMESCALE = "subreddit_timescale";

        private ListView listView;
        public List<RedditThread> threads;
        private RedditThreadListAdapter adapter;
        private GetThreadsTask threadsTask;

        public static ThreadListFragment newInstance(String subreddit, String sorting, String timescale) {
            ThreadListFragment fragment = new ThreadListFragment();
            Bundle args = new Bundle();
            args.putString(SUBREDDIT_NAME, subreddit);
            args.putString(SORTING, sorting);
            args.putString(TIMESCALE, timescale);
            fragment.setArguments(args);

            return fragment;
        }

        public ThreadListFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_threadlist, container, false);

            // TODO: Use listView.setOnItemClickListener instead of doing click events in list adapter
            listView = (ListView)rootView.findViewById(R.id.thread_list);

            if (threads != null)
            {
                adapter = new RedditThreadListAdapter(listView.getContext(), threads);
                listView.setAdapter(adapter);
            }
            else
            {
                Refresh();
            }

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            String subredditName = getArguments().getString(SUBREDDIT_NAME);
            ((MainActivity) activity).onSectionAttached(subredditName);
        }

        @Override
        public void onDetach() {
            if (threadsTask != null && threadsTask.getStatus() == AsyncTask.Status.RUNNING)
            {
                threadsTask.cancel(true);
            }
            super.onDetach();
        }

        public void Refresh()
        {
            if (threadsTask != null && threadsTask.getStatus() == AsyncTask.Status.RUNNING)
            {
                return;
            }

            threadsTask = new GetThreadsTask(this);
            threadsTask.execute(
                    getArguments().getString(SUBREDDIT_NAME),
                    getArguments().getString(SORTING),
                    getArguments().getString(TIMESCALE));
        }

        class GetThreadsTask extends AsyncTask<String, Void, List<RedditThread>>
        {
            ThreadListFragment fragment;

            public GetThreadsTask(ThreadListFragment fragment)
            {
                this.fragment = fragment;
            }

            @Override
            protected List<RedditThread> doInBackground(String... params) {
                if (params[0] == null) {
                    return RedditApi.getInstance().getFrontpage(params[1], params[2]);
                }

                return RedditApi.getInstance().getThreads(params[0], params[1], params[2]);
            }

            @Override
            protected void onPostExecute(List<RedditThread> redditThreads) {
                fragment.threads = redditThreads;
                fragment.adapter = new RedditThreadListAdapter(listView.getContext(), threads);
                fragment.listView.setAdapter(adapter);
            }
        }
    }

    public static class CommentsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        //private static final String THREAD_NAME = "thread";
        private static final String SORTING_NAME = "sorting";
        private static final String TIMESCALE_NAME = "timescale";

        private View threadInfo;
        private ListView listView;
        private RedditCommentListAdapter adapter;
        private RedditThread thread;
        private CommentsLoadTask commentsTask;

        public static CommentsFragment newInstance(RedditThread thread, String sorting, String timescale) {
            CommentsFragment fragment = new CommentsFragment();
            Bundle args = new Bundle();
            args.putString(SORTING_NAME, sorting);
            args.putString(TIMESCALE_NAME, timescale);
            fragment.setArguments(args);

            fragment.thread = thread;

            return fragment;
        }

        public CommentsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_comments, container, false);

            threadInfo = rootView.findViewById(R.id.thread_info);

            LayoutUtility.SetThreadInfo(thread, threadInfo);

            // Populate list
            listView = (ListView)rootView.findViewById(R.id.comment_list);

            return rootView;
        }

        public void Refresh()
        {
            if (commentsTask != null && commentsTask.getStatus() == AsyncTask.Status.RUNNING)
            {
                return;
            }

            commentsTask = new CommentsLoadTask(this);
            commentsTask.execute();
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getString("asd"));

            Refresh();
        }

        @Override
        public void onDetach() {
            if (commentsTask != null && commentsTask.getStatus() == AsyncTask.Status.RUNNING)
            {
                commentsTask.cancel(true);
            }
            super.onDetach();
        }

        class CommentsLoadTask extends AsyncTask<Void, Void, List<RedditComment>>
        {
            CommentsFragment fragment;

            public CommentsLoadTask(CommentsFragment fragment)
            {
                this.fragment = fragment;
            }

            @Override
            protected List<RedditComment> doInBackground(Void... asd) {
                Log.d("CommentsLoadTask", "START");

                return RedditApi.getInstance().getComments(fragment.thread);
            }

            @Override
            protected void onPostExecute(List<RedditComment> comments) {
                fragment.thread.setComments(comments);
                fragment.adapter = new RedditCommentListAdapter(listView.getContext(), fragment.thread);
                fragment.listView.setAdapter(adapter);

                Log.d("CommentsLoadTask", "END");
            }
        }
    }

    public static class AuthorizeFragment extends Fragment {

        AuthAttempt attempt;

        public static AuthorizeFragment newInstance() {
            AuthorizeFragment fragment = new AuthorizeFragment();
            Bundle args = new Bundle();
            //args.putString(SUBREDDIT_NAME, subredditName);
            fragment.setArguments(args);

            fragment.attempt = new AuthAttempt();

            Log.d("authorizeFragment", "newInstance");

            return fragment;
        }

        public AuthorizeFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_authorize, container, false);

            Log.d("authorizeFragment", "onCreateView");

            final WebView webView = (WebView)rootView.findViewById(R.id.webView);
            webView.setWebViewClient(new WebViewClient() {

                boolean done = false;

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Log.d("WEBVIEW", url);

                    if (done)
                        return true;

                    // SUCCESS
                    // http://google.fi/?state=asd&code=asd

                    // DENIED
                    // http://www.google.fi/?state=asd&error=access_denied

                    Uri uri = Uri.parse(url);
                    String state = uri.getQueryParameter("state");
                    String code = uri.getQueryParameter("code");
                    String error = uri.getQueryParameter("error");
                    Log.d("queryparam state", state != null ? state : "null");
                    Log.d("queryparam code", code != null ? code : "null");
                    Log.d("queryparam error", error != null ? error : "null");
                    if (state != null && code != null)
                    {
                        Log.d("WEBVIEW state", state);
                        Log.d("WEBVIEW code", code);
                        new UserAuthTask(AuthorizeFragment.this).execute(new UserAuthTaskParams(attempt, state, code));
                        done = true;
                        return true;
                    }
                    else if (state != null && error != null && error.equalsIgnoreCase("access_denied"))
                    {
                        Toast.makeText(getActivity(), "Why you do this?", Toast.LENGTH_LONG).show();
                        return true;
                    }

                    return false;
                }
            });
            webView.loadUrl(attempt.getAuthUrl());

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);



            ((MainActivity) activity).onSectionAttached(
                    getArguments().getString("asd"));
        }

        class UserAuthTaskParams
        {
            AuthAttempt attempt;
            String state;
            String code;

            UserAuthTaskParams(AuthAttempt attempt, String state, String code)
            {
                this.attempt = attempt;
                this.state = state;
                this.code = code;
            }
        }

        class UserAuthTask extends AsyncTask<UserAuthTaskParams, Void, AuthState>
        {
            AuthorizeFragment fragment;

            public UserAuthTask(AuthorizeFragment fragment)
            {
                this.fragment = fragment;
            }

            @Override
            protected AuthState doInBackground(UserAuthTaskParams... params) {
                Log.d("UserAuthTask", "START");
                return params[0].attempt.userAuthProcess(params[0].state, params[0].code);
            }

            @Override
            protected void onPostExecute(AuthState authState) {
                if (authState == null)
                {
                    Toast.makeText(getActivity(), "Authorization failed!", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(getActivity(), "Authorized!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent("authorized");
                intent.putExtra("authState", (android.os.Parcelable) authState);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                Log.d("UserAuthTask", "END");
            }
        }
    }

}
