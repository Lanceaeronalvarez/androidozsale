package au.com.dealsdirect.data.network;


import au.com.dealsdirect.BuildConfig;

public final class ApiEndPoint {

    private static final String NO_AKAMAI_EXTENSION = "api.asmx/";
    private static final String AKAMAI_EXTENSION = "api.ashx/";

    private static final String CATEGORY_TREE = "api/shop/shop/v1/accounts/" + BuildConfig.ACCOUNT_ID + "/categorytree/";
    private static final String SORTING = "api/shop/shop/v1/accounts/" + BuildConfig.ACCOUNT_ID + "/sorting/";
    private static final String SALES = "api/sale/sale/v1/accounts/" + BuildConfig.ACCOUNT_ID + "/banners/";
    private static final String PRODUCTS = "api/shop/shop/v1/accounts/" + BuildConfig.ACCOUNT_ID + "/products";
    private static final String PRODUCT_DETAILS = "api/shop/product/v1/accounts/" + BuildConfig.ACCOUNT_ID + "/products/{seo_identifier}";
    private static final String OURPAY_DATA = "api/shop/product/v1/accounts/" + BuildConfig.ACCOUNT_ID + "/ourpaydata";
    private static final String ADDTOCART = "api/shop/product/v1/accounts/" + BuildConfig.ACCOUNT_ID + "/basket/items";
    private static final String BASKET_QUANTITY = "api/shop/product/v1/accounts/" + BuildConfig.ACCOUNT_ID + "/basket/items/quantity";
    private static final String EVENTS = "api/shop/eventing/v1/events";
    private static final String USERS = "api/shop/eventing/v1/users/current";

    /* Deep Link Data */
    public static final String DEEP_LINK = BuildConfig.API_ROOT + "api/shop/settings/v1/deeplinkdata";


    public static final String GET_CATEGORY_TREE = getBaseApiGenie() + CATEGORY_TREE;
    public static final String GET_SORTING = getBaseApiGenie() + SORTING;
    public static final String GET_SALES = getBaseApiGenie() + SALES;
    public static final String GET_PRODUCTS = getBaseApiGenie() + PRODUCTS;
    public static final String GET_PRODUCT_DETAILS = getBaseApiGenie() + PRODUCT_DETAILS;
    public static final String GET_OURPAY_DATA = getBaseApiGenie() + OURPAY_DATA;
    public static final String GET_BASKET_QUANTITY = getBaseApiGenie() + BASKET_QUANTITY;
    public static final String SEARCH_EVENT = getBaseApiGenie() + EVENTS;
    public static final String GET_EVENT_USER = getBaseApiGenie() + USERS;

    public static final String API_LEGACY_AKAMAI = "https://www.dealsdirect.com.au/papi/public/v3.17/api.ashx/";

    private static final String COCOSA_SERVICE = "CocosaService.asmx/";
    /* API Constants */
    private static final String API_VERSION = "api/v1/";

    private static final String HANDLER_PREFIX = "handler.ashx/";
    private static final String HANDLER_ASMX_PREFIX = "handler.ashx/";
    private static final String BASE_URL = getBaseApiGenie() + HANDLER_PREFIX;
    private static final String BASE_URL_ASMX = getBaseApiGenie() + HANDLER_ASMX_PREFIX;

    /*SMS VERIFICATION*/
    public static final String SMS_VERIFICATION_NORMALIZE_PHONE = getBaseApiLegacy()+ "NormalizePhone";
    public static final String SMS_VERIFICATION_CODE_SEND = getBaseApiLegacy() + "VerificationCodeSend";
    public static final String SMS_VERIFICATION_CODE_CONFRIM = getBaseApiLegacy() + "VerificationCodeConfirm";

    /*GCM CALLS*/
    public static final String GCM_REGISTER_DEVICE = getBaseApiLegacy() + "RegisterDevice";
    public static final String GCM_NOTIFICATION_EVENT = getBaseApiLegacyAkamai() + "NotificationEvent";
    public static final String GCM_REGISTER_SUBSCRIBER = getBaseApiLegacyAkamai() + "RegisterSubscriber";


    /*CONFIG CALLS*/
    public static final String GET_SERVER_SETTINGS = getBaseApiLegacy() + "GetServerSettings";
    public static final String GET_PUBLIC_APP_SETTINGS = getBaseApiLegacy() + "GetPublicAppSettings";
    public static final String GET_APP_SETTINGS = getBaseApiLegacy() + "GetAppSettings";
    public static final String GET_APP_SETTINGS_SECTION = getBaseApiLegacy() + "GetAppSettingsSection";
    public static final String GET_PUBLIC_PAYMENT_TOKEN = getBaseApiLegacy() + "GetPublicPaymentToken";

    public static final String GET_USER_LANGUAGES = getBaseApiLegacy() + "GetUserLanguages";
    public static final String SET_USER_LANGUAGES = getBaseApiLegacy() + "SetUserLanguage";

    /* Shops Controller */
    public static final String GET_SHOP_BANNERS = BASE_URL + "GetPublicSalesBanners";
    public static final String GET_SALES_CATEGORIES = BASE_URL + "GetSaleCategories";


    /* Sale Items Controller */
    public static final String GET_PUBLIC_SALE_ITEMS = BASE_URL + "GetPublicSaleItems";

    /* Categories Controller */
    public static final String GET_SHOP_CATEGORIES = BASE_URL + "GetPublicSalesCategories";

    /* Sales Controller */
    public static final String GET_SALE_ITEMS = BASE_URL + "GetSaleItems";
    public static final String GET_SALE_DETAILS = BASE_URL + "GetSaleDetails";
    public static final String GET_sALE_CATEGORIES = BASE_URL + "GetSaleCategories";


    /* Sale Detail Controller */
    public static final String GET_SALES_ITEM_SALE_DETAILS = BASE_URL + "GetPublicSaleDetails";
    public static final String GET_SALES_ITEM_DETAILS = BASE_URL + "GetPublicItemDetails";
    public static final String ADD_TO_CART = getBaseApiGenie() + ADDTOCART;
    public static final String GET_ITEM_DETAILS = "GetItemDetails";


    /* Voucher Controller */
    public static final String ADD_VOUCHER_BY_KEY = getBaseApiLegacy() + "AddVoucherByKey";
    public static final String ADD_AND_APPLY_VOUCHER = getBaseApiLegacy() + "AddAndApplyVoucherByKey";
    public static final String GET_USER_VOUCHERS = getBaseApiLegacy() + "GetUserVouchers";
    public static final String GET_VOUCHERS = getBaseApiLegacy() + "GetVouchers";

    /* Invite Controller */
    public static final String SET_INVITE = getBaseApiLegacy() + "SetInviteLink";
    public static final String GET_INVITE = getBaseApiLegacy() + "GetInviteLink";


    /* Contact Controller */
    public static final String ANSWER_CONTACT = getBaseApiLegacy() + "AnswerContact";
    public static final String CREATE_CONTACT = getBaseApiLegacy() + "CreateContact";
    public static final String GET_CONTACT_INVOICES = getBaseApiLegacy() + "GetContactInvoices";
    public static final String GET_CONTACT = getBaseApiLegacy() + "GetContact";
    public static final String GET_CONTACTS = getBaseApiLegacy() + "GetContacts";
    public static final String GET_CONTACT_SUBJECTS = getBaseApiLegacy() + "GetContactSubjects";

    /* Return Controller */
    public static String GET_RETURNS = getBaseApiLegacy() + "GetReturns";
    public static String GET_RETURN_ORDERS = getBaseApiLegacy() + "GetReturnOrders";
    public static String GET_RETURN_ORDER_DETAIL = getBaseApiLegacy() + "GetReturnOrderDetail";
    public static String GET_RETURN_DETAILS = getBaseApiLegacy() + "GetReturnDetails";
    public static String CREATE_RETURN = getBaseApiLegacy() + "CreateReturn";


    /* Address Controller */
    public static final String DELETE_USER_DELIVERY_ADDRESS = getBaseApiLegacy() + "DeleteUserDeliveryAddress";
    public static final String GET_USER_ADDRESSES = getBaseApiLegacy() + "GetUserAddresses";
    public static final String SET_USER_DELIVERY_ADDRESS = getBaseApiLegacy() + "SetUserDeliveryAddress";
    public static final String APPLY_DELIVERY_ADDRESS = getBaseApiLegacy() + "ApplyDeliveryAddress";

    /* Orders Controller*/
    public static final String GET_PAYMENTS_LIST = getBaseApiLegacy() + "GetPaymentsList";
    public static final String GET_ORDER_PAYMENT_DETAILS = getBaseApiLegacy() + "GetOrderPaymentDetails";


    /* Checkout Endpoints*/

    public static final String GET_CURRENT_ORDER = getBaseApiLegacy() + "GetCurrentOrder";
    public static final String GET_USER_PAYMENT_METHODS = getBaseApiLegacy() + "GetUserPaymentMethods";
    public static final String CREATE_PAYMENT_METHOD = getBaseApiLegacy() + "CreatePaymentMethod";
    public static final String GET_PAYMENT_TOKEN = getBaseApiLegacy() + "GetPaymentToken";
    public static final String DECREASE_ORDER_ITEM = getBaseApiLegacy() + "DecreaseOrderItem";
    public static final String INCREASE_ORDER_ITEM = getBaseApiLegacy() + "IncreaseOrderItem";
    public static final String APPLY_VOUCHERS = getBaseApiLegacy() + "ApplyVouchers";
    public static final String CLEAR_VOUCHERS = getBaseApiLegacy() + "ClearVouchers";
    public static final String CLEAR_ORDER = getBaseApiLegacy() + "ClearOrder";
    public static final String CREATE_PAYMENT_TRANSACTION = getBaseApiLegacy() + "CreatePaymentTransaction";
    public static final String REMOVE_USER_PAYMENT_METHOD = getBaseApiLegacy() + "RemoveUserPaymentMethod";
    public static final String GET_DELIVERY_SERVICE_PACKAGE_DETAILS = getBaseApiLegacy() + "GetDeliveryServicePackageDetails";
    public static final String SET_DELIVERY_OPTION = getBaseApiLegacy() + "SetDeliveryOption";

    /* Legalities Endpoint*/
    public static final String GET_LEGALITIES_TEXT = getBaseApiLegacy() + "GetTemplateText";
    public static final String GET_TEMPLATE_TEXTS = getBaseApiLegacy() + "GetTemplateTexts";



    /* Login Controller */
    public static String FORGOT_PASSWORD = getBaseApiLegacy() + "ForgotPassword"; //
    public static String LOGIN_EMAIL = getBaseApiLegacy() + "Login";
    public static String LOGIN_FB = getBaseApiLegacy() + "LoginFacebook";
    public static String LOGIN_TICKET = getBaseApiLegacy() + "LoginTicket";
    public static String LOGOUT = getBaseApiLegacy() + "Logout";
    public static String REGISTRATION = getBaseApiLegacy() + "Registration";
    public static String SAVE_USER_DETAILS = getBaseApiLegacy() + "SetUserDetails";
    public static String LOAD_USER_DETAILS = getBaseApiLegacy() + "GetUserDetails";

    /* Summary */
    public static final String GET_SUMMARY_MENU = BASE_URL + "menu";
    public static final String GET_SUMMARY_DASHBOARDS = BASE_URL + "summary/dashboards";
    public static final String GET_SUMMARY_FILTERS = BASE_URL + "summary/{dashboardName}/filters";
    public static final String GET_SUMMARY_MEASURES = BASE_URL + "summary/{dashboardName}/measures";
    public static final String GET_SUMMARY_DATA = BASE_URL + "summary/{dashboardName}/data";
    public static final String GET_SUMMARY_DATA_FILTERS = BASE_URL + "summary/{dashboardName}/data/{period?}{measure}{&filters}";

    /* Overview */
    public static final String GET_OVERVIEW_MENU = BASE_URL + "menu";
    public static final String GET_OVERVIEW_DASHBOARDS = BASE_URL + "revenue/dashboards";
    public static final String GET_OVERVIEW_FILTERS = BASE_URL + "revenue/overview/filters";
    public static final String GET_OVERVIEW_DATA = BASE_URL + "revenue/overview/data";
    public static final String GET_OVERVIEW_DATA_FILTERS = BASE_URL + "revenue/overview/data?{period}{&filters}";

    /* Masterpass */
    public static final String MASTERPASS_PAYMENT = getBaseApiLegacy() + "MasterPassPayment";
    public static final String MASTERPASS_POST_TRANSACTION = getBaseApiLegacy() + "MasterPassPostTransaction";

    /* 3DS */
    public static final String GET_PAYMENT_METHOD_NONCE = getBaseApiLegacy() + "GetPaymentMethodNonce";

    /* Ourpay */
    public static String GET_PAYMENT_PLANS = getBaseApiLegacy() + "GetPaymentPlans";
    public static String GET_SCHEDULED_PLANS = getBaseApiLegacy() + "GetScheduledPayments";
    public static String GET_PAST_PAYMENTS = getBaseApiLegacy() + "GetPastPayments";
    public static String GET_DELIVERY_SERVICE = getBaseApiLegacy() + "GetDeliveryServicePackageByCustomer";

    /*VISA CHECKOUT*/
    public static String VISA_CHECKOUT_LOGIN = getBaseApiLegacy() + "LoginVisa";

    private ApiEndPoint() {
         // This class is not publicly instantiable
    }

    private static String getBaseApiLegacy() {
        return BuildConfig.API_LEGACY + NO_AKAMAI_EXTENSION;
    }

    private static String getBaseApiGenie() {
        return BuildConfig.API_ROOT;
    }

    private static String getBaseApiLegacyAkamai() {
        return BuildConfig.API_LEGACY + AKAMAI_EXTENSION;
    }
}
