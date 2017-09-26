package au.com.dealsdirect.data.network;


import au.com.dealsdirect.BuildConfig;

final class ApiEndPoint {

    private static final String LEGACY_API_VERSION = "v3.17/";
    private static final String NO_AKAMAI_EXTENSION = "api.asmx/";
    private static final String AKAMAI_EXTENSION = "api.ashx/";

    //private static final String TEST_API_LEGACY = "https://sbx-mob-api.mysaledev.com/public/" + LEGACY_API_VERSION + NO_AKAMAI_EXTENSION;
    private static final String TEST_API_LEGACY = "https://api.mysaledev.com/public/" + LEGACY_API_VERSION + NO_AKAMAI_EXTENSION;
    private static final String LIVE_API_LEGACY = "https://www.dealsdirect.com.au/papi/public/" + LEGACY_API_VERSION + NO_AKAMAI_EXTENSION;

    private static final String TEST_API_GENIE = "https://genie-ui-dealsdirect-pre.mysaledev.com/";
    private static final String LIVE_API_GENIE = "https://www.dealsdirect.com.au/";

    private static final String TEST_API_LEGACY_AKAMAI = "https://api.mysaledev.com/public/" + LEGACY_API_VERSION + AKAMAI_EXTENSION;
    private static final String LIVE_API_LEGACY_AKAMAI = "https://www.dealsdirect.com.au/papi/public/" + LEGACY_API_VERSION + AKAMAI_EXTENSION;

    private static final String CATEGORY_TREE = "api/shop/shop/v1/accounts/A56B0D62-AEF6-4653-848E-2ECDDB6E9B8C/categorytree/";
    private static final String SORTING = "api/shop/shop/v1/accounts/A56B0D62-AEF6-4653-848E-2ECDDB6E9B8C/sorting/";
    private static final String SALES = "api/sale/sale/v1/accounts/A56B0D62-AEF6-4653-848E-2ECDDB6E9B8C/banners/";
    private static final String PRODUCTS = "api/shop/shop/v1/accounts/A56B0D62-AEF6-4653-848E-2ECDDB6E9B8C/products";
    private static final String PRODUCT_DETAILS = "api/shop/product/v1/accounts/A56B0D62-AEF6-4653-848E-2ECDDB6E9B8C/products/{seo_identifier}";
    private static final String ADDTOCART = "api/shop/product/v1/accounts/A56B0D62-AEF6-4653-848E-2ECDDB6E9B8C/basket/items";
    private static final String BASKET_QUANTITY = "api/shop/product/v1/accounts/A56B0D62-AEF6-4653-848E-2ECDDB6E9B8C/basket/items/quantity";
    private static final String EVENTS = "api/shop/eventing/v1/events";
    private static final String USERS = "api/shop/eventing/v1/users/current";


    static final String GET_CATEGORY_TREE = getBaseApiGenie() + CATEGORY_TREE;
    static final String GET_SORTING = getBaseApiGenie() + SORTING;
    static final String GET_SALES = getBaseApiGenie() + SALES;
    static final String GET_PRODUCTS = getBaseApiGenie() + PRODUCTS;
    static final String GET_PRODUCT_DETAILS = getBaseApiGenie() + PRODUCT_DETAILS;
    static final String GET_BASKET_QUANTITY = getBaseApiGenie() + BASKET_QUANTITY;
    static final String SEARCH_EVENT = getBaseApiGenie() + EVENTS;
    static final String GET_EVENT_USER = getBaseApiGenie() + USERS;

    /* API Constants */
    private static final String API_HOST = "https://www.dealsdirect.com.au/";

    private static final String API_VERSION = "api/v1/";

    private static final String HANDLER_PREFIX = "handler.ashx/";
    private static final String HANDLER_ASMX_PREFIX = "handler.ashx/";
    private static final String BASE_URL = API_HOST + HANDLER_PREFIX;
    private static final String BASE_URL_ASMX = API_HOST + HANDLER_ASMX_PREFIX;

    /* OURPAY, SMS VERIFICATION */
    static final String SMS_VERIFICATION_NORMALIZE_PHONE = getBaseApiLegacy()+ "NormalizePhone";
    static final String SMS_VERIFICATION_CODE_SEND = getBaseApiLegacy() + "VerificationCodeSend";
    static final String SMS_VERIFICATION_CODE_CONFRIM = getBaseApiLegacy() + "VerificationCodeConfirm";

    /*GCM CALLS*/
    static final String GCM_REGISTER_DEVICE = getBaseApiLegacy() + "RegisterDevice";
    static final String GCM_NOTIFICATION_EVENT = getBaseApiLegacyAkamai() + "NotificationEvent";
    static final String GCM_REGISTER_SUBSCRIBER = getBaseApiLegacyAkamai() + "RegisterSubscriber";


    /*CONFIG CALLS*/
    static final String GET_SERVER_SETTINGS = getBaseApiLegacy() + "GetServerSettings";
    static final String GET_PUBLIC_APP_SETTINGS = getBaseApiLegacy() + "GetPublicAppSettings";
    static final String GET_APP_SETTINGS = getBaseApiLegacy() + "GetAppSettings";
    static final String GET_APP_SETTINGS_SECTION = getBaseApiLegacy() + "GetAppSettingsSection";

    static final String GET_USER_LANGUAGES = getBaseApiLegacy() + "GetUserLanguages";
    static final String SET_USER_LANGUAGES = getBaseApiLegacy() + "SetUserLanguage";

    /* Sale Items Controller */
    static final String GET_PUBLIC_SALE_ITEMS = BASE_URL + "GetPublicSaleItems";

    /* Categories Controller */
    static final String GET_SHOP_CATEGORIES = BASE_URL + "GetPublicSalesCategories";


    /* Sale Detail Controller */
    static final String GET_SALES_ITEM_SALE_DETAILS = BASE_URL + "GetPublicSaleDetails";
    static final String GET_SALES_ITEM_DETAILS = BASE_URL + "GetPublicItemDetails";
    static final String ADD_TO_CART = getBaseApiGenie() + ADDTOCART;


    /* Voucher Controller */
    static final String ADD_VOUCHER_BY_KEY = getBaseApiLegacy() + "AddVoucherByKey";
    static final String ADD_AND_APPLY_VOUCHER = getBaseApiLegacy() + "AddAndApplyVoucherByKey";
    static final String GET_USER_VOUCHERS = getBaseApiLegacy() + "GetUserVouchers";
    static final String GET_VOUCHERS = getBaseApiLegacy() + "GetVouchers";

    /* Invite Controller */
    static final String SET_INVITE = getBaseApiLegacy() + "SetInviteLink";
    static final String GET_INVITE = getBaseApiLegacy() + "GetInviteLink";


    /* Contact Controller */
    static final String ANSWER_CONTACT = getBaseApiLegacy() + "AnswerContact";
    static final String CREATE_CONTACT = getBaseApiLegacy() + "CreateContact";
    static final String GET_CONTACT_INVOICES = getBaseApiLegacy() + "GetContactInvoices";
    static final String GET_CONTACT = getBaseApiLegacy() + "GetContact";
    static final String GET_CONTACTS = getBaseApiLegacy() + "GetContacts";
    static final String GET_CONTACT_SUBJECTS = getBaseApiLegacy() + "GetContactSubjects";

    /* Return Controller */
    static String GET_RETURNS = getBaseApiLegacy() + "GetReturns";
    static String GET_RETURN_ORDERS = getBaseApiLegacy() + "GetReturnOrders";
    static String GET_RETURN_ORDER_DETAIL = getBaseApiLegacy() + "GetReturnOrderDetail";
    static String GET_RETURN_DETAILS = getBaseApiLegacy() + "GetReturnDetails";
    static String CREATE_RETURN = getBaseApiLegacy() + "CreateReturn";


    /* Address Controller */
    static final String DELETE_USER_DELIVERY_ADDRESS = getBaseApiLegacy() + "DeleteUserDeliveryAddress";
    static final String GET_USER_ADDRESSES = getBaseApiLegacy() + "GetUserAddresses";
    static final String SET_USER_DELIVERY_ADDRESS = getBaseApiLegacy() + "SetUserDeliveryAddress";
    static final String APPLY_DELIVERY_ADDRESS = getBaseApiLegacy() + "ApplyDeliveryAddress";

    /* Orders Controller*/
    static final String GET_PAYMENTS_LIST = getBaseApiLegacy() + "GetPaymentsList";
    static final String GET_ORDER_PAYMENT_DETAILS = getBaseApiLegacy() + "GetOrderPaymentDetails";


    /* Checkout Endpoints*/

    static final String GET_CURRENT_ORDER = getBaseApiLegacy() + "GetCurrentOrder";
    static final String GET_USER_PAYMENT_METHODS = getBaseApiLegacy() + "GetUserPaymentMethods";
    static final String CREATE_PAYMENT_METHOD = getBaseApiLegacy() + "CreatePaymentMethod";
    static final String GET_PAYMENT_TOKEN = getBaseApiLegacy() + "GetPaymentToken";
    static final String DECREASE_ORDER_ITEM = getBaseApiLegacy() + "DecreaseOrderItem";
    static final String INCREASE_ORDER_ITEM = getBaseApiLegacy() + "IncreaseOrderItem";
    static final String APPLY_VOUCHERS = getBaseApiLegacy() + "ApplyVouchers";
    static final String CLEAR_VOUCHERS = getBaseApiLegacy() + "ClearVouchers";
    static final String CLEAR_ORDER = getBaseApiLegacy() + "ClearOrder";
    static final String CREATE_PAYMENT_TRANSACTION = getBaseApiLegacy() + "CreatePaymentTransaction";
    static final String REMOVE_USER_PAYMENT_METHOD = getBaseApiLegacy() + "RemoveUserPaymentMethod";

    /* Legalities Endpoint*/
    static final String GET_LEGALITIES_TEXT = getBaseApiLegacy() + "GetTemplateText";
    static final String GET_TEMPLATE_TEXTS = getBaseApiLegacy() + "GetTemplateTexts";



    /* Login Controller */
    static String FORGOT_PASSWORD = getBaseApiLegacy() + "ForgotPassword";
    static String LOGIN_EMAIL = getBaseApiLegacy() + "Login";
    static String LOGIN_FB = getBaseApiLegacy() + "LoginFacebook";
    static String LOGIN_TICKET = getBaseApiLegacy() + "LoginTicket";
    static String LOGOUT = getBaseApiLegacy() + "Logout";
    static String REGISTRATION = getBaseApiLegacy() + "Registration";
    static String SAVE_USER_DETAILS = getBaseApiLegacy() + "SetUserDetails";
    static String LOAD_USER_DETAILS = getBaseApiLegacy() + "GetUserDetails";

    /* Masterpass */
    public static final String MASTERPASS_PAYMENT = getBaseApiLegacy() + "MasterPassPayment";
    public static final String MASTERPASS_POST_TRANSACTION = getBaseApiLegacy() + "MasterPassPostTransaction";

    /* 3DS */
    public static final String GET_PAYMENT_METHOD_NONCE = getBaseApiLegacy() + "GetPaymentMethodNonce";


    private ApiEndPoint() {
         // This class is not publicly instantiable
    }

    private static String getBaseApiLegacy() {
        if (BuildConfig.FLAVOR.contains("Test")) {
            return TEST_API_LEGACY;
        } else {
            return LIVE_API_LEGACY;
        }
    }

    private static String getBaseApiGenie() {
        if (BuildConfig.FLAVOR.contains("Test")) {
            return TEST_API_GENIE;
        } else {
            return LIVE_API_GENIE;
        }
    }

    private static String getBaseApiLegacyAkamai() {
        if (BuildConfig.FLAVOR.contains("Test")) {
            return TEST_API_LEGACY_AKAMAI;
        } else {
            return LIVE_API_LEGACY_AKAMAI;
        }
    }

}
