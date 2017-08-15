package au.com.dealsdirect.service.ourpay;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.PriceUtils;

/**
 * dp Created on 8/4/17.
 */

public class OurpayTemplateText {

    public static final String KEY_CODE_LOGO = "{0}";
    public static final String KEY_CODE_MIN = "{1}";
    public static final String KEY_CODE_MAX = "{2}";
    public static final String KEY_BILLING_PERIOD = "[[billingPeriod]]";
    public static final String KEY_TRANSACTION_COUNT = "[[transactionCount]]";


    public static final String KEY_CHECKOUT_MYPAY_PAY_EXCEED_LIMIT = "_checkoutMyPayPayExceedLimit";
    public static final String KEY_CHECKOUT_MYPAY_PAY_INVALID_PAYMENT_METHOD = "_checkoutMyPayPayInvalidPaymentMethod";
    public static final String KEY_CHECKOUT_MYPAY_PAY_OUT_OF_RANGE_MOBILE_APP = "_checkoutMyPayPayOutOfRangeMobileApp";
    public static final String KEY_CHECKOUT_MYPAY_PAY_OUT_UP_TO_MOBILE_APP = "_checkoutMyPayPayOutUpToMobileApp";
    public static final String KEY_CHECKOUT_MYPAY_PAY_UNTRUSTED = "_checkoutMyPayPayUntrusted";
    public static final String KEY_MYPAY_DETAILS_MOBILE_APP = "myPayDetailsMobileApp";
    public static final String KEY_OURPAY_THANK_YOU_TEXT = "_OurPayThankYouTextMobileApp";
    public static final String KEY_OURPAY_TC_TEXT = "_OurPayTC_text"; // Using web's template text for hyper link
    public static final String KEY_OURPAY_TC_VALIDATION_FAILED = "_OurPayTCValidationFailed";
    public static final String KEY_PAYMENT_SCHEDULE = "_PaymentSchedule";


    private static String[] templateTextsKeys = {
            KEY_CHECKOUT_MYPAY_PAY_EXCEED_LIMIT, //0
            KEY_CHECKOUT_MYPAY_PAY_INVALID_PAYMENT_METHOD, //1
            KEY_CHECKOUT_MYPAY_PAY_OUT_OF_RANGE_MOBILE_APP, //2
            KEY_CHECKOUT_MYPAY_PAY_OUT_UP_TO_MOBILE_APP, //3
            KEY_CHECKOUT_MYPAY_PAY_UNTRUSTED, //4
            KEY_MYPAY_DETAILS_MOBILE_APP, //5
            KEY_OURPAY_THANK_YOU_TEXT, //6
            KEY_OURPAY_TC_TEXT, //7
            KEY_OURPAY_TC_VALIDATION_FAILED, //8
            KEY_PAYMENT_SCHEDULE //9
    };

    public static String getTemplateText(Activity activity, Ourpay ourpay){
        try {
            String templateTexts = ((MainActivity) activity).getMyTemplateTexts(ourpay.getDetails());
            Log.d("Checkout", "detail = "+ourpay.getDetails());
            return parseTextSymbolsInString(templateTexts,ourpay);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String getText(Activity activity, String key) {
        try {
            String templateTexts = ((MainActivity) activity).getMyTemplateTexts(key);
            Log.d("Checkout", "detail = "+key);
            return templateTexts;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String parseTextSymbolsInString(String details, Ourpay ourpay) {

        details = details.replace(KEY_CODE_LOGO, "[img src=ourpay_logo/]");
        details = details.replace(KEY_CODE_MIN, PriceUtils.getPriceStringValue(ourpay.getMinAmount()));
        details = details.replace(KEY_CODE_MAX, PriceUtils.getPriceStringValue(ourpay.getMaxAmount()));
        details = details.replace(KEY_BILLING_PERIOD, ourpay.getBillingPeriod() + "");
        details = details.replace(KEY_TRANSACTION_COUNT, ourpay.getTransactionCount() + "");
        Log.d("Checkout", "detail = "+details);

        return details;

    }

    private static JSONArray constructArrayToJsonArray(String[] templateTextsKeys) {

        JSONArray jsonArray = new JSONArray();
        for (int i=0; i<templateTextsKeys.length; i++) {
            try {
                jsonArray.put(i, templateTextsKeys[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

}
