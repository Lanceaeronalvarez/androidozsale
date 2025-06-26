package au.com.dealsdirect.ui.main;

import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;

// TODO: Refactor this static container class out
public class PaymentInfo {

    public static final String TYPE_BRAINTREE = "braintree";
    public static final String TYPE_AFTERPAY = "afterpay";
    public static final String TYPE_LPAY = "lpay";
    public static final String VISA_CHECKOUT_CYBERSOURCE = "visacheckoutcybersource";
    public static final String VISA_CHECKOUT_BRAINTREE = "visacheckoutbraintree";
    public static final String TYPE_STRIPE = "stripe";
    public static final String TYPE_GPAY = "stripegooglepay";
    public static final String TYPE_KLARNA = "klarna";
    public static final String TYPE_ZIPPAY = "zippay";

    private static boolean sThreeDSecureRequired = false;
    private static String sPaymentType = "";
    private static String sFabricPaymentType = "";
    private static PaymentMethod sPaymentMethod = null;
    private static String sAuthorization = "";
    private static Double sCartCost = 0d;
    private static boolean sThreeDSecureCalled = false;
    private static boolean sIsTokenFetching = false;
    private static String provider = "";

    public static boolean isThreeDSecureRequired() {
        return sThreeDSecureRequired;
    }

    public static void setThreeDSecureRequired(boolean sThreeDSecureRequired) {
        PaymentInfo.sThreeDSecureRequired = sThreeDSecureRequired;
    }

    public static String getPaymentType() {
        return sPaymentType;
    }

    public static void setPaymentType(String sPaymentType) {
        PaymentInfo.sPaymentType = sPaymentType;
    }

    //    USED FOR PAYMENT TYPE MAPPING FOR FABRIC APP EVENTS
    public static String getFabricPaymentType() {
        return sFabricPaymentType;
    }

    public static void setFabricPaymentType(String sFabricPaymentType) {
        PaymentInfo.sFabricPaymentType = sFabricPaymentType;
    }

    public static PaymentMethod getPaymentMethod() {
        return sPaymentMethod;
    }

    public static void setPaymentMethod(PaymentMethod sPaymentMethod) {
        PaymentInfo.sPaymentMethod = sPaymentMethod;
    }

    public static String getAuthorization() {
        return sAuthorization;
    }

    public static void setAuthorization(String sAuthorization) {
        PaymentInfo.sAuthorization = sAuthorization;
    }

    public static Double getCartCost() {
        return sCartCost;
    }

    public static void setCartCost(Double sCartCost) {
        PaymentInfo.sCartCost = sCartCost;
    }

    public static boolean isThreeDSecureCalled() {
        return sThreeDSecureCalled;
    }

    public static void setThreeDSecureCalled(boolean sThreeDSecureCalled) {
        PaymentInfo.sThreeDSecureCalled = sThreeDSecureCalled;
    }

    public static void resetPaymentInfo() {
        PaymentInfo.sThreeDSecureRequired = false;
        PaymentInfo.sCartCost = 0d;
        PaymentInfo.sThreeDSecureCalled = false;

        //Have own method for clearing
        //PaymentInfo.sPaymentType = "";
        //PaymentInfo.sPaymentMethod = null;
        //PaymentInfo.sAuthorization = "";

    }


    public static boolean isTokenFetching() {
        return sIsTokenFetching;
    }

    public static void setIsTokenFetching(boolean sIsTokenFetching) {
        PaymentInfo.sIsTokenFetching = sIsTokenFetching;
    }

    public static String getProvider() {
        return provider;
    }

    public static void setProvider(String provider) {
        PaymentInfo.provider = provider;
    }
}
