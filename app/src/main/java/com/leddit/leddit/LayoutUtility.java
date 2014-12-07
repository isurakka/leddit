package com.leddit.leddit;

import android.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leddit.leddit.api.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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
        ImageButton preview = (ImageButton)container.findViewById(R.id.preview);
        // TODO: Image preview

        title.setText(thread.getTitle());
        score.setText(Integer.toString(thread.getScore()));
        domain.setText(thread.getDomain());
        time.setText(Utility.redditTimePeriod(thread.getPostDate(), DateTime.now(DateTimeZone.UTC)));
        user.setText(thread.getUser());
        comments.setText(thread.getNum_comments() + " comments");

        String thumbnailUrl = thread.getThumbnail_url();
        if (thumbnailUrl != null) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(thread.getThumbnail_url(), preview);
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
}
