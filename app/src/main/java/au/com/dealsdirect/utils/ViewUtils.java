package au.com.dealsdirect.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import au.com.dealsdirect.R;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;


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

    public static int dp2px(Context context, float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics()));
    }

    public static boolean isDarkBackground(Activity activity) {
        int color = activity.getResources().getColor(R.color.ed_white);
        try {
            Drawable background = activity.getWindow().getDecorView().getRootView().getBackground();
            if (background instanceof ColorDrawable) {
                color = ((ColorDrawable) background).getColor();
            }
        } catch (Exception ignored) {
        }

        double luminance = (0.2126 * Color.red(color)) + (0.7152 * Color.green(color)) +
                (0.0722 * Color.blue(color));

        return luminance < 128;
    }

    public static void changeFontInViewGroup(ViewGroup viewGroup, String fontPath, float textSize) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (TextView.class.isAssignableFrom(child.getClass())) {
                CalligraphyUtils.applyFontToTextView(child.getContext(), (TextView) child, fontPath);
                ((TextView) child).setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            } else if (ViewGroup.class.isAssignableFrom(child.getClass())) {
                changeFontInViewGroup((ViewGroup) viewGroup.getChildAt(i), fontPath, textSize);
            }
        }
    }
}
