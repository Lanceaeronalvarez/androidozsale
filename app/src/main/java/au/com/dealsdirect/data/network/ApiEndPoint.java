package au.com.dealsdirect.data.network;


import au.com.dealsdirect.BuildConfig;

public final class ApiEndPoint {

    public static final int TEST_API = 0;
    public static final int LIVE_API = 1;

    /* API Constants */
    private static final String API_HOST = "https://www.dealsdirect.com.au/";
    private static final String TEST_API_LEGACY = "http://api.mysaledev.com/Public/V3.18/api.asmx/";
    private static final String LIVE_API_LEGACY = "";
    private static final String API_VERSION = "api/v1/";

    private static final String HANDLER_PREFIX = "handler.ashx/";
    private static final String HANDLER_ASMX_PREFIX = "handler.ashx/";
    private static final String BASE_URL = API_HOST + HANDLER_PREFIX;
    private static final String BASE_URL_ASMX = API_HOST + HANDLER_ASMX_PREFIX;
    private static final String COCOSA_SERVICE = "CocosaService.asmx/";

    public static final String SAMPLE_API = "";

    /*CONFIG CALLS*/
    public static final String GET_SERVER_SETTING = BASE_URL_ASMX + "GetServerSettings";
    public static final String GET_PUBLIC_APP_SETTINGS = BASE_URL_ASMX + "GetPublicAppSettings";
    public static final String GET_APP_SETTINGS = BASE_URL_ASMX + "GetAppSettings";
    public static final String GET_APP_SETTINGS_SECTION = BASE_URL_ASMX + "GetAppSettingsSection";

    public static final String GET_SERVER_SETTING_TEST = TEST_API_LEGACY + "GetServerSettings";
    public static final String GET_PUBLIC_APP_SETTINGS_TEST = TEST_API_LEGACY + "GetPublicAppSettings";
    public static final String GET_APP_SETTINGS_TEST = TEST_API_LEGACY + "GetAppSettings";
    public static final String GET_APP_SETTINGS_SECTION_TEST = TEST_API_LEGACY + "GetAppSettingsSection";

    /* Shops Controller */
    public static final String GET_SHOP_BANNERS = BASE_URL + "GetPublicSalesBanners";
    public static final String GET_SALES_CATEGORIES = BASE_URL + "GetSaleCategories";

    /* Sale Items Controller */
    public static final String GET_PUBLIC_SALE_ITEMS = BASE_URL+"GetPublicSaleItems";

    /* Categories Controller */
    public static final String GET_SHOP_CATEGORIES = BASE_URL + "GetPublicSalesCategories";

    /* Sales Controller */
    public static final String GET_SALE_ITEMS = BASE_URL + "GetSaleItems";
    public static final String GET_SALE_DETAILS = BASE_URL + "GetSaleDetails";
    public static final String GET_sALE_CATEGORIES = BASE_URL+ "GetSaleCategories";


    /* Sale Detail Controller */
    public static final String GET_SALES_ITEM_SALE_DETAILS = BASE_URL + "GetPublicSaleDetails";
    public static final String GET_SALES_ITEM_DETAILS = BASE_URL + "GetPublicItemDetails";
    public static final String ADD_TO_CART = "AddItemToCart";
    public static final String GET_ITEM_DETAILS = "GetItemDetails";


    /* Voucher Controller */
    public static final String ADD_VOUCHER_BY_KEY = "AddVoucherByKey";
    public static final String APPLY_VOUCHERS = "ApplyVouchers";


    /* Contact Controller */
    public static final String ANSWER_CONTACT = "AnswerContact";
    public static final String CREATE_CONTACT = "CreateContact";
    public static final String GET_CONTACT = "GetContact";
    public static final String GET_CONTACT_INVOICES = "GetContactInvoices";
    public static final String GET_CONTACTS = BASE_URL+ COCOSA_SERVICE + "GetContacts";
    public static final String GET_CONTACT_SUBJECTS = "GetContactSubjects";


    /* Checkout Controller */
    public static final String CLEAR_ORDER = "ClearOrder";
    public static final String CLEAR_VOUCHERS = "ClearVouchers";
    public static final String DECREASE_ORDER_ITEM = "DecreaseOrderItem";
    public static final String GET_CURRENT_ORDER = "GetCurrentOrder";
    public static final String GET_ORDER_DETAILS = "GetOrderDetails";


    /* Return Controller */
    public static String CREATE_RETURN = "CreateReturn";


    /* Address Controller */
    public static final String DELETE_USER_DELIVERY_ADDRESS = "DeleteUserDeliveryAddress";

    /* Login Controller */
    public static String FORGOT_PASSWORD = "ForgotPassword"; //


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

    public static String getBaseUrl(int apiCode){
        switch (apiCode) {
            case TEST_API:
                if (BuildConfig.DEBUG) {
                    return TEST_API_LEGACY;
                } else {
                    return LIVE_API_LEGACY;
                }
            default:
                return API_HOST;
        }

    }
}
