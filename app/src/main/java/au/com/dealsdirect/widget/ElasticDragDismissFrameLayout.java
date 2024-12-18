package au.com.dealsdirect.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

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
                           float rawOffset, float rawOffsetPixels) {
        }

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
    private float startX = 0;
    private float startY = 0;
    private float prevX = 0;
    private float prevY = 0;
    private float touchPrevX = 0;
    private float touchPrevY = 0;
    private float touchPivotX = 0;
    private float touchPivotY = 0;
    private float touchSpeedX = 0; //rawX per millisecond
    private float touchSpeedY = 0; //rawY per millisecond
    // lower is smoother/slower
    // at 1.0, it snaps instantly
    // at 0.0, it doesn't move
    // any value outside [0.0 ..< 1.0] will cause weird things to happen
    private float dragSmoothness = 0.4f;
    private boolean isDragging = false;
    private boolean isHorizontalDismissEnabled = true;
    private boolean isVerticalDismissEnabled = true;
    private boolean hasMultiTouchActivated = false;

    private double verticalDirectionThreshold = 8; //8 degrees
    private boolean isGestureDirectionChanged = false;
    private double gestureDirectionThreshold = 20; //20 degrees
    private double establishedGestureDirection = 0;
    private double timeElapsedForDirectionUpdate = 0; //time elapsed resets to zero after every speed update
    private double timeElapsedToDirectionUpdate = 40; //70 milliseconds
    private long previousTime = -1;
    private double deltaTime = 0;
    private double timeElapsedForSpeedUpdate = 0; //time elapsed resets to zero after every speed update
    private double timeElapsedToSpeedUpdate = 7; //20 milliseconds

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

        flingThreshold = 50;

        dragDismissScale = 0.14f;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        calculateDeltaTime(event);
        calculateTouchVelocity(event);
        calculateGeneralGestureDirection(event);
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

    private void calculateDeltaTime(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                previousTime = -1;
                deltaTime = 0;
                break;
        }

        long time = System.nanoTime();
        if (previousTime >= 0) {
            deltaTime = (time - previousTime) / 10000000.0;
        } else {
            deltaTime = 0;
        }
        previousTime = time;
    }

    private void calculateTouchVelocity(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchSpeedX = 0;
                touchSpeedY = 0;
                touchPrevX = event.getRawX();
                touchPrevY = event.getRawY();
                timeElapsedForSpeedUpdate = 0;
                isGestureDirectionChanged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (timeElapsedForSpeedUpdate > timeElapsedToSpeedUpdate) {
                    touchSpeedX = (event.getRawX() - touchPrevX) / (float) timeElapsedToSpeedUpdate;
                    touchSpeedY = (event.getRawY() - touchPrevY) / (float) timeElapsedToSpeedUpdate;
                    touchPrevX = event.getRawX();
                    touchPrevY = event.getRawY();
                    timeElapsedForSpeedUpdate -= timeElapsedToSpeedUpdate;
                }
                break;
        }
        timeElapsedForSpeedUpdate += deltaTime;
    }

    private void calculateGeneralGestureDirection(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                timeElapsedForDirectionUpdate = 0;
                establishedGestureDirection = -1;
                isGestureDirectionChanged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (timeElapsedForDirectionUpdate > timeElapsedToDirectionUpdate) {
                    timeElapsedForDirectionUpdate -= timeElapsedToDirectionUpdate;

                    double direction = getDirection();

                    if (establishedGestureDirection < 0) {
                        establishedGestureDirection = (direction < 0 ? direction + 360 : direction) % 360.0;
                    } else if (!isGestureDirectionChanged) {
                        isGestureDirectionChanged = Math.abs(getDirectionDifference(establishedGestureDirection, direction)) > gestureDirectionThreshold;
                    }
                }
                break;
        }
        timeElapsedForDirectionUpdate += deltaTime;
    }

    private float getXTransformed() {
        float x = this.getX();
        if (this.getParent() instanceof ViewPager) {
            ViewPager viewPager = (ViewPager) this.getParent();
            // assuming this view is the currentItem
            x -= viewPager.getCurrentItem() * viewPager.getMeasuredWidth();
        }
        return x;
    }

    private float transformX(float transformedX) {
        float x = transformedX;
        if (this.getParent() instanceof ViewPager) {
            ViewPager viewPager = (ViewPager) this.getParent();
            // assuming this view is the currentItem
            x += viewPager.getCurrentItem() * viewPager.getMeasuredWidth();
        }
        return x;
    }

    private void setupTouchPivotPoint(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchPivotX = event.getRawX();
                touchPivotY = event.getRawY();
                startX = getX();
                startY = getY();
                setPivotX(event.getX());
                setPivotY(event.getY());
                prevX = getX();
                prevY = getY();
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
                touchSpeedX > dragHorizontalThreshold &&
                (Math.abs(touchSpeedY) < dragVerticalThreshold || isDragging);
    }

    private boolean shouldVerticalDragBeActivated(MotionEvent event) {
        return isVerticalDismissEnabled &&
                event.getRawY() < dragActivationAreaHeight &&
                touchSpeedY > dragVerticalThreshold &&
                (Math.abs(touchSpeedX) < dragHorizontalThreshold || isDragging)
                && isDirectionAlmostVerticalDown(verticalDirectionThreshold) &&
                !isGestureDirectionChanged;
    }

    private boolean isDirectionAlmostVerticalDown(double threshold) {
        return Math.abs(getDirectionDifference(getDirection(), 90.0)) < threshold;
    }

    private double getDirectionDifference(double dir1, double dir2) {
        double diff = (dir1 - dir2) % 360.0;
        if (diff > 180) {
            diff -= 360;
        }
        if (diff < -180) {
            diff += 360;
        }
        return diff;
    }

    private double getDirection() {
        return Math.toDegrees(Math.atan2(touchSpeedY, touchSpeedX));
    }

    private float getDistanceFromInitialTouchPoint(MotionEvent event) {
        final float x = event.getRawX();
        final float y = event.getRawY();
        return (float) Math.hypot(x - touchPivotX, y - touchPivotY);
    }

    private float getScaleWithDistance(float distance) {
        float t = Math.min(distance / dragDismissDistance, 1f);
        return lerp(1f, dragDismissScale, t);
    }

    private boolean processFlingGesture(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP &&
                isVerticalDismissEnabled &&
                touchSpeedY > flingThreshold) {
            dispatchDismissCallback();
            return true;
        }
        return false;
    }

    private void processDraggingGesture(MotionEvent event) {
        final float distance = getDistanceFromInitialTouchPoint(event);
        final float scale = getScaleWithDistance(distance);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                isDragging = true;
                final float newPosX = startX + (event.getRawX() - touchPivotX);
                final float newPosY = startY + (event.getRawY() - touchPivotY);
                setX(lerp(prevX, newPosX, dragSmoothness));
                setY(lerp(prevY, newPosY, dragSmoothness));
                prevX = getX();
                prevY = getY();
                setScaleX(scale);
                setScaleY(scale);
                dispatchDragCallback(scale, distance,
                        scale, distance);
                break;
        }
    }

    private float lerp(float a, float b, float t) {
        return a * (1 - t) + b * t;
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
        if (callbacks != null && !callbacks.isEmpty()) {
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