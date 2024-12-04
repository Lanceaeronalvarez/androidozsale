package au.com.dealsdirect.ui.custom.transitions;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.ArcMotion;
import androidx.transition.ChangeBounds;
import androidx.transition.ChangeClipBounds;
import androidx.transition.ChangeTransform;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionSet;

import com.bluelinelabs.conductor.changehandler.androidxtransition.TransitionChangeHandler;

public class SharedArcFadePopChangeHandler extends TransitionChangeHandler {

    public SharedArcFadePopChangeHandler() {
    }

    @Override
    public void setForceRemoveViewOnPush(boolean force) {
        super.setForceRemoveViewOnPush(force);
    }

    @Override
    @NonNull
    protected Transition getTransition(@NonNull ViewGroup container, View from, View to, boolean isPush) {

        TransitionSet transition = new TransitionSet()
                .setOrdering(TransitionSet.ORDERING_SEQUENTIAL)
                .addTransition(new TransitionSet().addTransition(new ChangeBounds()).addTransition(new ChangeClipBounds()).addTransition(new ChangeTransform()))
                .addTransition(new Fade(Fade.IN));

        transition.setPathMotion(new ArcMotion());

        return transition;
    }

    @Override
    public void performChange(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, boolean isPush, @NonNull ControllerChangeCompletedListener changeListener) {
        super.performChange(container, from, to, isPush, changeListener);
    }

    @Override
    public boolean removesFromViewOnPush() {
        return true;
    }

    @Override
    public void prepareForTransition(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, @NonNull Transition transition, boolean isPush, @NonNull OnTransitionPreparedListener onTransitionPreparedListener) {
        super.prepareForTransition(container, from, to, transition, isPush, onTransitionPreparedListener);

    }
}
