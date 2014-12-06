package com.leddit.leddit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.leddit.leddit.api.Utility;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;

import java.util.List;

/**
 * Created by Iiro on 30.11.2014.
 */
public class RedditCommentListAdapter extends BaseAdapter {

    Context context;
    RedditThread thread;
    static int id = 0;

    public RedditCommentListAdapter(Context context, RedditThread thread)
    {
        this.context = context;
        this.thread = thread;
    }

    @Override
    public int getCount() {
        return thread.getCommentCount();
    }

    @Override
    public Object getItem(int position) {
        return thread.getComments().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_comment, parent, false);
        }

        final View commentContainer = convertView.findViewById(R.id.commentContainer);

        TextView user = (TextView)commentContainer.findViewById(R.id.user);
        TextView score = (TextView)commentContainer.findViewById(R.id.score);
        TextView time = (TextView)commentContainer.findViewById(R.id.time);
        TextView text = (TextView)commentContainer.findViewById(R.id.text);
        Space indent = (Space)convertView.findViewById(R.id.indent);

        RedditComment comment = thread.getComments().get(position);

        user.setText(comment.getUser());
        score.setText(Integer.toString(comment.getScore()));
        time.setText(Utility.redditTimePeriod(thread.getPostDate(), DateTime.now(DateTimeZone.UTC)));
        text.setText(comment.getText());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)context.getResources().getDimension(R.dimen.comment_indent) * comment.getDepth(), ViewGroup.LayoutParams.MATCH_PARENT);
        indent.setLayoutParams(params);

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

                    /*
                    // Don't proceed if we have action view visible;
                    if (flipper.getDisplayedChild() == 0)
                    {
                        Log.d("flipper touch", "UP THREAD INFO");
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(thread.getLink()));
                        context.startActivity(browserIntent);
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
                            //Log.d("sender", "Broadcasting message");
                            //Intent intent = new Intent("open-comments");
                            //intent.putExtra("threadPosition", finalPosition);
                            //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }
                    }
                    */
                }

                return true;
            }
        });

        return convertView;
    }
}
