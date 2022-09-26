package au.com.dealsdirect.utils;

import java.util.Locale;

import au.com.dealsdirect.ui.controller.main.Settings;

public class PriceUtils {

    public static final String TEMP_CURRENCY_SIGN = "$";

    static final boolean HIDECENTS = false;

    public static String getRpStringValue(Double value) {

        if (value != null && value > 0) {
            return getPriceStringValue(value);
        }

        return "";
    }

    public static String getPriceStringValue(Float value) {
        return getPriceStringValue(value, false);
    }

    public static String getPriceStringValue(Float value, boolean alwaysShowDecinal) {
        return getPriceStringValue(value == null ? 0 : value.doubleValue(), alwaysShowDecinal);
    }

    public static String getPriceStringValue(Double value) {
        return getPriceStringValue(value, false);
    }

    public static String getPriceStringValue(Double value, boolean alwaysShowDecimal) {
        if (value == null) {
            value = 0d;
        }
        String decimal = value % 1 == 0 && !alwaysShowDecimal ? "%.0f" : "%.2f";
        return Settings.getSelectedCountry().currencySign + String.format(Locale.ENGLISH, decimal, value);
    }
}
