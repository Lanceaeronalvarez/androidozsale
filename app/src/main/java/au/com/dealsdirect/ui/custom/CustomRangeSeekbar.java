package au.com.dealsdirect.ui.custom;

/**
 * Created by smartwave on 24/07/2017.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;


public class CustomRangeSeekbar extends CrystalRangeSeekbar {

    private LinearLayout minPriceMovingLayout;
    private LinearLayout maxPriceMovingLayout;

    public CustomRangeSeekbar(Context context) {
        super(context);
    }

    public CustomRangeSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRangeSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMinPriceMovingLayout(LinearLayout layout) {
        minPriceMovingLayout = layout;
    }

    public void setMaxPriceMovingLayout(LinearLayout layout) {
        maxPriceMovingLayout = layout;
    }

    @Override
    protected void touchMove(float x, float y) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();

        switch (getPressedThumb()) {
            case MIN:
                adjustBubble(x, 0, minPriceMovingLayout, maxPriceMovingLayout);
                break;
            case MAX:
                adjustBubble(x, getWidth() - lp.rightMargin, maxPriceMovingLayout,
                        minPriceMovingLayout);
                break;
        }
    }

    public void adjustBubble(float x, float defaultPoint, LinearLayout bubbleLayout,
                             LinearLayout otherBubbleLayout) {

        boolean isBubbleWithinRange = x >= getThumbWidth() / 2 && x <= getWidth();
        boolean isBubbleValueEqual = getSelectedMinValue() == getSelectedMaxValue();
        boolean isXBeyondOtherBubble = bubbleLayout.equals(maxPriceMovingLayout) ?
                x <= minPriceMovingLayout.getX() + getThumbWidth() / 2
                : x >= maxPriceMovingLayout.getX() + getThumbWidth() / 2;

        bubbleLayout.setVisibility(VISIBLE);
        otherBubbleLayout.setVisibility(VISIBLE);
        if ((isBubbleValueEqual || isXBeyondOtherBubble)
                && otherBubbleLayout.getVisibility() == VISIBLE) {
            bubbleLayout.setVisibility(GONE);
        } else if (isBubbleWithinRange) {
            bubbleLayout.setX(x - getThumbWidth() / 2);
        } else {
            bubbleLayout.setX(defaultPoint);
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (minPriceMovingLayout != null) {
            minPriceMovingLayout.setX(getLeftThumbRect().left);
        }

        if (maxPriceMovingLayout != null) {
            maxPriceMovingLayout.setX(getRightThumbRect().left);
        }
    }

    public void resetMovingLayoutVisibility(){
        if(maxPriceMovingLayout!=null && minPriceMovingLayout!=null) {
            maxPriceMovingLayout.setVisibility(VISIBLE);
            minPriceMovingLayout.setVisibility(VISIBLE);
        }
    }

}

