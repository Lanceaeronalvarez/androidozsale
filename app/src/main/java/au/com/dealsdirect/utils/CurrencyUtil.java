package au.com.dealsdirect.utils;
/*
 * Created by CodeineBot on 11/27/17.
 */

public class CurrencyUtil {

    public static String getCurrency(String countryCode) {

        switch (countryCode.toUpperCase()) {
            case "AS":
            case "TA":
            case "OA":
            case "DA":
            case "BA":
            case "CA":
            case "LA":
                return "AUD";
            case "NZ":
            case "BN":
            case "CN":
            case "LN":
                return "NZD";
            case "CU":
            case "LU":
            case "UK":
                return "GBP";
            case "MY":
                return "MYR";
            case "PH":
                return "PHP";
            case "TH":
                return "THB";
            case "HK":
                return "HKD";
            case "SI":
                return "SGD";
            default:
                return "";
        }
    }
}
