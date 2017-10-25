package au.com.dealsdirect.ui.controller.categories;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.AttributeSet;

/**
 * dp Created by Admin on 10/26/16.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FilterCategoriesTransition extends TransitionSet {

    public FilterCategoriesTransition() {
        init();
    }

    public FilterCategoriesTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds())
                .addTransition(new ChangeTransform());
    }
}
