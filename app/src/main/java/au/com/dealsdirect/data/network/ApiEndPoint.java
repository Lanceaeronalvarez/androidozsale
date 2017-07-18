package au.com.dealsdirect.data.network;


import au.com.dealsdirect.BuildConfig;

public final class ApiEndPoint {

    public static final int TEST_API = 0;
    public static final int LIVE_API = 1;

    private static final String LEGACY_SHOP = "https://www.dealsdirect.com.au/api/shop/shop/";
    private static final String LEGACY_SALE = "https://www.dealsdirect.com.au/api/sale/sale/";
    private static final String LEGACY_PRODUCT = "https://www.dealsdirect.com.au/api/shop/product/";

    private static final String GENIE_PRODUCT = "https://genie-ui-dealsdirect-pre.mysaledev.com/api/shop/product/";
    private static final String GENIE_SHOP = "https://genie-ui-dealsdirect-pre.mysaledev.com/api/shop/shop/";
    private static final String GENIE_SALE = "https://genie-ui-dealsdirect-pre.mysaledev.com/api/sale/sale/";

    private static final String GENIE_TEST_SHOP = "https://genie-ui-dealsdirect-pre.mysaledev.com/api/shop/shop/";
    private static final String GENIE_TEST_SALE = "https://genie-ui-dealsdirect-pre.mysaledev.com/api/sale/sale/";

    private static final String CATEGORY_TREE = "v1/accounts/A56B0D62-AEF6-4653-848E-2ECDDB6E9B8C/categorytree/";
    private static final String SALES = "v1/accounts/A56B0D62-AEF6-4653-848E-2ECDDB6E9B8C/banners/";
    private static final String PRODUCTS = "v1/accounts/A56B0D62-AEF6-4653-848E-2ECDDB6E9B8C/products";
    private static final String PRODUCT_DETAILS = "v1/accounts/A56B0D62-AEF6-4653-848E-2ECDDB6E9B8C/products/{seo_identifier}";

    public static final String GET_CATEGORY_TREE = LEGACY_SHOP + CATEGORY_TREE;
    public static final String GET_SALES = LEGACY_SALE + SALES;
    public static final String GET_PRODUCTS = LEGACY_SHOP + PRODUCTS;
    public static final String GET_PRODUCT_DETAILS = LEGACY_PRODUCT + PRODUCT_DETAILS;

    private static final String COCOSA_SERVICE = "CocosaService.asmx/";
    /* API Constants */
    private static final String API_HOST = "https://www.dealsdirect.com.au/";

    //    private static final String TEST_API_LEGACY = "https://sbx-mob-api.mysaledev.com/public/v3.17/api.asmx/";
    private static final String TEST_API_LEGACY = "https://api.mysaledev.com/public/v3.17/api.asmx/";
    private static final String LIVE_API_LEGACY = "https://api.apacsale.com/public/v3.17/api.asmx/";

    private static final String API_VERSION = "api/v1/";

    private static final String HANDLER_PREFIX = "handler.ashx/";
    private static final String HANDLER_ASMX_PREFIX = "handler.ashx/";
    private static final String BASE_URL = API_HOST + HANDLER_PREFIX;
    private static final String BASE_URL_ASMX = API_HOST + HANDLER_ASMX_PREFIX;


    public static final String SAMPLE_API = "";

    /*CONFIG CALLS*/
    public static final String GET_SERVER_SETTINGS = getBaseApiLegacy() + "GetServerSettings";
    public static final String GET_PUBLIC_APP_SETTINGS = getBaseApiLegacy() + "GetPublicAppSettings";
    public static final String GET_APP_SETTINGS = getBaseApiLegacy() + "GetAppSettings";
    public static final String GET_APP_SETTINGS_SECTION = getBaseApiLegacy() + "GetAppSettingsSection";

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
    public static final String ADD_TO_CART = "AddItemToCart";
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
    //    public static final String GET_CONTACT =  API_HOST + COCOSA_SERVICE +"GetContact";
    //    public static final String GET_CONTACTS = API_HOST + COCOSA_SERVICE + "GetContacts";
    public static final String GET_CONTACT_INVOICES = getBaseApiLegacy() + "GetContactInvoices";
    public static final String GET_CONTACT = getBaseApiLegacy() + "GetContact";
    public static final String GET_CONTACTS = getBaseApiLegacy() + "GetContacts";

    public static final String GET_CONTACT_SUBJECTS = getBaseApiLegacy() + "GetContactSubjects";

    /* Return Controller */
    public static String CREATE_RETURN = "CreateReturn";

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

    /* Legalities Endpoint*/
    public static final String GET_LEGALITIES_TEXT = getBaseApiLegacy() + "GetTemplateText";


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


    private ApiEndPoint() {
//         This class is not publicly instantiable
    }

    public static String getBaseApiLegacy() {
        if (BuildConfig.DEBUG) {
            return TEST_API_LEGACY;
        } else {
            return LIVE_API_LEGACY;
        }
    }
}
