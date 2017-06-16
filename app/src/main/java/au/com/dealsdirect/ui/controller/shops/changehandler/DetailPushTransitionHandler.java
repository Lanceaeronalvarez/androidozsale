package au.com.dealsdirect.ui.controller.shops.changehandler;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.changehandler.TransitionChangeHandler;

import au.com.dealsdirect.ui.controller.productdetails.ProductDetailsView;

/**
 * dp Created by Admin on 6/15/17.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class DetailPushTransitionHandler  extends TransitionChangeHandler {


    private static final String KEY_FLAG_TRANSITION_NAME = "key_flag_transition_name";

    private String flagViewTransitionName;

    public DetailPushTransitionHandler() {}

    public DetailPushTransitionHandler(String flagViewTransitionName) {
        this.flagViewTransitionName = flagViewTransitionName;
    }

    @Override
    public void saveToBundle(@NonNull Bundle bundle) {
        bundle.putString(KEY_FLAG_TRANSITION_NAME, flagViewTransitionName);
    }

    @Override
    public void restoreFromBundle(@NonNull Bundle bundle) {
        flagViewTransitionName = bundle.getString(KEY_FLAG_TRANSITION_NAME);
    }

    @NonNull
    @Override
    protected Transition getTransition(@NonNull ViewGroup container,
            @Nullable View from,
            @Nullable View to,
            boolean isPush) {

        if (to == null || !(to instanceof ProductDetailsView)) {
            Log.d("test","null");
            throw new IllegalArgumentException("The to view must be a CountryDetailView");
        }

        Log.d("test","not null");

        final ProductDetailsView detailView = (ProductDetailsView) to;
        detailView.mProductSharedImage.setTransitionName(flagViewTransitionName);

        ChangeTransform changeTransform = new ChangeTransform();

        // Shared elements (the flag view in this case) are drawn in the window's view overlay during the transition by default.
        // That causes the favourite fab being drawn behind the flag when it is scaled up.
        // Setting the change transform not using overlay addresses this issue.
        changeTransform.setReparentWithOverlay(false);

        return new TransitionSet()
                .addTransition(new TransitionSet()
                                       .addTransition(new ChangeBounds())
                                       .addTransition(new ChangeClipBounds())
                                       .addTransition(changeTransform)
                                       .addTransition(new ChangeImageTransform())
                                       .setDuration(300))
                .setInterpolator(new FastOutSlowInInterpolator());
    }
}
