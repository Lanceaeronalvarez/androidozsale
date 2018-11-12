package au.com.dealsdirect.ui.custom;

import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ArcTranslateAnimation extends Animation {

    private OPoint start;
    private OPoint end;
    private OPoint middle;
    private float mFromXValue = 0.0f;
    private float mToXValue = 0.0f;
    private float mYValue = 0.0f;
    private int mFromXType = ABSOLUTE;
    private int mToXType = ABSOLUTE;
    private int mYType = ABSOLUTE;
    private boolean hasControlPoint;

    /**
     * A translation along an arc defined by three points and a Bezier Curve
     *
     * @param duration - the time in ms it will take for the translation to complete
     * @param fromXType - One of Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, or Animation.RELATIVE_TO_PARENT.
     * @param fromXValue - Change in X coordinate to apply at the start of the animation
     * @param toXType - One of Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, or Animation.RELATIVE_TO_PARENT.
     * @param toXValue - Change in X coordinate to apply at the end of the animation
     * @param yType - One of Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, or Animation.RELATIVE_TO_PARENT.
     * @param yValue - Change in Y coordinate to apply at the middle of the animation (the radius of the arc)
     */
    public ArcTranslateAnimation(long duration, int fromXType, float fromXValue,
                                 int toXType, float toXValue, int yType, float yValue){
        setDuration(duration);
        hasControlPoint = false;

        mFromXValue = fromXValue;
        mToXValue = toXValue;
        mYValue = yValue;

        mFromXType = fromXType;
        mToXType = toXType;
        mYType = yType;

    }

    public ArcTranslateAnimation(long duration, float startX,
                                 float startY, float middleX, float middleY, float endX, float endY){
        setDuration(duration);
        hasControlPoint = true;

        start = new OPoint(startX, startY);
        end = new OPoint(endX, endY);
        middle = new OPoint(middleX, middleY);
    }

    /** Calculate the position on a quadratic bezier curve given three points
     *  and the percentage of time passed.
     * from http://en.wikipedia.org/wiki/B%C3%A9zier_curve
     * @param interpolatedTime - the fraction of the duration that has passed where 0<=time<=1
     * @param p0 - a single dimension of the starting point
     * @param p1 - a single dimension of the middle point
     * @param p2 - a single dimension of the ending point
     */
    private long calcBezier(float interpolatedTime, float p0, float p1, float p2){
        return Math.round((Math.pow((1 - interpolatedTime), 2) * p0)
                + (2 * (1 - interpolatedTime) * interpolatedTime * p1)
                + (Math.pow(interpolatedTime, 2) * p2));
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float dx = calcBezier(interpolatedTime, start.x, middle.x, end.x);
        float dy = calcBezier(interpolatedTime, start.y, middle.y, end.y);

        t.getMatrix().setScale(1 - interpolatedTime,1 - interpolatedTime);
        t.getMatrix().postRotate(interpolatedTime * 45, 100, 100);
        t.getMatrix().postTranslate(dx, dy);
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        if (!hasControlPoint) {
            float startX = resolveSize(mFromXType, mFromXValue, width, parentWidth);
            float endX = resolveSize(mToXType, mToXValue, width, parentWidth);
            float endY = resolveSize(mYType, mYValue, width, parentWidth);
            float middleX = startX + ((endX-startX)/2);
            start = new OPoint(startX, 0);
            end = new OPoint(endX, endY * 1.1f);
            middle = new OPoint(middleX, 0);
        } else {
            float startX = resolveSize(ABSOLUTE, start.x, width, parentWidth);
            float startY = resolveSize(ABSOLUTE, start.y, height, parentHeight);
            float midX = resolveSize(ABSOLUTE, middle.x, width, parentWidth);
            float midY = resolveSize(ABSOLUTE, middle.y, height, parentHeight);
            float endX = resolveSize(ABSOLUTE, end.x, width, parentWidth);
            float endY = resolveSize(ABSOLUTE, end.y, height, parentHeight);
            start = new OPoint(startX, startY);
            middle = new OPoint(midX, midY);
            end = new OPoint(endX, endY);
        }
    }

    public class OPoint
    {
        float x, y;

        public OPoint(float xValue, float yValue)
        {
            x = xValue;
            y = yValue;
        }
    }
}