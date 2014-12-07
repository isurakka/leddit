package com.leddit.leddit;

import android.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leddit.leddit.api.RedditItemSorting;
import com.leddit.leddit.api.RedditSortingTimescale;
import com.leddit.leddit.api.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Created by Iiro on 7.12.2014.
 */
public class LayoutUtility {
    public static void SetThreadInfo(RedditThread thread, View container)
    {
        TextView title = (TextView)container.findViewById(R.id.title);
        TextView score = (TextView)container.findViewById(R.id.score);
        TextView domain = (TextView)container.findViewById(R.id.domain);
        TextView time = (TextView)container.findViewById(R.id.time);
        TextView user = (TextView)container.findViewById(R.id.user);
        TextView comments = (TextView)container.findViewById(R.id.comments);
        ImageView preview = (ImageView)container.findViewById(R.id.preview);
        // TODO: Image preview

        title.setText(thread.getTitle());
        score.setText(Integer.toString(thread.getScore()));
        domain.setText(thread.getDomain());
        time.setText(Utility.redditTimePeriod(thread.getPostDate(), DateTime.now(DateTimeZone.UTC)));
        user.setText(thread.getUser());
        comments.setText(thread.getNum_comments() + " comments");

        String thumbnailUrl = thread.getThumbnail_url();
        if (thumbnailUrl != null) {
            Log.d("thumbnail url", thumbnailUrl);
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheOnDisk(true)
                    .build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(thread.getThumbnail_url(), preview, options);
            preview.setVisibility(View.VISIBLE);
        } else {
            preview.setVisibility(View.GONE);
        }

        /*
        int w = preview.getMeasuredWidth();
        int h = preview.getMeasuredHeight();
        Log.d("w", new Integer(preview.getWidth()).toString());
        Log.d("h", new Integer(preview.getHeight()).toString());
        int min = Math.min(w, h);
        preview.setLayoutParams(new LinearLayout.LayoutParams(min, min));
        */
    }

    public static RedditSortData sortingOptionToSortData(String option)
    {
        if (option.equals("hot") || option.equals("new"))
        {
            return new RedditSortData(option, null);
        }
        else
        {
            String[] split = option.split(" ");

            String sorting;
            if (split[0].equals("conv"))
            {
                sorting = RedditItemSorting.CONTROVERSIAL;
            }
            else
            {
                sorting = split[0];
            }

            String timescale = RedditSortingTimescale.MONTH;
            if (split[1].equals("1h"))
            {
                timescale = RedditSortingTimescale.HOUR;
            }
            else if (split[1].equals("1d"))
            {
                timescale = RedditSortingTimescale.DAY;
            }
            else if (split[1].equals("1w"))
            {
                timescale = RedditSortingTimescale.WEEK;
            }
            else if (split[1].equals("1m"))
            {
                timescale = RedditSortingTimescale.MONTH;
            }
            else if (split[1].equals("1y"))
            {
                timescale = RedditSortingTimescale.YEAR;
            }
            else if (split[1].equals("all"))
            {
                timescale = RedditSortingTimescale.ALL;
            }

            return new RedditSortData(sorting, timescale);
        }
    }
}
