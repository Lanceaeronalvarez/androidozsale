package au.com.dealsdirect.ui.controller.ourpay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.concurrent.TimeUnit;

public class MyAccountsOurpayOverlayView extends View {
    private float mPreviousXTouch;
    private float mPreviousYTouch;
    private float mTouchXSpeed;
    private float mTouchYSpeed;
    private long mPreviousTime = 0;
    private double mDeltaTime = 0;
    private TimeUnit mTimeUnit = TimeUnit.MILLISECONDS;
    private int mTouchState;
    private TouchMoveListeneer mListener;

    public MyAccountsOurpayOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyAccountsOurpayOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyAccountsOurpayOverlayView(Context context) {
        super(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mTouchState = event.getAction();

        calculateDeltaTime(event);
        calculateTouchSpeed(event);
        boolean willCaptureTouch = talkToListener(event);

        super.onTouchEvent(event);
        return willCaptureTouch;
    }

    private void calculateDeltaTime(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreviousTime = -1;
                mDeltaTime = 0;
                break;
        }

        long time = System.nanoTime();
        if (mPreviousTime >= 0) {
            double conversion = (double) TimeUnit.NANOSECONDS.convert(1, mTimeUnit);
            mDeltaTime = (time - mPreviousTime) / conversion;
        } else {
            mDeltaTime = 0;
        }
        mPreviousTime = time;
    }

    private void calculateTouchSpeed(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchXSpeed = 0;
                mTouchYSpeed = 0;
                mPreviousXTouch = event.getRawX();
                mPreviousYTouch = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mTouchXSpeed = (event.getRawY() - mPreviousXTouch) / (float) mDeltaTime;
                mTouchYSpeed = (event.getRawY() - mPreviousYTouch) / (float) mDeltaTime;
                mPreviousXTouch = event.getRawX();
                mPreviousYTouch = event.getRawY();
                break;
        }
    }

    private boolean talkToListener(MotionEvent event) {
        if (mListener == null) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return mListener.onTouchDown(event);
            case MotionEvent.ACTION_MOVE:
                return mListener.onTouchMoved(event, mTouchXSpeed, mTouchYSpeed);
            case MotionEvent.ACTION_UP:
                return mListener.onTouchUp(event);
        }

        return false;
    }

    public float getTouchXSpeed() {
        return mTouchXSpeed;
    }

    public float getTouchYSpeed() {
        return mTouchYSpeed;
    }

    public void setTimeUnit(TimeUnit mTimeUnit) {
        this.mTimeUnit = mTimeUnit;
    }

    public TimeUnit getmTimeUnit() {
        return mTimeUnit;
    }

    public int getTouchState() {
        return mTouchState;
    }

    public boolean getIsTouching() {
        switch (mTouchState) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                return true;
        }
        return false;
    }

    public TouchMoveListeneer getListener() {
        return mListener;
    }

    public void setListener(TouchMoveListeneer mListener) {
        this.mListener = mListener;
    }

    interface TouchMoveListeneer {
        boolean onTouchDown(MotionEvent event);

        boolean onTouchMoved(MotionEvent event, float xSpeed, float ySpeed);

        boolean onTouchUp(MotionEvent event);
    }
}
