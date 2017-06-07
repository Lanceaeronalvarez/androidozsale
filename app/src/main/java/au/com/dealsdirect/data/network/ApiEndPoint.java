package au.com.dealsdirect.data.network;



public final class ApiEndPoint {

    /* API Constants */
    private static final String API_HOST = "https://www.dealsdirect.com.au/";
    private static final String API_VERSION = "api/v1/";

    private static final String HANDLER_PREFIX = "handler.ashx/";
    private static final String BASE_URL = API_HOST + HANDLER_PREFIX;

    public static final String SAMPLE_API = "";

    /* Shops Controller */
    public static final String GET_SHOP_BANNERS = BASE_URL + "GetPublicSalesBanners";
    public static final String GET_SALES_CATEGORIES = BASE_URL + "GetSaleCategories";


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
    public static final String GET_CONTACTS = "GetContacts";
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
}
