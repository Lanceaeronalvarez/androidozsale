package au.com.dealsdirect.service.ourpay;

import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;

/**
 * dp Created by Admin on 8/8/17.
 */

public class OurpayStateManager {

    public static final String CARD_PAYPAL = "Paypal";
    public static final String CARD_MASTERPASS = "Masterpass";
    public static final String CARD_MASTERCARD = "MasterCard";
    public static final String CARD_VISA = "Visa";
    private static final String PAYMENT_TYPE_MYPAY = "mypay";

    public static void setDetails(Ourpay ourpay, boolean isMyPayEnabled) {

        assert ourpay != null;

        if (!isMyPayEnabled) {
            ourpay.setState(OurpayState.DISABLED);
        } else if (!ourpay.isCanUse()) {
            ourpay.setState(ourpay.getState() | OurpayState.ERROR);

            if (ourpay.getErrorCode().equalsIgnoreCase(OurpayError.EXCEED_DEBT_LIMIT)) {
                //"You can't pay with {0} now because you exceed your credit limit. You could pay with {0} after next payment on the earlier purchase."

                ourpay.setDetails(OurpayTemplateText.KEY_CHECKOUT_MYPAY_PAY_EXCEED_LIMIT);
            } else if (ourpay.getErrorCode().equalsIgnoreCase(OurpayError.INVALID_PAYMENT_METHOD)) {
                //"You can pay less with {0} now, but we don't support your current payment method for it. If you choose another payment method you could use it."

                ourpay.setDetails(OurpayTemplateText.KEY_CHECKOUT_MYPAY_PAY_INVALID_PAYMENT_METHOD);
            } else if (ourpay.getErrorCode().equalsIgnoreCase(OurpayError.CUSTOMER_UNTRUSTED)) {
                //"Sorry, selected payment method is disabled now."

                ourpay.setDetails(OurpayTemplateText.KEY_CHECKOUT_MYPAY_PAY_UNTRUSTED);

            } else if (ourpay.getErrorCode().equalsIgnoreCase(OurpayError.AMOUNT_OUT_OF_RANGE)
                    || isPriceOutOfRange(ourpay)) {
                if (ourpay.getMinAmount() == 0) {
                    ////"Pay less today with {0}. Only available for order up to {2}"
                    ourpay.setDetails(OurpayTemplateText.KEY_CHECKOUT_MYPAY_PAY_OUT_UP_TO_MOBILE_APP);
                } else {
                    //"Pay less today with {0}. Only available for orders between {1} and {2}"
                    ourpay.setDetails(OurpayTemplateText.KEY_CHECKOUT_MYPAY_PAY_OUT_OF_RANGE_MOBILE_APP);
                }
            } else {
                ourpay.setState(OurpayState.DISABLED);
            }
        } else {
            if (0 != (ourpay.getState() & OurpayState.POSTCART)) {
                ourpay.setDetails(OurpayTemplateText.KEY_PAYMENT_SCHEDULE);
            } else {
                //"[[transactionCount]] interest free payments over [[billingPeriod]] weeks. Your order will be dispatched as soon as possible."
                ourpay.setDetails(OurpayTemplateText.KEY_MYPAY_DETAILS_MOBILE_APP);

            }
        }

        ourpay.setTermsAndConditionsText(OurpayTemplateText.KEY_OURPAY_TC_TEXT);
    }

    public static boolean isPriceOutOfRange(Ourpay ourpay) {
        return  !(ourpay.getUserAmount() >= ourpay.getMinAmount()
                && ourpay.getUserAmount() <= ourpay.getMaxAmount());

    }

    public static void setOurpayAccordingToPaymentMethod(Ourpay ourpay, PaymentMethod paymentMethod) {
        if (paymentMethod.getPaymentType().equalsIgnoreCase(CARD_PAYPAL)) {
            //"You can pay less with {0} now, but we don't support your current payment method for it. If you choose another payment method you could use it."
            ourpay.setState(ourpay.getState() | OurpayState.ERROR);
            ourpay.setDetails(OurpayTemplateText.KEY_CHECKOUT_MYPAY_PAY_INVALID_PAYMENT_METHOD);
        }
    }
}
