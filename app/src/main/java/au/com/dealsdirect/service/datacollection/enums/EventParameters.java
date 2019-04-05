package au.com.dealsdirect.service.datacollection.enums;

/**
 * Created by MTC on 2/18/19.
 */

public class EventParameters {
    public static final String METHOD = "METHOD";
    public static final String ITEM_ID = "ITEM_ID";
    public static final String ITEM_NAME = "ITEM_NAME";
    public static final String ITEM_BRAND = "ITEM_BRAND";
    public static final String PRICE = "PRICE";
    public static final String COUNTRY_ID = "COUNTRY_ID";
    public static final String PAYMENT_METHOD_TYPE = "PAYMENT_METHOD_TYPE";
    public static final String SIZE = "SIZE";
    public static final String TOTAL = "TOTAL";
    public static final String NUMBER_OF_ITEMS = "NUMBER_OF_ITEMS";
    public static final String SOURCE = "SOURCE";
    public static final String ATTEMPTS = "ATTEMPTS";
    public static final String PAYMENT_OPTION = "PAYMENT_OPTION";
    public static final String IS_NEW_USER = "IS_NEW_USER";
    public static final String RESULT = "RESULT";
    public static final String MILLISECONDS = "MILLISECONDS";
    public static final String EVENT_PROGRESS = "EVENT_PROGRESS";
    public static final String ITEM_ARRAY_POSITION = "ITEM_ARRAY_POSITION";
    public static final String TYPE = "TYPE";
    public static final String SEARCH_EVENT_REQUEST = "SEARCH_EVENT_REQUEST";
    public static final String PRODUCT_VIEW_REQUEST = "PRODUCT_VIEW_REQUEST";
    public static final String CATEGORY_REQUEST = "CATEGORY_REQUEST";
    public static final String SALE_EVENT_REQUEST = "SALE_EVENT_REQUEST";
    public static final String APP_CONTEXT = "APP_CONTEXT";
    public static final String ITEM_LIST = "ITEM_LIST";
    public static final String SCREEN_NAME = "SCREEN_NAME";
    public static final String ITEM_CATEGORY = "ITEM_CATEGORY";
    public static final String SEARCH_TERM = "SEARCH_TERM";
    public static final String QUANTITY = "QUANTITY";

    public final class ClickType {
        final static String PHONE = "Phone";
        final static String TABLET = "Tablet";
        public final static String BANNER_CLICK = "BannerClick";
        public final static String PRODUCT_CLICK = "ProductClick";
        final static String ORDER_TRACK = "OrderTrack";
    }

    public final class LoginType {
        public final static String FACEBOOK = "LoginFacebook";
        public final static String LOGIN = "Login";
        public final static String TICKET = "LoginTicket";
        public final static String SUCCESSFUL_LOGIN = "SuccessfulLogin";
        public final static String GAVE_UP_LOGIN = "GaveUpLogin";
        public final static String FORGOT_PASSWORD = "ForgotPassword";
        public final static String NO_ACTION = "NoAction";
    }

    public final class RegisterMethod {
        public final static String FACEBOOK = "LoginFacebook";
        public final static String REGISTRATION = "Registration";
        public final static String VCO = "LoginVisa";
        final static String SUCCESSFUL_REGISTRATION = "SuccessfulRegistration";
        final static String GAVE_UP_REGISTRATION = "GaveUpRegistration";
        public final static String NO_ACTION = "NoAction";
    }

    public final class ViewSource {
        public final static String SALE = "Sale";
        public final static String CATEGORY = "Category";
        public final static String SEARCH = "Search";
        public final static String ITEM_DETAILS = "ItemDetails";
        public final static String INVITE = "Invite";
        public final static String ORDER_LIST = "OrderList";
        public final static String ORDER_DETAILS = "OrderDetails";
    }

    public final class InviteType {
        public final static String FACEBOOK = "Facebook";
        public final static String TWITTER = "Twitter";
        public final static String SMS = "SMS";
        public final static String EMAIL = "Email";
        public final static String CANCEL = "Cancel";
    }

    public final class CartJourneyType {
        final static String VIEW_SALE = "ViewSale";
        final static String VIEW_PRODUCT_CATEGORY = "ViewProductCategory";
        final static String VIEW_PRODUCT = "ViewProduct";
        final static String ADD_TO_CART = "AddToCart";
        final static String VIEW_CART = "ViewCart";
    }

    public final class LastRedirection {
        public final static String PAY = "Pay";
        public final static String ADD_ADDRESS = "AddAddress";
        public final static String ADD_PAYMENT_METHOD = "AddPaymentMethod";
        public final static String OURPAY_OFFER = "OurpayOffer"; // This is Legacy only
        public final static String OURPAY_PHONE_VERIFIATION = "OurpayPhoneVerification";
        public final static String PAYPAL = "Paypal";
        public final static String VISACHECKOUT = "VisaCheckout";
        public final static String MASTERPASS = "Masterpass";
        public final static String THREEDSECURE_OTP = "3DSOTP";
    }

    public enum PaymentOption {
        VCO("VisaCheckout"),
        PAYPAL("PayPal"),
        MASTERPASS("Masterpass"),
        OURPAY("Ourpay"),
        OURPAY3DS("Ourpay3DS"),
        THREEDS("3DS"),
        REGULAR("Regular");

        private String value;

        PaymentOption(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum EventProgress {
        START("Start"),
        SUCCESS("Success"),
        END("End");

        private String value;

        EventProgress(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum CustomAttributeTypes {
        TYPE("Type"),
        PHONE_TYPE("PhoneType"),
        TABLET_TYPE("TabletType"),
        SOURCE("Source"),
        LOAD_TIME("LoadTime"),
        PAYMENT_OPTION("PaymentOption"),
        ATTEMPTS("Attempts"),
        NEW_USER("NewUser");

        private String value;

        CustomAttributeTypes(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum CustomEventType {
        CC_SCAN("CC_SCAN"),
        CV_APPLAUNCH("CV_APPLAUNCH"),
        CV_SALEBANNERS("CV_SALEBANNERS"),
        CV_ITEMLIST("CV_ITEMLIST"),
        CV_ITEMDETAILS("CV_ITEMDETAILS"),
        CV_ORDERTRACK("CV_ORDERTRACK"),
        CLICKS("CLICKS"),
        ADDTOCART_JOURNEY("ADDTOCART_JOURNEY"),
        CHECKOUT_JOURNEY("CHECKOUT_JOURNEY");

        private String value;

        CustomEventType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
