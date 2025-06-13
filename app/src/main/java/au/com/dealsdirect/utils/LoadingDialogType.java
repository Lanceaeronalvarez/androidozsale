package au.com.dealsdirect.utils;

import au.com.dealsdirect.R;

public enum LoadingDialogType {
    DEFAULT(0),
    NOLOGO(1),
    OURPAY(2),
    GPAY(3),
    AFTERPAY(4),
    LPAY(5),
    KLARNA(6),
    ZIPPAY(7);

    private final int type;
    private static final int[] layouts = new int[]{
            R.layout.progress_dialog,
            R.layout.progress_dialog_no_logo,
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