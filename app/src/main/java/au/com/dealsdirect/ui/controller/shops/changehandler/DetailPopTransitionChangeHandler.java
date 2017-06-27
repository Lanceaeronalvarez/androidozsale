package au.com.dealsdirect.ui.controller.shops.changehandler;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.changehandler.TransitionChangeHandler;

import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsView;

/**
 * dp Created by Admin on 6/15/17.
 */

public class DetailPopTransitionChangeHandler extends TransitionChangeHandler {

    private static final String KEY_FLAG_TRANSITION_NAME = "key_flag_transition_name";

    private String flagViewTransitionName;

    public DetailPopTransitionChangeHandler() {
        this.flagViewTransitionName = null;
    }

    public DetailPopTransitionChangeHandler(String flagViewTransitionName) {
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

        if (from == null || !(from instanceof View))
            throw new IllegalArgumentException("The from view must be a CountryDetailView");

        SaleItemDetailsView itemImage = (SaleItemDetailsView) from;
        itemImage.mProductSharedImage.setTransitionName(flagViewTransitionName);


        return new TransitionSet()
                .addTransition(new TransitionSet()
                                       .addTransition(new ChangeBounds())
                                       .addTransition(new ChangeClipBounds())
                                       .addTransition(new ChangeTransform())
                                       .addTransition(new ChangeImageTransform())
                                       .addTransition(new Slide()).addTarget(itemImage.mSaleItemDetailsView));
    }

    protected void getTransitionImage(@NonNull ViewGroup container,
            @NonNull View from, @Nullable View to, boolean isPush){

        int tempFromId = ViewGroup.FOCUS_BEFORE_DESCENDANTS;
    }

}
