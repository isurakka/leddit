package com.leddit.leddit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.leddit.leddit.api.Utility;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;

import java.util.Date;
import java.util.List;

/**
 * Created by Iiro on 30.11.2014.
 */
public class RedditThreadListAdapter extends BaseAdapter {

    Context context;
    List<RedditThread> threads;
    static int id = 0;

    public RedditThreadListAdapter(Context context, List<RedditThread> threads)
    {
        this.context = context;
        this.threads = threads;
    }

    @Override
    public int getCount() {
        return threads.size();
    }

    @Override
    public Object getItem(int position) {
        return threads.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_thread, parent, false);
        }

        final View threadInfo = convertView.findViewById(R.id.thread_info);

        final RedditThread thread = threads.get(position);
        final int finalPosition = position;

        LayoutUtility.SetThreadInfo(thread, threadInfo);

        final ViewFlipper flipper = (ViewFlipper)convertView.findViewById(R.id.viewFlipper);

        // TODO: This is not the correct place to do event stuff
        flipper.setOnTouchListener(new View.OnTouchListener() {

            float x;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN)
                {
                    Log.d("flipper touch", "DOWN");
                    x = event.getX();
                }
                else if (action == MotionEvent.ACTION_UP)
                {
                    Log.d("flipper touch", "UP");
                    float xDiff = event.getX() - x;

                    // If x movement high enough, flip the flipper
                    if (Math.abs(xDiff) > 60)
                    {
                        flipper.showNext();
                        return true;
                    }

                    // Don't proceed if we have action view visible;
                    if (flipper.getDisplayedChild() == 0)
                    {
                        Log.d("flipper touch", "UP THREAD INFO");
                        if (thread.isIs_self())
                        {
                            broadcastOpenComments(finalPosition);
                        }
                        else
                        {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(thread.getLink()));
                            context.startActivity(browserIntent);
                        }
                    }
                    else if (flipper.getDisplayedChild() == 1)
                    {
                        Log.d("flipper touch", "UP THREAD ACTIONS");
                        ImageButton commentsButton = (ImageButton)flipper.findViewById(R.id.thread_comments_button);
                        Rect rect = new Rect();
                        commentsButton.getHitRect(rect);
                        float hitX = event.getX();
                        float hitY = event.getY();
                        boolean contains = rect.contains((int)hitX, (int)hitY);
                        if (contains)
                        {
                            Log.d("flipper touch", "UP COMMENTS BUTTON");
                            broadcastOpenComments(finalPosition);
                        }
                    }
                }

                return true;
            }
        });

        return convertView;
    }

    private void broadcastOpenComments(int position)
    {
        Log.d("sender", "Broadcasting open comments message");
        Intent intent = new Intent("open-comments");
        intent.putExtra("threadPosition", position);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
