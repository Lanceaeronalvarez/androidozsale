package au.com.dealsdirect.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

public class BackChangeHandler extends AnimatorChangeHandler {

    public BackChangeHandler() {
    }

    public BackChangeHandler(boolean removesFromViewOnPush) {
        super(removesFromViewOnPush);
    }

    public BackChangeHandler(long duration) {
        super(duration);
    }

    public BackChangeHandler(long duration, boolean removesFromViewOnPush) {
        super(duration, removesFromViewOnPush);
    }

    @Override
    @NonNull
    protected Animator getAnimator(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, boolean isPush, boolean toAddedToContainer) {
        AnimatorSet animatorSet = new AnimatorSet();

        if (from != null) {
            animatorSet.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_X, from.getWidth()));
        }
        if (to != null) {
            // Allow this to have a nice transition when coming off an aborted push animation
            float fromLeft = from != null ? from.getTranslationX() : 0;
            animatorSet.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_X, fromLeft - to.getWidth(), 0));
        }

        return animatorSet;
    }

    @Override
    protected void resetFromView(@NonNull View from) {
        from.setTranslationX(0);
    }

    @Override
    @NonNull
    public ControllerChangeHandler copy() {
        return new HorizontalChangeHandler(getAnimationDuration(), removesFromViewOnPush());
    }

}