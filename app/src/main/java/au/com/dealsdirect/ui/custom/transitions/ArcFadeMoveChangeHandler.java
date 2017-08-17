package au.com.dealsdirect.ui.custom.transitions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.ArcMotion;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.changehandler.TransitionChangeHandler;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsView;

/**
 * dp Created by Admin on 6/8/17.
 */

public class ArcFadeMoveChangeHandler extends TransitionChangeHandler {

    public ArcFadeMoveChangeHandler() { }

    @Override
    @NonNull
    protected Transition getTransition(@NonNull ViewGroup container, View from, View to, boolean isPush) {


        if (to == null || !(to instanceof SaleItemDetailsView)) {
            throw new IllegalArgumentException("The to view must be a CountryDetailView");
        }

        final SaleItemDetailsView detailView = (SaleItemDetailsView) to;

        ChangeTransform changeTransform = new ChangeTransform();

        // Shared elements (the flag view in this case) are drawn in the window's view overlay during the transition by default.
        // That causes the favourite fab being drawn behind the flag when it is scaled up.
        // Setting the change transform not using overlay addresses this issue.
//        changeTransform.setReparentWithOverlay(false);

        TransitionSet transition = new TransitionSet()
                .addTransition(new TransitionSet()
                                       .addTransition(new ChangeBounds())
                                       .addTransition(new ChangeClipBounds())
                                       .addTransition(changeTransform))
//                                       .addTransition(new ChangeImageTransform())
                .addTransition(new Slide().addTarget(detailView.mSaleItemDetailsView).setStartDelay(150))
                .addTransition(new Fade(Fade.IN));
//
        transition.setPathMotion(new ArcMotion());

        return transition;
    }

    @Override public boolean removesFromViewOnPush() {
        return false;
    }

    @Override public void executePropertyChanges(@NonNull ViewGroup container, @Nullable View from,
            @Nullable View to, @Nullable Transition transition, boolean isPush) {
        super.executePropertyChanges(container, from, to, transition, isPush);
        if (from != null && (removesFromViewOnPush() || !isPush) && from.getParent() == container) {
            container.removeView(from);
        }
        if (to != null && to.getParent() == null) {
            from.findViewById(R.id.vh_sale_item_image).setTransitionName("none");
            container.addView(to);
        }
    }
}
