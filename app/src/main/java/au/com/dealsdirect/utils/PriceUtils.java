package au.com.dealsdirect.utils;


/*
 * Created by Ayi on 18/05/2017.
 */

import java.text.DecimalFormat;

public class PriceUtils {

    public static String convertDoubleToSaleString(double cost){
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String price = "$"+cost;
        return price;
    }

}
