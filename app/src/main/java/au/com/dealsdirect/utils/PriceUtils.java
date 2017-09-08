package au.com.dealsdirect.utils;

import java.util.Locale;

public class PriceUtils {

    public static final String TEMP_CURRENCY_SIGN = "$";

    static final boolean HIDECENTS = false;

    public static String getRpStringValue(Double value) {

        if (value > 0) {
            return getPriceStringValue(value);
        }

        return "";
    }

    public static String getPriceStringValue(Double value) {
        String decimal = value % 1 == 0 ? "%.0f" : "%.2f";
        return TEMP_CURRENCY_SIGN + String.format(Locale.ENGLISH, decimal, value);
    }

    public static String getVoucherStringValue(String value){
        return TEMP_CURRENCY_SIGN + value;
    }
}
