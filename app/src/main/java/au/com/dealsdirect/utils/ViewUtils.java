package au.com.dealsdirect.utils;

import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v4.widget.NestedScrollView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ScrollView;


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

    public static boolean isViewVisibleInScrollView(View view, ScrollView scrollView) {
        return isViewVisibleInScrollView(view, scrollView, 0, 0);
    }

    public static boolean isViewVisibleInScrollView(View view, ScrollView scrollView, float topOffset, float bottomOffset) {
        Rect scrollBounds = new Rect();
        scrollView.getDrawingRect(scrollBounds);

        Rect offsetViewBounds = new Rect();
        view.getDrawingRect(offsetViewBounds);
        scrollView.offsetDescendantRectToMyCoords(view, offsetViewBounds);

        float top = offsetViewBounds.top;
        float bottom = offsetViewBounds.bottom;
        return (scrollBounds.top + topOffset) < top && (scrollBounds.bottom + bottomOffset) > bottom;
    }

    public static boolean isViewVisibleInScrollView(View view, NestedScrollView scrollView) {
        return isViewVisibleInScrollView(view, scrollView, 0, 0);
    }

    public static boolean isViewVisibleInScrollView(View view, NestedScrollView scrollView, float topOffset, float bottomOffset) {
        Rect scrollBounds = new Rect();
        scrollView.getDrawingRect(scrollBounds);

        Rect offsetViewBounds = new Rect();
        view.getDrawingRect(offsetViewBounds);
        scrollView.offsetDescendantRectToMyCoords(view, offsetViewBounds);

        float top = offsetViewBounds.top;
        float bottom = offsetViewBounds.bottom;

        return (scrollBounds.top + topOffset) < top && (scrollBounds.bottom + bottomOffset) > bottom;
    }
}
