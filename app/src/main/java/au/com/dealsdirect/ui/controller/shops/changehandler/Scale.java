package au.com.dealsdirect.ui.controller.shops.changehandler;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Admin on 6/16/17.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Scale extends android.transition.Visibility{

    public Scale() {}

    public Scale(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        return getAnimator(view, 0, 1);
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        return getAnimator(view, 1, 0);
    }

    private Animator getAnimator(View view, float startValue, float endValue) {
        view.setScaleX(startValue);
        view.setScaleY(startValue);

        PropertyValuesHolder
                scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, startValue, endValue);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, startValue, endValue);

        return ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY);
    }
}
