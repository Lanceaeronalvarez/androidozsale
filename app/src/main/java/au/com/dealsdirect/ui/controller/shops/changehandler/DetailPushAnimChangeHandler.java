package au.com.dealsdirect.ui.controller.shops.changehandler;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler;

import au.com.dealsdirect.ui.controller.productdetails.ProductDetailsView;

/**
 * dp Created by Admin on 6/15/17.
 */

public class DetailPushAnimChangeHandler extends AnimatorChangeHandler {

    @NonNull
    @Override
    protected Animator getAnimator(@NonNull ViewGroup container,
            @Nullable View from,
            @Nullable View to,
            boolean isPush,
            boolean toAddedToContainer) {


        // Make sure the to view is a CountryDetailView
        if (to == null || !(to instanceof ProductDetailsView))
            throw new IllegalArgumentException("The to view must be a CountryDetailView");

        ProductDetailsView detailView = (ProductDetailsView) to;

        // Set the button scale to 0 to make it invisible at the beginning.
//        detailView.favouriteFab.setScaleX(0);
//        detailView.favouriteFab.setScaleY(0);

        AnimatorSet animatorSet = new AnimatorSet();

        AnimatorSet flagAndDetailAnim = new AnimatorSet();

        // Hide the old view
        Animator hideFromViewAnim = ObjectAnimator.ofFloat(from, View.ALPHA, 1, 0);

        // Slide down the flag
        Animator flagAnim = ObjectAnimator.ofFloat(detailView.mProductSharedImage, View
                                                           .TRANSLATION_Y, -detailView.mProductSharedImage
                                                           .getHeight(),
                                                   0);

        // Slide up the details
//        Animator detailAnim = ObjectAnimator.ofFloat(detailView.detailGroup, View.TRANSLATION_Y, detailView.detailGroup.getHeight(), 0);

        flagAndDetailAnim.playTogether(hideFromViewAnim, flagAnim);
        flagAndDetailAnim.setDuration(300);
        flagAndDetailAnim.setInterpolator(new FastOutSlowInInterpolator());

        // Scale up the favourite fab
        PropertyValuesHolder fabScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0, 1);
        PropertyValuesHolder fabScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0, 1);
//        Animator favouriteAnim = ObjectAnimator.ofPropertyValuesHolder(detailView.favouriteFab, fabScaleX, fabScaleY)
//                                               .setDuration(200);

        animatorSet.playSequentially(flagAndDetailAnim);

        animatorSet.start();

        return animatorSet;
    }

    @Override
    protected void resetFromView(@NonNull View from) {
        from.setAlpha(1);
    }
}
