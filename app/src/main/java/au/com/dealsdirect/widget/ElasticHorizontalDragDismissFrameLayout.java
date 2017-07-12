package au.com.dealsdirect.widget;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul on 7/12/17.
 */

public class ElasticHorizontalDragDismissFrameLayout extends FrameLayout implements NestedScrollingParent {

    public static abstract class ElasticHorizontalDragDismissCallback {

        /**
         * Called for each drag event.
         *
         * @param elasticOffset       Indicating the drag offset with elasticity applied i.e. may
         *                            exceed 1.
         * @param elasticOffsetPixels The elastically scaled drag distance in pixels.
         * @param rawOffset           Value from [0, 1] indicating the raw drag offset i.e.
         *                            without elasticity applied. A value of 1 indicates that the
         *                            dismiss distance has been reached.
         * @param rawOffsetPixels     The raw distance the user has dragged
         */
        public void onDrag(float elasticOffset, float elasticOffsetPixels,
                           float rawOffset, float rawOffsetPixels) {
        }

        /**
         * Called when dragging is released and has exceeded the threshold dismiss distance.
         */
        public void onDragDismissed() {

        }

    }

    // configurable attribs
    private float dragDismissDistance = Float.MAX_VALUE;
    private float dragDismissFraction = -2f;
    private float dragDismissScale = 2f;
    private boolean shouldScale = false;
    private float dragElacticity = 2f;
    private float xDistance;
    private float yDistance;
    private float lastX;
    private float lastY;
    private float downX;
    private float downY;

    // state
    private float totalDrag;
    private boolean draggingLeft = false;
    private boolean draggingRight = false;

    private List<ElasticHorizontalDragDismissCallback> callbacks;

    public ElasticHorizontalDragDismissFrameLayout(Context context) {
        this(context, null, 0);
    }

    public ElasticHorizontalDragDismissFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ElasticHorizontalDragDismissFrameLayout(Context context, AttributeSet attrs,
                                                   int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        dragDismissDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        dragDismissFraction = 0.7f;
        dragDismissScale = 0.8f;
        shouldScale = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("action",ev.getAction()+"");
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                xDistance = yDistance = 0f;
                lastX = ev.getX();
                lastY = ev.getY();
                downX = ev.getX();
                downY = ev.getY();

                Log.d("logx", "action down " + lastX + " lastX " + lastY + " lastY");
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += curX - lastX;
                yDistance += Math.abs(curY - lastY);
                lastX = curX;
                lastY = curY;
                if(Math.abs(downY - curY) > Math.abs(downX - curX)) {
                    return false;
                }
                Log.d("logx", Math.abs(downY - curY) + " > " + Math.abs(downX - curX));
                dragScale((int)xDistance * -1);
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.d("logx", child + " child" + target + " target" + nestedScrollAxes + " axes");
        return true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // if we're in a drag gesture and the user reverses up the we should take those events
        if (draggingLeft && dx > 0 || draggingRight && dx < 0) {
            Log.d("logx", dx + "dxConsumed nestedprescroll");
            Log.d("logx", dy + "dxConsumed nestedprescroll");
            consumed[1] = dx;
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        Log.d("logx", dxConsumed + "dxConsumed nestedscroll");
        Log.d("logx", dyConsumed + "dyConsumed nestedscroll");
        onStopNestedScroll(target);
    }

    @Override
    public void onStopNestedScroll(View child) {
        if (Math.abs(totalDrag) >= dragDismissDistance) {
            dispatchDismissCallback();
        } else { // settle back to natural position
            animate()
                    .translationX(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200L)
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setListener(null)
                    .start();
            totalDrag = 0;
            draggingLeft = draggingRight = false;
            dispatchDragCallback(0f, 0f, 0f, 0f);
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        Log.d("logx", child + " child" + target + " target" + axes + " axes");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (dragDismissFraction > 0f) {
            dragDismissDistance = h * dragDismissFraction;
        }
    }

    public void addListener(ElasticHorizontalDragDismissCallback listener) {
        if (callbacks == null) {
            callbacks = new ArrayList<>();
        }
        callbacks.add(listener);
    }

    public void removeListener(ElasticHorizontalDragDismissCallback listener) {
        if (callbacks != null && callbacks.size() > 0) {
            callbacks.remove(listener);
        }
    }

    private void dragScale(int scroll) {
        Log.d("logx", scroll + " dragscale");
        if (scroll == 0) return;

        totalDrag += scroll;

        // track the direction & set the pivot point for scaling
        // don't double track i.e. if start dragging down and then reverse, keep tracking as
        // dragging down until they reach the 'natural' position
        if (scroll < 0 && !draggingRight && !draggingLeft) {
            draggingLeft = true;
            if (shouldScale) setPivotX(getWidth());
        } else if (scroll > 0 && !draggingLeft && !draggingRight) {
            draggingRight = true;
            if (shouldScale) setPivotX(0f);
        }
        setPivotY(getWidth() / 2);


        // how far have we dragged relative to the distance to perform a dismiss
        // (0–1 where 1 = dismiss distance). Decreasing logarithmically as we approach the limit
        float dragFraction = (float) Math.log10(1 + (Math.abs(totalDrag) / dragDismissDistance));

        // calculate the desired translation given the drag fraction
        float dragTo = dragFraction * dragDismissDistance * dragElacticity;

        if (draggingRight) {
            // as we use the absolute magnitude when calculating the drag fraction, need to
            // re-apply the drag direction
            dragTo *= 0;
            totalDrag = 0;
        }
        setTranslationX(dragTo);

        if (shouldScale) {
            final float scale = 1 - ((1 - dragDismissScale) * dragFraction);
            setScaleX(scale);
            setScaleY(scale);
        }

        // if we've reversed direction and gone past the settle point then clear the flags to
        // allow the list to get the scroll events & reset any transforms
        if ((draggingLeft && totalDrag >= 0)
                || (draggingRight && totalDrag <= 0)) {
            totalDrag = dragTo = dragFraction = 0;
            draggingLeft = draggingRight = false;
            setTranslationX(0f);
            setScaleX(1f);
            setScaleY(1f);
        }
        dispatchDragCallback(dragFraction, dragTo,
                Math.min(1f, Math.abs(totalDrag) / dragDismissDistance), totalDrag);
    }

    private void dispatchDragCallback(float elasticOffset, float elasticOffsetPixels,
                                      float rawOffset, float rawOffsetPixels) {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (ElasticHorizontalDragDismissCallback callback : callbacks) {
                callback.onDrag(elasticOffset, elasticOffsetPixels,
                        rawOffset, rawOffsetPixels);
                Log.d("logx", elasticOffset + " elasticOffset " + elasticOffsetPixels + " elasticOffsetPixels " + rawOffset
                        + " rawOffsetPixels ");
            }
        }
    }

    private void dispatchDismissCallback() {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (ElasticHorizontalDragDismissCallback callback : callbacks) {
                callback.onDragDismissed();
            }
        }
    }

}
