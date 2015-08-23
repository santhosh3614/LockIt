package com.smartminds.lockit.others;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by santhosh on 8/7/15.
 */
public class NonSwipeableViewPager extends ViewPager {
    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
