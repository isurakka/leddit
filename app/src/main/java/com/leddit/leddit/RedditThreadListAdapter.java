package com.leddit.leddit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;

import java.util.Date;
import java.util.List;

/**
 * Created by Iiro on 30.11.2014.
 */
public class RedditThreadListAdapter extends BaseAdapter {

    LayoutInflater inflater;
    List<RedditThread> threads;

    public RedditThreadListAdapter(LayoutInflater inflater, List<RedditThread> threads)
    {
        this.inflater = inflater;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_thread, null);

        TextView title = (TextView)convertView.findViewById(R.id.title);
        TextView score = (TextView)convertView.findViewById(R.id.score);
        TextView domain = (TextView)convertView.findViewById(R.id.domain);
        TextView time = (TextView)convertView.findViewById(R.id.time);
        TextView user = (TextView)convertView.findViewById(R.id.user);
        TextView comments = (TextView)convertView.findViewById(R.id.comments);
        // TODO: Image preview

        RedditThread thread = threads.get(position);

        title.setText(thread.getTitle());
        score.setText(Integer.toString(thread.getScore()));
        domain.setText(thread.getDomain());
        time.setText(Hours.hoursBetween(DateTime.now(DateTimeZone.UTC), thread.getPostDate()).getHours() + "h");
        user.setText(thread.getUser());
        comments.setText(thread.getCommentCount() + " comments");

        return convertView;
    }
}
