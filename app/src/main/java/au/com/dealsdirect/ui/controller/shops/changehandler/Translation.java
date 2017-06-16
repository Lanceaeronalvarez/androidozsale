package au.com.dealsdirect.ui.controller.shops.changehandler;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * dp Created by Admin on 6/16/17.
 */

public class Translation extends Visibility {

    private int translationX;
    private int translationY;

    public Translation() {}

    public Translation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Translation(int x, int y){
        translationX = x;
        translationY = y;
    }

    @Override public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues,
            TransitionValues endValues) {

        return getAnimator(view, translationX, translationY);
    }


    private Animator getAnimator(View view, int xValue, int yValue) {
        view.setX(xValue);
        view.setY(yValue);

        view.animate().x(xValue);
        view.animate().y(yValue);

        PropertyValuesHolder
                positionX = PropertyValuesHolder.ofFloat(View.X, xValue, 0);
        PropertyValuesHolder positionY = PropertyValuesHolder.ofFloat(View.Y, yValue, 0);

        return ObjectAnimator.ofPropertyValuesHolder(view, positionX, positionY);
    }
}
