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
        return Math.round(px / (densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int dpToPx(float dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
