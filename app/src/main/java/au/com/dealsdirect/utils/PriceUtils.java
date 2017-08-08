package au.com.dealsdirect.utils;



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
//        return value % 1 == 0 ? TEMP_CURRENCY_SIGN + String.format("%.0f", value) : TEMP_CURRENCY_SIGN + String.format("%.2f", value);
        return HIDECENTS ? TEMP_CURRENCY_SIGN + String.format("%.0f", value) : TEMP_CURRENCY_SIGN + String.format("%.2f", value);
    }

    public static String getProductRpStringValue(Double value) {
        return HIDECENTS ? TEMP_CURRENCY_SIGN + String.format("%.0f", value) : TEMP_CURRENCY_SIGN + String.format("%.2f", value);
    }

}
