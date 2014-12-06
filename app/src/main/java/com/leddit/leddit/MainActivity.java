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

        LocalBroadcastManager.getInstance(this).registerReceiver(
                messageReceiver,
                new IntentFilter("open-comments"));
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("receiver", "Activity got message");

            ThreadListFragment threadListFragment = (ThreadListFragment)getFragmentManager().findFragmentByTag("threadListFragment");
            //threadListFragment

            int threadPosition = intent.getIntExtra("threadPosition", 0);
            ViewComments(threadListFragment.threads.get(threadPosition));
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
            if (item == "Login")
            {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AuthorizeFragment.newInstance(), "threadListFragment")
                        .commit();
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
        actionBar.setTitle(mTitle);
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

            //getFragmentManager().findFragmentByTag("threadListFragment");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
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

            new GetThreadsTask(this).execute(subredditName, "hot");
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
                    if (state != null && code != null)
                    {
                        Log.d("WEBVIEW state", state);
                        Log.d("WEBVIEW code", code);
                        AuthState authState = attempt.userAuthProcess(state, code);
                        done = true;
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
    }

}
