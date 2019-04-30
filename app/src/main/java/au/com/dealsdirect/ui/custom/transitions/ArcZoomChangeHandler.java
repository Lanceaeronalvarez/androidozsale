package au.com.dealsdirect.ui.custom.transitions;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.utils.BundleKeys;

public class ArcZoomChangeHandler extends AnimatorChangeHandler {

    private float sourceLeft = 0;
    private float sourceTop = 0;
    private float sourceWidth = 0;
    private float sourceHeight = 0;

    public ArcZoomChangeHandler() { }

    public ArcZoomChangeHandler(float left, float top, float width, float height) {
        super(false);
        sourceLeft = left;
        sourceTop = top;
        sourceWidth = width;
        sourceHeight = height;
    }

    @Override
    public void saveToBundle(@NonNull Bundle bundle) {
        super.saveToBundle(bundle);

        bundle.putFloat(BundleKeys.KEY_ARCZOOMCHANGEHANDLER_LEFT, sourceLeft);
        bundle.putFloat(BundleKeys.KEY_ARCZOOMCHANGEHANDLER_TOP, sourceTop);
        bundle.putFloat(BundleKeys.KEY_ARCZOOMCHANGEHANDLER_WIDTH, sourceWidth);
        bundle.putFloat(BundleKeys.KEY_ARCZOOMCHANGEHANDLER_HEIGHT, sourceHeight);
    }

    @Override
    public void restoreFromBundle(@NonNull Bundle bundle) {
        super.restoreFromBundle(bundle);

        sourceLeft = bundle.getFloat(BundleKeys.KEY_ARCZOOMCHANGEHANDLER_LEFT);
        sourceTop = bundle.getFloat(BundleKeys.KEY_ARCZOOMCHANGEHANDLER_TOP);
        sourceWidth = bundle.getFloat(BundleKeys.KEY_ARCZOOMCHANGEHANDLER_WIDTH);
        sourceHeight = bundle.getFloat(BundleKeys.KEY_ARCZOOMCHANGEHANDLER_HEIGHT);
    }

    @Override @NonNull
    protected Animator getAnimator(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, boolean isPush, boolean toAddedToContainer) {
        AnimatorSet animator = new AnimatorSet();
        List<Animator> viewAnimators = new ArrayList<>();

        if (isPush && to != null) {
            float scaleX = sourceWidth / to.getWidth();
            float scaleY = sourceTop / to.getHeight();
            float left = sourceLeft - to.getWidth() / 2 * (1 - scaleX);
            float top = sourceTop - to.getHeight() / 2 * (1 - scaleY);
            viewAnimators.add(ObjectAnimator.ofFloat(to, View.ALPHA, 0, 1));
            viewAnimators.add(ObjectAnimator.ofFloat(to, View.TRANSLATION_X, left, 0));
            viewAnimators.add(ObjectAnimator.ofFloat(to, View.TRANSLATION_Y, top, 0));
            viewAnimators.add(ObjectAnimator.ofFloat(to, View.SCALE_X, scaleX, 1));
            viewAnimators.add(ObjectAnimator.ofFloat(to, View.SCALE_Y, scaleY, 1));
        } else if (!isPush && from != null) {
            float scaleX = sourceWidth / from.getWidth();
            float scaleY = sourceTop / from.getHeight();
            float left = sourceLeft - from.getWidth() / 2 * (1 - scaleX);
            float top = sourceTop - from.getHeight() / 2 * (1 - scaleY);
            viewAnimators.add(ObjectAnimator.ofFloat(from, View.ALPHA, 1, 0));
            viewAnimators.add(ObjectAnimator.ofFloat(from, View.TRANSLATION_X, left));
            viewAnimators.add(ObjectAnimator.ofFloat(from, View.TRANSLATION_Y, top));
            viewAnimators.add(ObjectAnimator.ofFloat(from, View.SCALE_X, scaleX));
            viewAnimators.add(ObjectAnimator.ofFloat(from, View.SCALE_Y, scaleY));
        }

        animator.playTogether(viewAnimators);
        return animator;
    }

    @Override
    protected void resetFromView(@NonNull View from) { }

    @Override @NonNull
    public ControllerChangeHandler copy() {
        return new ArcZoomChangeHandler(sourceLeft, sourceTop, sourceWidth, sourceHeight);
    }
}
