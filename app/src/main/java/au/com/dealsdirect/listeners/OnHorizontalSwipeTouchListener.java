package au.com.dealsdirect.listeners;

import android.view.MotionEvent;
import android.view.View;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class OnHorizontalSwipeTouchListener implements View.OnTouchListener {

    private boolean capturedTouch = false;

    private float previousX = 0;

    // Cumulative Moving Average
    private float cmaValue = 0;
    private int cmaIndex = 0;

    private float travelDistance = 0;

    private static final int SWIPE_VELOCITY_THRESHOLD = 13;
    private static final float SWIPE_DISTANCE_THRESHOLD = 32;

    public void onTouchDown() {

    }

    public void onTouchUp() {

    }

    public void onFinishDragging() {

    }

    public void onSwipeLeft() {
    }

    public void onSwipeRight() {
    }

    private void resetCma() {
        cmaIndex = -1;
        cmaValue = 0;
    }

    private void updateCma(float newValue) {
        if (cmaIndex >= 0) {
            cmaValue = (newValue + cmaIndex * cmaValue) / (cmaIndex + 1);
        }
        cmaIndex++;
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (event != null) {
            switch (event.getAction()) {
                case ACTION_DOWN:
                    previousX = event.getX();
                    resetCma();
                    travelDistance = 0;
                case ACTION_MOVE:
                    if (!capturedTouch) {
                        onTouchDown();
                        capturedTouch = true;
                    }
                    float displacement = event.getX() - previousX;
                    updateCma(displacement);
                    travelDistance += displacement;
                    previousX = event.getX();
                    break;
                case ACTION_CANCEL:
                case ACTION_UP:
                    if (travelDistance != 0 &&
                            Math.abs(travelDistance) > SWIPE_DISTANCE_THRESHOLD &&
                            travelDistance * cmaValue > 0 && //same direction
                            cmaIndex >= 0 &&
                            Math.abs(cmaValue) > SWIPE_VELOCITY_THRESHOLD) {
                        if (cmaValue > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    } else {
                        onFinishDragging();
                    }
                    resetCma();
                    onTouchUp();
                    capturedTouch = false;
                    break;
                default:
                    break;
            }
        }
        return false;
    }
}
