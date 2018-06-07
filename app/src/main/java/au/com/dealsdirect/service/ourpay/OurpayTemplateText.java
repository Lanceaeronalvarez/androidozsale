package au.com.dealsdirect.service.ourpay;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;

import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.PriceUtils;

/**
 * dp Created on 8/4/17.
 */

public class OurpayTemplateText {

    public enum DeliveryOptions{
        STANDARD ("STANDARD"),
        EXPRESS ("EXPRESS"),
        OURPAYSELECT ("OURPAYSELECT");

        private final String name;

        private DeliveryOptions(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

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


//    DELIVERY OPTIONS/OURPAY SELECT
    public static final String KEY_DELIVERYOPTION_FREE_DELIVERY_QTY = "[[FreeDeliveryQty]]";
    public static final String KEY_DELIVERYOPTION_OPS_FREE = "_Free";
    public static final String KEY_DELIVERYOPTION_OPS_TITLE = "_DeliveryOption_OURPAYSELECT_Title";
    public static final String KEY_DELIVERYOPTION_OPS_DESCRIPTION = "_DeliveryOption_OURPAYSELECT_Description";

    public static final String KEY_DELIVERYOPTION_EXPRESS_TITLE = "_DeliveryOption_EXPRESS_Title";
    public static final String KEY_DELIVERYOPTION_EXPRESS_DESCRIPTION = "_DeliveryOption_EXPRESS_Description";
    public static final String KEY_DELIVERYOPTION_STANDARD_TITLE = "_DeliveryOption_STANDARD_Title";

    public static final String KEY_OURPAY_OPS_DESCRIPTION_REMAINING= "_Ops_description_remaining";
    public static final String KEY_OURPAY_OPS_INFO_REMAINING_BEFORE_PURCHASE = "_Ops_info_remaining_before_purchase";
    public static final String KEY_OURPAY_OPS_INFO_REMAINING_BEFORE_PURCHASE_FREE_DELIVERY = "_Ops_info_remaining_before_purchase_free_delivery";

    public static final String KEY_OURPAY_OPS_TNC_HEADER = "_OurPaySelectTermsAndConditionsHeader";
    public static final String KEY_OURPAY_OPS_TNC_BODY = "_OurPaySelectTermsAndConditionsBody";
    public static final String KEY_OPS_TNC_FULL_TEXT = "OurPayTermsAndConditions_Text";


    public static String getTemplateText(Activity activity, Ourpay ourpay){
        try {
            String templateTexts = ((MainActivity) activity).getMyTemplateTexts(ourpay.getDetails());
            return parseTextSymbolsInString(templateTexts,ourpay);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String getText(Activity activity, String key) {
        try {
            String templateTexts = ((MainActivity) activity).getMyTemplateTexts(key);
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
