package au.com.dealsdirect.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.support.design.widget.TabLayout;
import android.view.ViewGroup;

import au.com.dealsdirect.ui.main.MainActivity;


public final class ViewUtils {

    private ViewUtils() {
        // This utility class is not publicly instantiable
    }

    public static float pxToDp(float px) {
        float densityDpi = Resources.getSystem().getDisplayMetrics().densityDpi;
        return px / (densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int dpToPx(float dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public static void changeIconDrawableToGray(Context context, Drawable drawable) {
        if (drawable != null) {
            drawable.mutate();
//            drawable.setColorFilter(ContextCompat
//                    .getColor(context, R.color.dark_gray), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public static void setDynamicTabLayout(TabLayout mTabLayout, MainActivity mActivity) {
        if (mTabLayout.getWidth() < mActivity.getResources().getDisplayMetrics().widthPixels) {
            mTabLayout.setTabMode(TabLayout.MODE_FIXED);
            ViewGroup.LayoutParams mParams = mTabLayout.getLayoutParams();
            mParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            mTabLayout.setLayoutParams(mParams);
        }
    }
}
