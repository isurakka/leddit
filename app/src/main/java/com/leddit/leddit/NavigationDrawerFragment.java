package com.leddit.leddit;


import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    public ArrayList<String> defaultActions;
    public ArrayList<String> actions;

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private EditText mDrawerGoto;
    private ListView mDrawerSubredditListView;
    private ListView mDrawerActionListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedSubredditPosition = 0;
    private boolean mFromSavedInstanceState;

    public NavigationDrawerFragment() {
        actions = new ArrayList<String>() {{
                add("Front page");
                add("Login");
                //add("Test");
        }};

        defaultActions = new ArrayList<String>(actions);
    }

    public void RemoveLogin()
    {
        ListView view = (ListView)getView().findViewById(R.id.actions);
        ArrayAdapter<String> adapter = (ArrayAdapter<String>)view.getAdapter();

        int loginPosition = actions.indexOf("Login");
        actions.add(loginPosition, "Logout");
        actions.add(loginPosition, "Profile");
        actions.add(loginPosition, "Submit");
        actions.remove("Login");
        adapter.notifyDataSetChanged();
    }

    public void ResetActions()
    {
        ListView view = (ListView)getView().findViewById(R.id.actions);
        ArrayAdapter<String> adapter = (ArrayAdapter<String>)view.getAdapter();
        actions.clear();
        for (int i = 0; i < defaultActions.size(); i++) {
            actions.add(defaultActions.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentSelectedSubredditPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // TODO: Select either the default item (0) or the last selected item.
        //selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        mDrawerGoto = (EditText)rootView.findViewById(R.id.gotoSubreddit);
        mDrawerGoto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL &&
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                        mCallbacks != null) {
                    mCallbacks.onNavigationDrawerGotoAction(mDrawerGoto.getText().toString());
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mDrawerGoto.getWindowToken(), 0);
                    mDrawerLayout.closeDrawer(mFragmentContainerView);
                    return true;
                }
                return false;
            }
        });

        mDrawerSubredditListView = (ListView)rootView.findViewById(R.id.subreddits);
        mDrawerActionListView = (ListView)rootView.findViewById(R.id.actions);

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem((ListView)parent, position, (String) parent.getItemAtPosition(position));
            }
        };
        mDrawerSubredditListView.setOnItemClickListener(listener);
        mDrawerActionListView.setOnItemClickListener(listener);

        mDrawerSubredditListView.setAdapter(new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                new String[]{
                        "funny",
                        "pics",
                        "AskReddit",
                        "todayilearned",
                        "worldnews",
                        "science",
                        "blog",
                        "IAmA",
                        "videos",
                        "gaming",
                        "movies",
                        "Music",
                        "aww",
                        "technology",
                        "bestof",
                        "WTF",
                        "news",
                        "AdviceAnimals",
                        "gifs",
                        "askscience",
                        "test",
                }));
        mDrawerSubredditListView.setItemChecked(mCurrentSelectedSubredditPosition, true);

        mDrawerActionListView.setAdapter(new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                actions));

        return rootView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (mDrawerGoto != null)
                {
                    mDrawerGoto.setText("");
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(ListView list, int position, String item) {
        if (list == mDrawerSubredditListView) {
            mCurrentSelectedSubredditPosition = position;
            if (mDrawerSubredditListView != null) {
                mDrawerSubredditListView.setItemChecked(position, true);
            }
            if (mCallbacks != null) {
                mCallbacks.onNavigationDrawerSubredditSelected("subreddits", item);
            }
        } else if (list == mDrawerActionListView) {
            if (mCallbacks != null) {
                mCallbacks.onNavigationDrawerSubredditSelected("actions", item);
            }
        }

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedSubredditPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerSubredditSelected(String listName, String item);
        void onNavigationDrawerGotoAction(String subreddit);
    }
}
