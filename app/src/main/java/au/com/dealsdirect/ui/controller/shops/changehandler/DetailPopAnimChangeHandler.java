package au.com.dealsdirect.ui.controller.shops.changehandler;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler;

import au.com.dealsdirect.ui.controller.productdetails.ProductDetailsView;

/**
 * dp Created by Admin on 6/15/17.
 */

public class DetailPopAnimChangeHandler extends AnimatorChangeHandler {

    @NonNull
    @Override
    protected Animator getAnimator(@NonNull ViewGroup container,
            @Nullable View from,
            @Nullable View to,
            boolean isPush,
            boolean toAddedToContainer) {

        // Make sure the from view is a CountryDetailView
        if (from == null || !(from instanceof ProductDetailsView))
            throw new IllegalArgumentException("The from view must be a CountryDetailView");

        if (to == null)
            throw new IllegalArgumentException("The to view must not be null");

        final ProductDetailsView detailView = (ProductDetailsView) from;

        AnimatorSet animatorSet = new AnimatorSet();

        // Set the to View's alpha to 0 to hide it at the beginning.
        to.setAlpha(0);
//
//        // Scale down to hide the fab button
//        PropertyValuesHolder fabScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0);
//        PropertyValuesHolder fabScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0);
//        Animator hideFabButtonAnimator = ObjectAnimator.ofPropertyValuesHolder(detailView.favouriteFab, fabScaleX, fabScaleY);

        // Slide up the flag
        Animator flagAnimator = ObjectAnimator.ofFloat(detailView.mProductSharedImage, View.TRANSLATION_Y, 0,
                                                       -detailView.mProductSharedImage.getHeight());

//        // Slide down the details
//        Animator detailAnimator = ObjectAnimator.ofFloat(detailView.detailGroup, View.TRANSLATION_Y, 0, detailView.detailGroup.getHeight());

        // Show the new view
        Animator showToViewAnimator = ObjectAnimator.ofFloat(to, View.ALPHA, 0, 1);

        animatorSet.playTogether(flagAnimator, showToViewAnimator);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new FastOutLinearInInterpolator());

        animatorSet.start();

        return animatorSet;
    }

    @Override
    protected void resetFromView(@NonNull View from) {

        ProductDetailsView detailView = (ProductDetailsView) from;
//        detailView.favouriteFab.setScaleX(1);
//        detailView.favouriteFab.setScaleY(1);
        detailView.mProductSharedImage.setTranslationY(0);
//        detailView.detailGroup.setTranslationY(0);
    }
}
