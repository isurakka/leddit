package com.leddit.leddit;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Iiro on 1.12.2014.
 */
public class PagerThreadAdapter extends PagerAdapter {

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater)container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int id = 0;

        Log.d("Pager adapter", "Here I am pos " + position);

        if (position == 0)
        {
            id = R.layout.list_thread;
        }
        else if (position == 1)
        {
            id = R.layout.list_thread_actions;
        }

        View view = inflater.inflate(id, null);
        ((ViewPager)container).addView(view, position);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }
}
