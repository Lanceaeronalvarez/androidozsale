package au.com.dealsdirect.ui.controller.main;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * dp Created by Admin on 7/17/17.
 */

public class MainCustomViewPager extends ViewPager {

    private boolean mIsSwipingAllowed = true;
    public MainCustomViewPager(Context context) {
        super(context);
        setMyScroller();
    }

    public MainCustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMyScroller();
    }

    @Override
    public int getCurrentItem() {
        return super.getCurrentItem();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        if (mIsSwipingAllowed) {
            try {
                return super.onInterceptTouchEvent(event);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        if (mIsSwipingAllowed) {
            try {
                return super.onTouchEvent(event);

            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }

    //down one is added for smooth scrolling

    public void setMyScroller() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyScroller extends Scroller {
        public MyScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 350 /*1 secs*/);
        }
    }

    public void setIsSwipeable(boolean isSwipeable){
        mIsSwipingAllowed = isSwipeable;
    }

    public boolean isSwipeable() {
        return mIsSwipingAllowed;
    }
}
