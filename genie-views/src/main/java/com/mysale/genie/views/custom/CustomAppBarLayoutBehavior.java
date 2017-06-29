package com.mysale.genie.views.custom;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by smartwave on 14/03/2017.
 */

public class CustomAppBarLayoutBehavior extends AppBarLayout.Behavior {

    private CoordinatorLayoutAsBottomSheetBehavior behavior;

    public CustomAppBarLayoutBehavior() {
    }

    public CustomAppBarLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        behavior = CoordinatorLayoutAsBottomSheetBehavior.from((ViewGroup)parent.getParent());
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed) {
        if(behavior != null && behavior.getState() == CoordinatorLayoutAsBottomSheetBehavior.STATE_DRAGGING){
            return;
        }else {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        }
    }

    @Override
    public void setDragCallback(@Nullable DragCallback callback) {
        super.setDragCallback(callback);
    }
}