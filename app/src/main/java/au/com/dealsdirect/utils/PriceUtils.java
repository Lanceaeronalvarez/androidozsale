package au.com.dealsdirect.utils;


/*
 * Created by Ayi on 18/05/2017.
 */

import java.text.DecimalFormat;

public class PriceUtils {

    public static final String TEMP_CURRENCY_SIGN = "$";
//    public static String convertForecastToSaleString(SaleSearchResponse.Forecast forecast){
//        DecimalFormat formatter = new DecimalFormat("#,###,###");
//        String price = forecast.getValue() == null ? "" : formatter.format(forecast.getValue());
//        String currency = forecast.getCurrency() == null ? "" : forecast.getCurrency() + " ";
//        return "Forecast: " + currency + price;
//    }

    static final boolean HIDECENTS = true;

    public static String getRpStringValue(Double value) {

        if (value > 0) {
            return getPriceStringValue(value);
        }

        return "";
    }

    public static String getPriceStringValue(Double value) {
        return value % 1 == 0 ? TEMP_CURRENCY_SIGN + String.format("%.0f", value) : TEMP_CURRENCY_SIGN + String.format("%.2f", value);
        //return HIDECENTS ? Config.getCurrencySign() + String.format("%.0f", value) : Config.getCurrencySign() + String.format("%.2f", value);
    }

}
