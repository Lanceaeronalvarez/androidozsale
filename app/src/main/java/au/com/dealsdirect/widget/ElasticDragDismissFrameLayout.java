package au.com.dealsdirect.widget;

import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * dp Created by Admin on 6/15/17.
 */

public class ElasticDragDismissFrameLayout extends FrameLayout {

    public static abstract class ElasticDragDismissCallback {

        /**
         * Called for each drag event.
         *
         * @param elasticOffset       Indicating the drag offset with elasticity applied i.e. may
         *                            exceed 1.
         * @param elasticOffsetPixels The elastically scaled drag distance in pixels.
         * @param rawOffset           ScheduledPlan from [0, 1] indicating the raw drag offset i.e.
         *                            without elasticity applied. A value of 1 indicates that the
         *                            dismiss distance has been reached.
         * @param rawOffsetPixels     The raw distance the user has dragged
         */
        public void onDrag(float elasticOffset, float elasticOffsetPixels,
                float rawOffset, float rawOffsetPixels) { }

        /**
         * Called when dragging is released and has exceeded the threshold dismiss distance.
         */
        public void onDragDismissed() {

        }

        /**
         * Called when dragging is released but has not exceeded the threshold dismiss distance.
         */
        public void onCancel() {

        }
    }

    // configurable attribs
    private float dragDismissDistance = Float.MAX_VALUE;
    private float dragActivationAreaWidth = Float.MAX_VALUE;
    private float dragActivationAreaHeight = Float.MAX_VALUE;
    private float dragHorizontalThreshold = 1;
    private float dragVerticalThreshold = 1;
    private float dragDismissScale = 1;
    private float flingThreshold = 1;

    // state
    private float oldX = 0;
    private float oldY = 0;
    private float dX = 0;
    private float dY = 0;
    private float speedX = 0;
    private float speedY = 0;
    private boolean isDragging = false;
    private boolean isHorizontalDismissEnabled = true;
    private boolean isVerticalDismissEnabled = true;
    private boolean hasMultiTouchActivated = false;

    private List<ElasticDragDismissCallback> callbacks;

    public ElasticDragDismissFrameLayout(Context context) {
        this(context, null, 0);
    }

    public ElasticDragDismissFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ElasticDragDismissFrameLayout(Context context, AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        dragDismissDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, getResources().getDisplayMetrics());

        dragHorizontalThreshold = 12;
        dragVerticalThreshold = 8;

        flingThreshold = 32;

        dragDismissScale = 0.85f;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        calculateTouchVelocity(event);
        setupTouchPivotPoint(event);

        if (processMultiTouch(event)) {
            return super.dispatchTouchEvent(event);
        } else if (processFlingGesture(event)) {
            return true;
        } else if (shouldHorizontalDragBeActivated(event) ||
                shouldVerticalDragBeActivated(event) ||
                isDragging) {
            processDraggingGesture(event);
            processEndDragGesture(event);
            return true;
        } else {
            return super.dispatchTouchEvent(event);
        }
    }

    private void calculateTouchVelocity(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                speedX = 0;
                speedY = 0;
                oldX = event.getRawX();
                oldY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                speedX = event.getRawX() - oldX;
                speedY = event.getRawY() - oldY;
                oldX = event.getRawX();
                oldY = event.getRawY();
                break;
        }
    }

    private void setupTouchPivotPoint(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dX = this.getX() - event.getRawX();
                dY = this.getY() - event.getRawY();
                break;
        }
    }

    private boolean processMultiTouch(MotionEvent event) {
        if (event.getPointerCount() > 1 || hasMultiTouchActivated) {
            if (isDragging) {
                isDragging = false;
                cancelDragDismiss();
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_MOVE:
                    hasMultiTouchActivated = true;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    if (event.getPointerCount() <= 1) {
                        hasMultiTouchActivated = false;
                    }
                    break;
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean shouldHorizontalDragBeActivated(MotionEvent event) {
        return isHorizontalDismissEnabled &&
                event.getRawX() < dragActivationAreaWidth &&
                speedX > dragHorizontalThreshold &&
                (Math.abs(speedY) < dragVerticalThreshold || isDragging);
    }

    private boolean shouldVerticalDragBeActivated(MotionEvent event) {
        return isVerticalDismissEnabled &&
                event.getRawY() < dragActivationAreaHeight &&
                speedY > dragVerticalThreshold &&
                (Math.abs(speedX) < dragHorizontalThreshold || isDragging);
    }

    private float getDistanceFromInitialTouchPoint(MotionEvent event) {
        return (float) Math.hypot(event.getRawX() + dX, Math.max(0, event.getRawY() + dY));
    }

    private float getScaleWithDistance(float distance) {
        float t = Math.min(distance / dragDismissDistance, 1f);
        return  1f * (1 - t) + dragDismissScale * t;
    }

    private boolean processFlingGesture(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP &&
                isVerticalDismissEnabled &&
                speedY > flingThreshold) {
            dispatchDismissCallback();
            return true;
        }
        return false;
    }

    private void processDraggingGesture(MotionEvent event) {
        float distance = getDistanceFromInitialTouchPoint(event);
        float scale = getScaleWithDistance(distance);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                isDragging = true;
                float centerX = (-dX / this.getWidth());
                float centerY = (-dY / this.getHeight());
                this.animate()
                        .x(event.getRawX() + dX + this.getWidth() / 2 * (-1 + scale + centerX * 2 * (1 - scale)))
                        .y(event.getRawY() + dY + this.getHeight() / 2 * (-1 + scale + centerY * 2 * (1 - scale)))
                        .scaleX(scale)
                        .scaleY(scale)
                        .setDuration(0)
                        .start();
                dispatchDragCallback(scale, distance,
                        scale, distance);
                break;
        }
    }

    private void processEndDragGesture(MotionEvent event) {
        float distance = getDistanceFromInitialTouchPoint(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                break;
            default:
                if (distance > dragDismissDistance) {
                    dispatchDismissCallback();
                } else {
                    cancelDragDismiss();
                }
                isDragging = false;
                break;
        }
    }

    private void cancelDragDismiss() {
        animate()
                .translationX(0f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200L)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setListener(null)
                .start();
        dispatchDragCallback(0f, 0f, 0f, 0f);
        dispatchCancelCallback();
    }

    public void setIsHorizontalDismissEnabled(boolean enabled) {
        isHorizontalDismissEnabled = enabled;
    }

    public boolean getIsHorizontalDismissEnabled() {
        return isHorizontalDismissEnabled;
    }

    public void setIsVerticalDismissEnabled(boolean enabled) {
        isVerticalDismissEnabled = enabled;
    }

    public boolean getIsVerticalDismissEnabled() {
        return isVerticalDismissEnabled;
    }

    public void setDragDismissScale(float scale) {
        dragDismissScale = scale;
    }

    public float getDragDismissScale() {
        return dragDismissScale;
    }

    public void setDragActivationAreaWidth(float width) {
        dragActivationAreaWidth = width;
    }

    public float getDragActivationAreaWidth() {
        return dragActivationAreaWidth;
    }

    public void setDragActivationAreaHeight(float height) {
        dragActivationAreaHeight = height;
    }

    public float getDragActivationAreaHeight() {
        return dragActivationAreaHeight;
    }

    public void setDragHorizontalThreshold(float threshold) {
        dragHorizontalThreshold = threshold;
    }

    public float getDragHorizontalThreshold() {
        return dragHorizontalThreshold;
    }

    public void setDragVerticalThreshold(float threshold) {
        dragVerticalThreshold = threshold;
    }

    public float getDragVerticalThreshold() {
        return dragVerticalThreshold;
    }

    public void addListener(ElasticDragDismissCallback listener) {
        if (callbacks == null) {
            callbacks = new ArrayList<>();
        }
        callbacks.add(listener);
    }

    public void removeListener(ElasticDragDismissCallback listener) {
        if (callbacks != null && callbacks.size() > 0) {
            callbacks.remove(listener);
        }
    }

    private void dispatchDragCallback(float elasticOffset, float elasticOffsetPixels,
            float rawOffset, float rawOffsetPixels) {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (ElasticDragDismissCallback callback : callbacks) {
                callback.onDrag(elasticOffset, elasticOffsetPixels,
                                rawOffset, rawOffsetPixels);
            }
        }
    }

    private void dispatchDismissCallback() {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (ElasticDragDismissCallback callback : callbacks) {
                callback.onDragDismissed();
            }
        }
    }

    private void dispatchCancelCallback() {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (ElasticDragDismissCallback callback : callbacks) {
                callback.onCancel();
            }
        }
    }
}