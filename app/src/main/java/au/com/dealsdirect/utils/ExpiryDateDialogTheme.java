package au.com.dealsdirect.utils;

import android.app.Activity;

import au.com.dealsdirect.R;

/**
 * Created by MTC on 2019-10-17.
 */
public enum ExpiryDateDialogTheme {

    LIGHT(R.color.ed_black_87, R.color.ed_white_87, R.color.ed_black_38),
    DARK(R.color.ed_white_87, R.color.ed_black_87, R.color.ed_white_38);

    private final int mItemTextColor;
    private final int mItemInverseTextColor;
    private final int mItemDisabledTextColor;

    private int mResolvedItemTextColor;
    private int mResolvedItemInverseTextColor;
    private int mResolvedItemDisabledTextColor;
    private int mResolvedSelectedItemBackground;

    ExpiryDateDialogTheme(int itemTextColor, int itemInverseTextColor, int itemDisabledTextColor) {
        mItemTextColor = itemTextColor;
        mItemInverseTextColor = itemInverseTextColor;
        mItemDisabledTextColor = itemDisabledTextColor;
    }

    public static ExpiryDateDialogTheme detectTheme(Activity activity) {
        ExpiryDateDialogTheme theme;
        if (ViewUtils.isDarkBackground(activity)) {
            theme = ExpiryDateDialogTheme.LIGHT;
        } else {
            theme = ExpiryDateDialogTheme.DARK;
        }

        theme.mResolvedItemTextColor = activity.getResources().getColor(theme.mItemTextColor);
        theme.mResolvedItemInverseTextColor = ColorUtils.getColor(activity,
                "textColorPrimaryInverse", theme.mItemInverseTextColor);
        theme.mResolvedItemDisabledTextColor = activity.getResources()
                .getColor(theme.mItemDisabledTextColor);

        //removed color accent to being resolved as the selected item background
        theme.mResolvedSelectedItemBackground = ColorUtils.getColor(activity, "", R.color.bt_blue);

        return theme;
    }

    public int getItemTextColor() {
        return mResolvedItemTextColor;
    }

    public int getItemInvertedTextColor() {
        return mResolvedItemInverseTextColor;
    }

    public int getItemDisabledTextColor() {
        return mResolvedItemDisabledTextColor;
    }

    public int getSelectedItemBackground() {
        return mResolvedSelectedItemBackground;
    }

}
