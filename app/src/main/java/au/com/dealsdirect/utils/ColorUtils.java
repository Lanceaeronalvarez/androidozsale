package au.com.dealsdirect.utils;

import android.app.Activity;
import android.util.TypedValue;

/**
 * Created by Ayi on 08/06/2017.
 */

public class ColorUtils {

    private static String[] mColors = new String[]{
            "#C7E4ED",  // R.color.red_oval_icon
            "#EDC7D5",  // R.color.blue_oval_icon
            "#CAEDC7",  // R.color.yellow_oval_icon
            "#D4C7ED"  // R.color.green_oval_icon
    };

    public static int getColorsSize() {
        return mColors.length;
    }

    public static String getOvalColor(int index) {
        return mColors[index % mColors.length];
    }

    public static int getColor(Activity activity, String attr, int fallbackColor) {
        TypedValue color = new TypedValue();
        try {
            int colorId = activity.getResources().getIdentifier(attr, "attr", activity.getPackageName());
            if (activity.getTheme().resolveAttribute(colorId, color, true)) {
                return color.data;
            }
        } catch (Exception ignored) {}

        return activity.getResources().getColor(fallbackColor);
    }
}
