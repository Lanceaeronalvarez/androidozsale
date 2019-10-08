package au.com.dealsdirect.ui.custom.transitions;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link AnimatorChangeHandler} that will slide either slide a new View up or slide an old View down,
 * depending on whether a push or pop change is happening.
 */
public class ReverseVerticalChangeHandler extends AnimatorChangeHandler {

    public ReverseVerticalChangeHandler() { }

    public ReverseVerticalChangeHandler(boolean removesFromViewOnPush) {
        super(removesFromViewOnPush);
    }

    public ReverseVerticalChangeHandler(long duration) {
        super(duration);
    }

    public ReverseVerticalChangeHandler(long duration, boolean removesFromViewOnPush) {
        super(duration, removesFromViewOnPush);
    }

    @Override @NonNull
    protected Animator getAnimator(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, boolean isPush, boolean toAddedToContainer) {
        AnimatorSet animator = new AnimatorSet();
        List<Animator> viewAnimators = new ArrayList<>();

        if (isPush && to != null) {
            viewAnimators.add(ObjectAnimator.ofFloat(to, View.TRANSLATION_Y, -to.getHeight(), 0));
        } else if (!isPush && from != null) {
            viewAnimators.add(ObjectAnimator.ofFloat(from, View.TRANSLATION_Y, 0, -from.getHeight()));
        }

        animator.playTogether(viewAnimators);
        return animator;
    }

    @Override
    protected void resetFromView(@NonNull View from) { }

    @Override @NonNull
    public ControllerChangeHandler copy() {
        return new ReverseVerticalChangeHandler(getAnimationDuration(), removesFromViewOnPush());
    }

}
