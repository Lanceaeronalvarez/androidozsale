package au.com.dealsdirect.ui.custom.transitions;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler;

/**
 * dp Created by Admin on 8/2/17.
 */

public class SimpleChangeHandler extends AnimatorChangeHandler {
    public SimpleChangeHandler() { }

    public SimpleChangeHandler(boolean removesFromViewOnPush) {
        super(removesFromViewOnPush);
    }

    public SimpleChangeHandler(long duration) {
        super(duration);
    }

    public SimpleChangeHandler(long duration, boolean removesFromViewOnPush) {
        super(duration, removesFromViewOnPush);
    }

    @Override @NonNull
    protected Animator getAnimator(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, boolean isPush, boolean toAddedToContainer) {
        AnimatorSet animator = new AnimatorSet();
        if (to != null) {
            float start = toAddedToContainer ? 1 : to.getAlpha();
            animator.play(ObjectAnimator.ofFloat(to, View.ALPHA, start, 1));
        }

        if (from != null && removesFromViewOnPush()) {
            animator.play(ObjectAnimator.ofFloat(from, View.ALPHA, 1));
        }

        return animator;
    }

    @Override
    protected void resetFromView(@NonNull View from) {
        from.setAlpha(1);
    }

    @Override @NonNull
    public ControllerChangeHandler copy() {
        return new SimpleChangeHandler(getAnimationDuration(), removesFromViewOnPush());
    }

}
