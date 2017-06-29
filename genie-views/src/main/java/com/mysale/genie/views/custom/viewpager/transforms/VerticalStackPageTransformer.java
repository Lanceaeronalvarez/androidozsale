package com.mysale.genie.views.custom.viewpager.transforms;
/*
 * Created by CodeineBot on 12/13/16.
 */

import android.support.v4.view.ViewPager;
import android.view.View;

public class VerticalStackPageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float position) {
        page.setTranslationX(page.getWidth() * -position);
        if (position < 0) {
            page.setTranslationY(position * page.getHeight());
        } else {
            page.setTranslationY(0f);
        }
    }
}
