package au.com.dealsdirect.utils;

import au.com.dealsdirect.R;

public enum LoadingDialogType {
    DEFAULT(0),
    OURPAY(1),
    GPAY(2),
    AFTERPAY(3),
    LPAY(4),
    KLARNA(5),
    ZIPPAY(6);

    private final int type;
    private static final int[] layouts = new int[]{
            R.layout.progress_dialog,
            R.layout.ourpay_loading_indicator_layout,
            R.layout.gpay_loading_indicator_layout,
            R.layout.afterpay_loading_indicator_layout,
            R.layout.lpay_loading_indicator_layout,
            R.layout.klarna_loading_indicator_layout,
            R.layout.zippay_loading_indicator_layout
    };

    LoadingDialogType(int type) {
        this.type = type;
    }

    public int toLayout() {
        return layouts[type];
    }
}