package au.com.dealsdirect.service.datacollection.core;


import java.util.HashMap;

import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.datacollection.registerservices.FacebookEventService;
import au.com.dealsdirect.service.datacollection.registerservices.FirebaseAnalyticsService;
import au.com.dealsdirect.service.datacollection.registerservices.GenieEventService;


/**
 * Created by MTC on 2/19/19.
 */

public class DataCollector {

    static HashMap<String, DataCollectionService> services = new HashMap<>();

     static {
        DataCollector.registerService(FacebookEventService.getServiceKey(), FacebookEventService.getInstance());
        DataCollector.registerService(GenieEventService.getServiceKey(), GenieEventService.getInstance());
        DataCollector.registerService(FirebaseAnalyticsService.getServiceKey(), FirebaseAnalyticsService.getInstance());
    }

    public static class EventParameters {
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
        public static final String WISHLIST_EVENT_REQUEST = "WISHLIST_EVENT_REQUEST";
        public static final String START_CHECKOUT_REQUEST = "START_CHECKOUT_REQUEST";
        public static final String COMMON_CHECKOUT_REQUEST = "COMMON_CHECKOUT_REQUEST";
        public static final String YOU_MAY_ALSO_LIKE_REQUEST = "YOU_MAY_ALSO_LIKE_REQUEST";
        public static final String RECENTLY_VIEWED_REQUEST = "RECENTLY_VIEWED_REQUEST";
        public static final String RECOMMENDATION_EVENT_REQUEST = "RECOMMENDATION_EVENT_REQUEST";
        public static final String BANNER_CLICK_REQUEST = "BANNER_CLICK_REQUEST";
        public static final String FEATURE_EVENT_REQUEST = "FEATURE_EVENT_REQUEST";
        public static final String APP_CONTEXT = "APP_CONTEXT";
        public static final String ITEM_LIST = "ITEM_LIST";
        public static final String SCREEN_NAME = "SCREEN_NAME";
        public static final String ITEM_CATEGORY = "ITEM_CATEGORY";
        public static final String SEARCH_TERM = "SEARCH_TERM";
        public static final String QUANTITY = "QUANTITY";
        public static final String DELIVERY_PRICE_VIEW_EVENT_REQUEST = "DELIVERY_PRICE_VIEW_EVENT_REQUEST";
        public static final String SELLER_LINK_EVENT_REQUEST = "SELLER_LINK_EVENT_REQUEST";
        // START CHECKOUT
        public static final String START_CHECKOUT_VALUE = "kFIRParameterValue";
        public static final String START_CHECKOUT_CURRENCY = "kFIRParameterCurrency";
        //ADD TO CART
        public static final String ADD_TO_CART_ITEM_ID = "kFIRParameterItemID";
        public static final String ADD_TO_CART_ITEM_NAME = "kFIRParameterItemName";
        public static final String ADD_TO_CART_ITEM_CATEGORY = "kFIRParameterItemCategory";
        public static final String ADD_TO_CART_QUANTITY = "kFIRParameterQuantity";
        public static final String ADD_TO_CART_VALUE = "kFIRParameterValue";
        public static final String ADD_TO_CART_CURRENCY = "kFIRParameterCurrency";
        public static final String ADD_TO_CART_SOURCE = "kFIRParameterSource";
        public static final String ADD_TO_CART_ATTEMPTS = "Attempts";
        //APP LAUNCH
        public static final String LOAD_TIME = "LoadTime";
        //ITEM LIST
        public static final String ITEM_LIST_CATEGORY = "kFIRParameterItemCategory";
        // ITEM DETAILS
        public static final String ITEM_DETAILS_ITEM_ID = "kFIRParameterItemID";
        public static final String ITEM_DETAILS_ITEM_NAME = "kFIRParameterItemName";
        public static final String ITEM_DETAILS_PRICE = "kFIRParameterPrice";
        public static final String ITEM_DETAILS_SOURCE = "kFIRParameterSource";
        // PURCHASE
        public static final String PURCHASE_TRANSACTION_ID = "kFIRParameterTransactionID";
        public static final String PURCHASE_VALUE = "kFIRParameterValue";
        public static final String PURCHASE_CURRENCY = "kFIRParameterCurrency";
        public static final String PURCHASE_CHECKOUT_OPTION = "kFIRParameterCheckoutOption";
        public static final String PURCHASE_NEW_USER = "NewUser";
        public static final String PURCHASE_CC_SCAN = "CCScan";
        // SIGN UP
        public static final String SIGN_UP_METHOD = "kFIRParameterSignUpMethod";
        public static final String SIGN_UP_GAVE_UP = "GaveUpRegistration";
        // LOGIN
        public static final String LOGIN_METHOD = "kFIRParameterMethod";
        public static final String LOGIN_GAVE_UP = "GaveUpLogin";
        // ORDER TRACK
        public static final String ORDER_TRACK_SOURCE = "kFIRParameterSource";
        // SHARE
        public static final String SHARE_SOURCE = "kFIRParameterSource";
        public static final String SHARE_CONTENT_TYPE = "kFIRParameterContentType";
        public static final String SHARE_SUCCESS = "kFIRParameterSuccess";
        // FAILED TRANSACTION
        public static final String FAILED_TRANSACTION_OPTION = "PaymentOption";
        public static final String FAILED_TRANSACTION_MESSAGE = "Message";
        // TOGGLE COLUMN LIST
        public static final String TOGGLE_LIST_PORTRAIT = "PortraitNumberOfColumns";
        public static final String TOGGLE_LIST_LANDSCAPE = "LandscapeNumberOfColumns";
        public static final String TOGGLE_LIST_PREFERENCE = "ProductListGridViewPreference";
        // BANNERS
        public static final String SALE_NAME = "SaleName";
        public static final String BANNER_TYPE = "BANNER_TYPE";

        public final class LoginType {
            public final static String FACEBOOK = "LoginFacebook";
            public final static String LOGIN = "Login";
            public final static String TICKET = "LoginTicket";
            public final static String SUCCESSFUL_LOGIN = "SuccessfulLogin";
            public final static String GAVE_UP_LOGIN = "GaveUpLogin";
            public final static String FORGOT_PASSWORD = "ForgotPassword";
            public final static String NO_ACTION = "NoAction";
        }

        public final class ClickType {
            public final static String PHONE = "Phone";
            public final static String TABLET = "Tablet";
            public final static String BANNER_CLICK = "BannerClick";
            public final static String PRODUCT_CLICK = "ProductClick";
            public final static String ORDER_TRACK = "OrderTrack";
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

        public enum Operation {
            REGULAR(8),
            OURPAY(10),
            MASTERPASS(11),
            VCO(12),
            PAYPALCREDIT(9),
            PAYPAL(9),
            AFTERPAY(6),
            LPAY(7),
            STRIPE(501),
            GPAY(560),
            UNKNOWN(8);

            private int value;

            Operation(int value) {
                this.value = value;
            }

            public int getValue() {
                return value;
            }
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

        public final class CartJourneyType {
            public final static String VIEW_SALE = "ViewSale";
            public final static String VIEW_PRODUCT_CATEGORY = "ViewProductCategory";
            public final static String VIEW_PRODUCT = "ViewProduct";
            public final static String ADD_TO_CART = "AddToCart";
            public final static String VIEW_CART = "ViewCart";
        }

        public final class InviteType {
            public final static String FACEBOOK = "Facebook";
            public final static String TWITTER = "Twitter";
            public final static String SMS = "SMS";
            public final static String EMAIL = "Email";
            public final static String CANCEL = "Cancel";
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

        public final class RegisterMethod {
            public final static String FACEBOOK = "LoginFacebook";
            public final static String REGISTRATION = "Registration";
            public final static String VCO = "LoginVisa";
            final static String SUCCESSFUL_REGISTRATION = "SuccessfulRegistration";
            final static String GAVE_UP_REGISTRATION = "GaveUpRegistration";
            public final static String NO_ACTION = "NoAction";
        }
    }

    static void registerService(String key, DataCollectionService newService) {
        if (!services.containsKey(key)) {
            services.put(key, newService);
        }
    }

    public static void logEvent(Events eventKey, HashMap<String, Object> parameters) {
        for (String key : services.keySet()) {
            DataCollectionService service = services.get(key);
            if (service.hasEvent(eventKey.toString())) {
                service.logEvent(eventKey.toString(), parameters);
            }
        }
    }

    public static class EventRegistry {

        private static HashMap<String, LoggingService.LoggingEventData> registeredEvents = new HashMap<>();

        public static void register(String eventKey, Events events, LoggingService.LoggingEventData registry) {
            registeredEvents.put(eventKey, registry);
        }

        public static boolean hasEvent(String eventKey) {
            return registeredEvents.containsKey(eventKey);
        }

        public static void logData(String eventKey, HashMap<String, Object> parameters) {
            registeredEvents.get(eventKey).logEventData(parameters);
        }

    }

}
