package au.com.dealsdirect.ui.controller.shops.changehandler;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

/**
 * dp Created by Admin on 6/5/17.
 */

public class HorizontalNavTransitionChangeHandler extends AnimatorChangeHandler{

    public HorizontalNavTransitionChangeHandler() { }

    public HorizontalNavTransitionChangeHandler(boolean removesFromViewOnPush) {
        super(removesFromViewOnPush);
    }

    public HorizontalNavTransitionChangeHandler(long duration) {
        super(duration);
    }

    public HorizontalNavTransitionChangeHandler(long duration, boolean removesFromViewOnPush) {
        super(duration, removesFromViewOnPush);
    }

    @Override @NonNull
    protected Animator getAnimator(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, boolean isPush, boolean toAddedToContainer) {
        AnimatorSet animatorSet = new AnimatorSet();

        if (isPush) {
            if (from != null) {
                animatorSet.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_X, 0));

            }
            if (to != null) {
                animatorSet.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_X, -to
                        .getWidth(),0));


            }
        } else {
            if (from != null) {

                animatorSet.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_X, -from.getWidth()));

            }
            if (to != null) {
                // Allow this to have a nice transition when coming off an aborted push animation
                float fromRight = from != null ? from.getTranslationX() : to.getWidth();
                animatorSet.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_X, fromRight, 0));
            }
        }

        return animatorSet;
    }

    @Override
    protected void resetFromView(@NonNull View from) {
        from.setTranslationX(0);
    }

    @Override @NonNull
    public ControllerChangeHandler copy() {
        return new HorizontalChangeHandler(getAnimationDuration(), removesFromViewOnPush());
    }

}
