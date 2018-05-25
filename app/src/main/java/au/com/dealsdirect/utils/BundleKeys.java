package au.com.dealsdirect.utils;

/**
 * Created by smartwave on 02/11/2017.
 */

public class BundleKeys {

    //categories
    public static final String CATEGORIES_ITEM_LIST = "CATEGORIES_ITEM_RESPONSE";
    public static final String CATEGORIES_API_CALL_FINISHED = "CATEGORIES_API_CALL_FINISHED";

    //sale items
    public static final String SALEITEMS_SALE_ID = "SaleItemsController.SALEITEMS_SALE_ID";
    public static final String SALEITEMS_BANNER_ID = "SaleItemsController.SALEITEMS_BANNER_ID";
    public static final String SALEITEMS_TITLE = "SaleItemsController.SALEITEMS_TITLE";
    public static final String SALEITEMS_HEADER_IMAGE = "SaleItemsController.header_image_url";
    public static final String SALEITEMS_FROM_POSITION = "SaleItemsController.position";
    public static final String SALEITEMS_CATEGORY_MAP = "SaleItemsController.CATEGORY_SALEITEMS";
    public static final String SALEITEMS_SEARCH_QUERY = "SaleItemsController.SEARCH_SALEITEMS";
    public static final String SALEITEMS_CHIPS_FILTER = "SaleItemsController.CHIPS_FILTER";
    public static final String SALEITEMS_FROM_CATEGORIES = "SaleItemsController.IS_FROM_CATEGORY";
    public static final String SALEITEMS_FROM_SHOP_SEARCH = "SaleItemsController.FROM_SHOP_SEARCH";
    public static final String SALEITEMS_FROM_CATEGORY_SEARCH = "SaleItemsController.FROM_CATEGORY_SEARCH";

    //sale item details
    public static final String SALEITEMDETAILS_KEY_POSITION = "SALEITEMDETAILS_KEY_POSITION";
    public static final String SALEITEMDETAILS_KEY_SKU_ID = "SALEITEMDETAILS_KEY_SKU_ID";
    public static final String SALEITEMDETAILS_KEY_SALE_ID = "SALEITEMDETAILS_KEY_SALE_ID";
    public static final String SALEITEMDETAILS_KEY_ITEM_IMAGE_ID = "SALEITEMDETAILS_KEY_IMAGE_ID";
    public static final String SALEITEMDETAILS_KEY_SEO_IDENTIFIER_ID = "SALEITEMDETAILS_KEY_SEO_IDENTIFIER";
    public static final String SALEITEMDETAILS_KEY_ITEM_NAME = "SALEITEMDETAILS_KEY_SALE_NAME";
    public static final String SALEITEMDETAILS_KEY_ITEM_PRICE = "SALEITEMDETAILS_KEY_SALE_PRICE";
    public static final String SALEITEMDETAILS_KEY_ITEM_OLD_PRICE = "SALEITEMDETAILS_KEY_SALE_OLD_PRICE";
    
    //search filters
    public static final String FACET_FILTER_TYPE = "FACET_FILTER_TYPE";
    public static final String BRANDS_FACET_FILTER_TYPE = "brands";
    public static final String COLOR_FACET_FILTER_TYPE = "color";
    public static final String SIZE_FACET_FILTER_TYPE = "size";
    public static final String PRICE_FACET_FILTER_TYPE = "price";
    public static final String BRANDS_FACETFILTER_NAME = "skus.brandName";
    public static final String SIZES_FACETFILTER_NAME = "skus.attributes.size";
    public static final String COLORS_FACETFILTER_NAME = "color";
    public static final String PRICE_FACETFILTER_NAME = "skus.attributesForFaceting.aud";
    public static final String SEARCH_QUERY_NAME = "search_query";
    public static final String SORT_FACETFILTER_NAME = "sort";
    public static final String CATEGORY_TREE_FACET = "KEY_CATEGORY_FACET";
    public static final String KEY_SELECTED_FACETS = "KEY_SELECTED_FACETS";
    public static final String KEY_BRAND_LIST = "KEY_BRAND_LIST";
    public static final String KEY_ORIG_SELECTED = "KEY_ORIG_SELECTED";
    public static final String KEY_CHIP_TO_REMOVE = "KEY_CHIP_TO_REMOVE";
    public static final String KEY_FACET_STRING = "KEY_FACET_STRING";
    public static final String KEY_CATEGORY_STRING = "KEY_CATEGORY_STRING";
    public static final String KEY_SORTING_STRING = "KEY_SORTING_STRING";
    public static final String KEY_SALE_ITEMS_TITLE = "KEY_SALE_ITEMS_TITLE";


    //facet filters
    public static final String FACET_PAYLOAD = "FACET_PAYLOAD";

    //payment select
    public static final String PAYMENT_METHODS = "payment_methods";
    public static final String IS_FROM_CART = "is_from_cart";
    public static final String CART_TOTAL_COST = "cart_total_cost";
    public static final String ITEM_LIST_SIZE = "item_list_size";

    //payment success
    public static final String KEY_ADDRESS = "Address";
    public static final String KEY_PRICE = "Price";
    public static final String KEY_SHIPPING_FEE = "Shipping";
    public static final String KEY_INVOICE = "Invoice";
    public static final String KEY_INVOICE_NUMBER = "InvoiceNumber";
    public static final String KEY_ESTIMATED_DELIVERY = "EstimatedDelivery";

    //add vouchers
    public static final String VOUCHERS="Vouchers";
    public static final String IS_VOUCHER_ADDED = "IS_VOUCHER_ADDED_KEY";
    public static final String IS_CART_NO_DISCOUNT = "IS_NO_DISCOUNT";

    //legalities
    public static final String TEMPLATE_KEY = "TEMPLATE_KEY";
    public static final String TITLE = "TITLE";

    //Tutorial
    public static final String FROM_MY_ACCOUNTS = "FROM_MY_ACCOUNTS";

    // Address
    public static final String DECORATION_INFO_LIST = "DECORATION_INFO_LIST";
    public static final String DELIVERY_ADDRESS = "ViewAddressController.DELIVERY_ADDRESS";

    // Contact
    public static final String IS_SUBJECT_LOADED = "IS_SUBJECT_LOADED";
    public static final String FROM_FRAGMENT_ID = "FROM_FRAGMENT_ID";
    public static final String CONTACT_SUBJECT = "CONTACT_SUBJECT";
    public static final String CONTACT_INVOICE = "CONTACT_INVOICE";
    public static final String CONTACT_NUMBER = "CONTACT_NUMBER";

    public static final String CONTACT_NAME = "CONTACT_NAME";
    public static final String CONTACT_INVOICE_NUMBER = "CONTACT_INVOICE_NUMBER";
    public static final String CONTACT_TIME_STAMP = "CONTACT_TIME_STAMP";

    //Ourpay SMS Verification
    public static final String PHONE_KEY = "PHONE_KEY";

    //Returns
    public static final String KEY_ORDER_NUMBER = "ReturnDetailsController.KEY_ORDER_NUMBER";
    public static final String KEY_REQUEST_DATE = "ReturnDetailsController.REQUEST_DATE";
    public static final String KEY_IS_APPROVED = "ReturnDetailsController.IS_APPROVED";
    public static final String KEY_STATUS = "ReturnDetailsController.STATUS";
    public static final String KEY_RAN = "ReturnDetailsController.RAN";
    public static final String KEY_RETURN_ID = "ReturnDetailsController.RETURN_ID";

    //Categories
    public static final String CATEGORY_SHOP = "shop";

    //Password Verification
    public static final String KEY_ACCOUNT_EMAIL = "PasswordVerification.ACCOUNT_EMAIL";
    public static final String KEY_ACCOUNT_EXISTS = "PasswordVerification.ACCOUNT_EXISTS";
    public static final String KEY_LOGIN_VISA_REQUEST_DATA = "PasswordVerification.LOGIN_VISA_REQUEST_DATA";


}
