package com.leddit.leddit;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.leddit.leddit.api.AuthAttempt;
import com.leddit.leddit.api.AuthState;
import com.leddit.leddit.api.RedditApi;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;

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

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

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
    }

    private BroadcastReceiver openCommentsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("receiver", "Activity got message");

            ThreadListFragment threadListFragment = (ThreadListFragment)getFragmentManager().findFragmentByTag("threadListFragment");
            //threadListFragment

            int threadPosition = intent.getIntExtra("threadPosition", 0);
            ViewComments(threadListFragment.threads.get(threadPosition));
        }
    };

    private BroadcastReceiver authorizedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("receiver", "Got authorized broadcast");

            ViewSubreddit(null, "hot");
        }
    };

    @Override
    public void onNavigationDrawerSubredditSelected(String listName, String item) {
        if (listName == "subreddits")
        {
            ViewSubreddit(item, "hot");
        }
        else if (listName == "actions")
        {
            // TODO: Checking via displayed string is a bad way to do this
            if (item == "Login")
            {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AuthorizeFragment.newInstance(), "threadListFragment")
                        .commit();
            }
            else if (item == "Front page")
            {
                ViewSubreddit(null, "hot");
            }
        }
    }

    public void ViewSubreddit(String subreddit, String sorting)
    {
        // TODO: Use sorting

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ThreadListFragment.newInstance(subreddit), "threadListFragment")
                .commit();
    }

    public void ViewComments(RedditThread thread)
    {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, CommentsFragment.newInstance(thread))
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
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

    public static class ThreadListFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String SUBREDDIT_NAME = "section_number";

        private ListView listView;
        public List<RedditThread> threads;
        private RedditThreadListAdapter adapter;
        private GetThreadsTask threadsTask;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         * @param subredditName
         */
        public static ThreadListFragment newInstance(String subredditName) {
            ThreadListFragment fragment = new ThreadListFragment();
            Bundle args = new Bundle();
            args.putString(SUBREDDIT_NAME, subredditName);
            fragment.setArguments(args);

            return fragment;
        }

        public ThreadListFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_threadlist, container, false);

            listView = (ListView)rootView.findViewById(R.id.thread_list);

            // TODO: Use listView.setOnItemClickListener instead of doing click events in list adapter

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            String subredditName = getArguments().getString(SUBREDDIT_NAME);
            ((MainActivity) activity).onSectionAttached(subredditName);

            Refresh();
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
            threadsTask.execute(getArguments().getString(SUBREDDIT_NAME), "hot");
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
                    return RedditApi.getInstance().getFrontpage(params[1]);
                }

                return RedditApi.getInstance().getThreads(params[0], params[1]);
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
        private static final String SUBREDDIT_NAME = "section_number";

        private View threadInfo;
        private ListView listView;
        //private RedditThreadListAdapter adapter;
        private RedditThread thread;

        public static CommentsFragment newInstance(RedditThread thread) {
            CommentsFragment fragment = new CommentsFragment();
            Bundle args = new Bundle();
            //args.putString(SUBREDDIT_NAME, subredditName);
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

            // Find views
            TextView title = (TextView)threadInfo.findViewById(R.id.title);
            TextView score = (TextView)threadInfo.findViewById(R.id.score);
            TextView domain = (TextView)threadInfo.findViewById(R.id.domain);
            TextView time = (TextView)threadInfo.findViewById(R.id.time);
            TextView user = (TextView)threadInfo.findViewById(R.id.user);
            final TextView comments = (TextView)threadInfo.findViewById(R.id.comments);

            // Set values
            title.setText(thread.getTitle());
            score.setText(Integer.toString(thread.getScore()));
            domain.setText(thread.getDomain());
            time.setText(Hours.hoursBetween(DateTime.now(DateTimeZone.UTC), thread.getPostDate()).getHours() + "h");
            user.setText(thread.getUser());
            comments.setText(thread.getCommentCount() + " comments");

            // Populate list
            listView = (ListView)rootView.findViewById(R.id.comment_list);

            //adapter = new RedditThreadListAdapter(listView.getContext(), threads);
            //listView.setAdapter(adapter);

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getString("asd"));
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

            return fragment;
        }

        public AuthorizeFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_authorize, container, false);

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
                //intent.putExtra("threadPosition", finalPosition);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                Log.d("UserAuthTask", "END");
            }
        }
    }

}
