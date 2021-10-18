package au.com.dealsdirect.data.network;


import java.util.HashMap;
import java.util.Set;

import au.com.dealsdirect.ui.controller.main.Settings;

public final class ApiEndPoint {

    private static final String NO_AKAMAI_EXTENSION = "api.asmx/";
    private static final String AKAMAI_EXTENSION = "api.ashx/";
    private static final String ACCOUNT_ID_DELIMETER = "_ACCOUNT_ID_DELIMETER_";
    public static final String API_LEGACY_AKAMAI = "https://www.dealsdirect.com.au/papi/public/v3.17/api.ashx/";
    private static final String COCOSA_SERVICE = "CocosaService.asmx/";
    /* API Constants */
    private static final String API_VERSION = "api/v1/";
    private static final String HANDLER_PREFIX = "handler.ashx/";
    private static final String HANDLER_ASMX_PREFIX = "handler.ashx/";
    public static final String API_VCO_ICON = "https://assets.secure.checkout.visa.com/VCO/images/acc_40x30_wht01.png";
    public static final String LEGACY_API_VERSION = "3.29";
    public static final String LEGACY_API_VERSION_FOR_SETTINGS = "3.30";
    public static final String LEGACY_API_VERSION_FOR_CHECKOUT = "3.30";

    enum ApiService {
        LEGACY,
        GENIE,
        SHOP,
        SALE,
        PRODUCT,
        WISHLIST,
        SETTING,
        EVENTING,
        ATTACHMENTS,
        MYACCOUNT,
        RECENTLY
    }

    enum ApiUrlVersion {
        v1("v1"),
        v2("v2"),
        v3("v3"),
        v4("v4"),
        v5("v5"),
        emptyVersion("");

        private String apiVersion;

        ApiUrlVersion(String apiVersion) {
            this.apiVersion = apiVersion;
        }

        public String apiVersion() {
            return apiVersion;
        }
    }

    private static String getFormattedUrl(ApiService service, String url, String version) {

        String microServiceUrl = "";
        switch (service) {
            case SHOP:
                microServiceUrl = "api/shop/shop/" + version + "/accounts/";
                break;
            case SALE:
                microServiceUrl = "api/sale/sale/" + version + "/accounts/";
                break;
            case PRODUCT:
                microServiceUrl = "api/shop/product/" + version + "/accounts/";
                break;
            case WISHLIST:
                microServiceUrl = "api/shop/shop/" + version + "/wishlist/";
                break;
            case SETTING:
                microServiceUrl = "api/shop/settings/" + version + "/";
                break;
            case EVENTING:
                microServiceUrl = "api/shop/EVENTING/" + version + "/";
                break;
            case RECENTLY:
                microServiceUrl = "api/shop/shop/" + version + "/recently/";
                break;
            case ATTACHMENTS:
                microServiceUrl = "api/shop/files/" + version + "/files/";
                break;
            case LEGACY:
                microServiceUrl = "papi/public/v" + LEGACY_API_VERSION + "/";
                break;
            case GENIE:
                microServiceUrl = HANDLER_PREFIX;
                break;
            case MYACCOUNT:
                microServiceUrl = "api/shop/myaccount/" + version + "/accounts/";
                break;

        }

        Settings.Country selectedCountry = Settings.getSelectedCountry();
        String root = "";
        switch (service) {
            case LEGACY:
                root = selectedCountry.legacyRoot;
                break;
            case MYACCOUNT:
                root = selectedCountry.myAccount;
                break;
            default:
                root = selectedCountry.genieRoot;
        }

        return root + microServiceUrl + processUrlString(url);
    }

    private static String getFormattedUrlForCustomLegacyVersion(String legacyApiVersion, String url) {
        String microServiceUrl = "papi/public/v" + legacyApiVersion + "/";
//        String microServiceUrl = "ApacService.asmx/";
//        return Settings.getSelectedCountry().legacyRoot + microServiceUrl + processUrlString(url);

        return Settings.getSelectedCountry().legacyRoot + microServiceUrl + processUrlString(url);
    }

    private static String processUrlString(String url) {
        String newUrlString = url;
        HashMap<String, String> replaceStrings = new HashMap<>();
        replaceStrings.put(ACCOUNT_ID_DELIMETER, Settings.getSelectedCountry().accountId);

        Set<String> keySet = replaceStrings.keySet();
        for (String key : keySet) {
            String value = replaceStrings.get(key);
            newUrlString = newUrlString.replaceFirst(key, value);
        }
        return newUrlString;
    }

    public static String getCategoryTree() {
        return getFormattedUrl(ApiService.SHOP, ACCOUNT_ID_DELIMETER + "/categorytree/", ApiUrlVersion.v5.apiVersion());
    }

    public static String getTopBrands() {
        return getFormattedUrl(ApiService.SHOP, ACCOUNT_ID_DELIMETER + "/brands/topbrands/", ApiUrlVersion.v5.apiVersion());
    }

    public static String getSales() {
        return getFormattedUrl(ApiService.SALE, ACCOUNT_ID_DELIMETER + "/banners/grouped/", ApiUrlVersion.v3.apiVersion());
    }

    public static String getSales2() {
        return getFormattedUrl(ApiService.SALE, ACCOUNT_ID_DELIMETER + "/banners/groups/", ApiUrlVersion.v3.apiVersion());
    }

    public static String getSaleBannerDetails() {
        return getFormattedUrl(ApiService.SHOP, ACCOUNT_ID_DELIMETER + "/sales/{sale_id}", ApiUrlVersion.v2.apiVersion());
    }

    public static String getSorting() {
        return getFormattedUrl(ApiService.SHOP, ACCOUNT_ID_DELIMETER + "/sorting/", ApiUrlVersion.v2.apiVersion());
    }

    public static String getProducts() {
        return getFormattedUrl(ApiService.SHOP, ACCOUNT_ID_DELIMETER + "/products", ApiUrlVersion.v4.apiVersion());
    }

    public static String getProductDetailsWithoutSales() {
        return getFormattedUrl(ApiService.PRODUCT, ACCOUNT_ID_DELIMETER + "/products/{seo_identifier}", ApiUrlVersion.v2.apiVersion());
    }

    // note: sale_id is from a product list opened from banners
    public static String getProductDetailsWithSales() {
        return getFormattedUrl(ApiService.PRODUCT, ACCOUNT_ID_DELIMETER + "/sales/{sale_id}/products/{seo_identifier}", ApiUrlVersion.v2.apiVersion());
    }

    public static String getProductDetailsWithoutSalesNoSkuVariants() {
        return getFormattedUrl(ApiService.PRODUCT, ACCOUNT_ID_DELIMETER + "/products/{seo_identifier}/details", ApiUrlVersion.v2.apiVersion());
    }

    // note: sale_id is from a product list opened from banners
    public static String getProductDetailsWithSalesNoSkuVariants() {
        return getFormattedUrl(ApiService.PRODUCT, ACCOUNT_ID_DELIMETER + "/sales/{sale_id}/products/{seo_identifier}/details", ApiUrlVersion.v2.apiVersion());
    }

    public static String getProductDetailsFromCategories() {
        return getFormattedUrl(ApiService.PRODUCT, ACCOUNT_ID_DELIMETER + "/products/{seo_identifier}", ApiUrlVersion.v2.apiVersion());
    }

    public static String getProductDetailDynamicDiscount() {
        return getFormattedUrl(ApiService.PRODUCT, ACCOUNT_ID_DELIMETER + "/price/label", ApiUrlVersion.v2.apiVersion());
    }

    public static String getPromoInfo() {
        return getFormattedUrl(ApiService.PRODUCT, ACCOUNT_ID_DELIMETER + "/promo-info", ApiUrlVersion.v3.apiVersion());
    }

    public static String getOurpayData() {
        return getFormattedUrl(ApiService.PRODUCT, ACCOUNT_ID_DELIMETER + "/ourpaydata", ApiUrlVersion.v2.apiVersion());
    }

    public static String getAddToCart() {
        return getFormattedUrl(ApiService.PRODUCT, ACCOUNT_ID_DELIMETER + "/basket/items", ApiUrlVersion.v2.apiVersion());
    }

    public static String getBasketQuantity() {
        return getFormattedUrl(ApiService.PRODUCT, ACCOUNT_ID_DELIMETER + "/basket/items/quantity", ApiUrlVersion.v2.apiVersion());
    }

    public static String getSearchEvent() {
        return getFormattedUrl(ApiService.EVENTING, "events", ApiUrlVersion.v1.apiVersion());
    }

    public static String getEventUser() {
        return getFormattedUrl(ApiService.EVENTING, "users/current", ApiUrlVersion.v1.apiVersion());
    }

    public static String getWishlistEvent() {
        return getFormattedUrl(ApiService.EVENTING, "events", ApiUrlVersion.v1.apiVersion());
    }

    public static String getFeatureUsageEvent() {
        return getFormattedUrl(ApiService.EVENTING, "events", ApiUrlVersion.v1.apiVersion());
    }

    public static String getDeliveryPriceViewEvent() {
        return getFormattedUrl(ApiService.EVENTING, "events", ApiUrlVersion.v1.apiVersion());
    }

    /* Deep Link Data */
    public static String deepLink() {
        return getFormattedUrl(ApiService.SETTING, "deeplinkdata", ApiUrlVersion.v1.apiVersion());
    }

    /* Account Data*/
    public static String accountData() {
        return getFormattedUrl(ApiService.SETTING, "settings/accountdata/" + ACCOUNT_ID_DELIMETER, ApiUrlVersion.v1.apiVersion());
    }

    /*SMS VERIFICATION*/
    public static String getSmsVerificationNormalizePhone() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "NormalizePhone");
    }

    public static String getSmsVerificationCodeSend() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "VerificationCodeSend");
    }

    public static String getSmsVerificationCodeConfirm() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "VerificationCodeConfirm");
    }

    /*GCM CALLS*/
    public static String gcmRegisterDevice() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "RegisterDevice", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String gcmNotificationEvent() {
        return getFormattedUrl(ApiService.LEGACY, AKAMAI_EXTENSION + "NotificationEvent", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String gcmRegisterSubscriber() {
        return getFormattedUrl(ApiService.LEGACY, AKAMAI_EXTENSION + "RegisterSubscriber", ApiUrlVersion.emptyVersion.apiVersion());
    }

    /*CONFIG CALLS*/
    public static String getServerSettings() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_SETTINGS, AKAMAI_EXTENSION + "GetServerSettings");
    }

    public static String getPublicAppSettings() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_SETTINGS, AKAMAI_EXTENSION + "GetPublicAppSettings");
    }

    public static String getAppSettings() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_SETTINGS, AKAMAI_EXTENSION + "GetAppSettings");
    }

    public static String getAppSettingsSection() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_SETTINGS, AKAMAI_EXTENSION + "GetAppSettingsSection");
    }

    public static String getPublicAppSettingsSection() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_SETTINGS, AKAMAI_EXTENSION + "GetPublicAppSettingsSection");
    }

    public static String getConsentData() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_SETTINGS, NO_AKAMAI_EXTENSION + "GetConsentData");
    }

    public static String saveConsentData() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_SETTINGS, NO_AKAMAI_EXTENSION + "SaveConsentData");
    }

    public static String saveReceiveSales() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "SaveReceiveSales", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getPublicPaymentToken() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "GetPublicPaymentToken");
    }

    public static String getUserLanguages() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "GetUserLanguages", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String setUserLanguages() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "SetUserLanguage", ApiUrlVersion.emptyVersion.apiVersion());
    }

    /* Shops Controller */
    public static String getShopBanners() {
        return getFormattedUrl(ApiService.GENIE, "GetPublicSalesBanners", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getSalesCategories() {
        return getFormattedUrl(ApiService.GENIE, "GetSaleCategories", ApiUrlVersion.emptyVersion.apiVersion());
    }

    /* Sale Items Controller */
    public static String getPublicSaleItems() {
        return getFormattedUrl(ApiService.GENIE, "GetPublicSaleItems", ApiUrlVersion.emptyVersion.apiVersion());
    }

    /* Categories Controller */
    public static String getShopCategories() {
        return getFormattedUrl(ApiService.GENIE, "GetPublicSalesCategories", ApiUrlVersion.emptyVersion.apiVersion());
    }

    /* Sales Controller */
    public static String getSaleItems() {
        return getFormattedUrl(ApiService.GENIE, "GetSaleItems", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getSaleDetails() {
        return getFormattedUrl(ApiService.GENIE, "GetSaleDetails", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getSaleCategories() {
        return getFormattedUrl(ApiService.GENIE, "GetSaleCategories", ApiUrlVersion.emptyVersion.apiVersion());
    }

    /* Sale Detail Controller */
    public static String getSalesItemSaleDetails() {
        return getFormattedUrl(ApiService.GENIE, "GetPublicSaleDetails", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getSalesItemDetails() {
        return getFormattedUrl(ApiService.GENIE, "GetPublicItemDetails", ApiUrlVersion.emptyVersion.apiVersion());
    }

    /* Voucher Controller */
    public static String addVoucherByKey() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "AddVoucherByKey");
    }

    public static String addAndApplyVoucher() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "AddAndApplyVoucherByKey", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getUserVouchers() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "GetUserVouchers", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getVouchers() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "GetVouchers", ApiUrlVersion.emptyVersion.apiVersion());
    }

    /* Invite Controller */
    public static String setInvite() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "SetInviteLink", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getInvite() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "GetInviteLink", ApiUrlVersion.emptyVersion.apiVersion());
    }

    /* Contact Controller */
    public static String answerContact() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/threads/number={number}/messages", ApiUrlVersion.v1.apiVersion());
    }

    public static String createContactOld() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "CreateContact", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String createContact() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/threads", ApiUrlVersion.v1.apiVersion());
    }

    public static String createContactPublic() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/threads/public", ApiUrlVersion.v1.apiVersion());
    }

    public static String getContactInvoices() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/threads/invoices", ApiUrlVersion.v1.apiVersion());
    }

    public static String getContact() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/threads/number={number}/messages", ApiUrlVersion.v1.apiVersion());
    }

    public static String getContacts() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/threads", ApiUrlVersion.v1.apiVersion());
    }

    public static String getContactSubjects() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/threads/subjects?public={public}", ApiUrlVersion.v1.apiVersion());
    }

    public static String getContactSubjectsTemplates() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/threads/subjects/{id}/templates", ApiUrlVersion.v1.apiVersion());
    }

    public static String escalateContact() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/threads/number={number}:escalate", ApiUrlVersion.v1.apiVersion());
    }

    /* Return Controller */
    public static String getReturns() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "GetReturns", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getReturnOrders() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "GetReturnOrders", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getReturnOrderDetail() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "GetReturnOrderDetail", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getReturnDetails() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "GetReturnDetails", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String createReturn() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "CreateReturn", ApiUrlVersion.emptyVersion.apiVersion());
    }

    /* Address Controller */
    public static String deleteUserDeliveryAddress() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/addresses/{addressId}", ApiUrlVersion.v1.apiVersion());
    }

    public static String getUserAddresses() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/addresses", ApiUrlVersion.v1.apiVersion());
    }

    public static String setUserDeliveryAddress() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/addresses", ApiUrlVersion.v1.apiVersion());
    }

    public static String applyDeliveryAddress() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "ApplyDeliveryAddress");
    }

    /* Orders Controller*/
    public static String getOrders() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/orders", ApiUrlVersion.v1.apiVersion());
    }

    public static String getOrdersHistory() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/orders?from={from_date}&months={months}", ApiUrlVersion.v1.apiVersion());
    }

    public static String getOrderDetails() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/orders/number={order_number}", ApiUrlVersion.v1.apiVersion());
    }

    public static String getOrderTracking() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/orders/number={order_id}/invoices/number={invoice_number}/tracking", ApiUrlVersion.v1.apiVersion());
    }

    public static String changeDeliveryAddress() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/orders/{order_id}/invoices/number={invoice_number}/addresses/{address_id}", ApiUrlVersion.v1.apiVersion());
    }

    public static String cancelInvoice() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/orders/{order_id}/invoices/number={invoice_number}", ApiUrlVersion.v1.apiVersion());
    }

    public static String cancelInvoiceItem() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/orders/{order_id}/invoices/number={invoice_number}/ordered/{order_item_id}?qty={quantity}", ApiUrlVersion.v1.apiVersion());
    }

    public static String callSetOrderReceived() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/orders/{order_id}/invoices/number={invoice_number}:received", ApiUrlVersion.v1.apiVersion());
    }

    public static String callSetOrderNotReceived() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/orders/{order_id}/invoices/number={invoice_number}:not_received", ApiUrlVersion.v1.apiVersion());
    }

    public static String callGetOrderReceivedSatisfaction() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/orders/{order_id}/invoices/number={invoice_number}:received_satisfaction", ApiUrlVersion.v1.apiVersion());
    }

    /* Checkout Endpoints*/
    public static String getCurrentOrder() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "GetCurrentOrder");
    }

    public static String getUserPaymentMethods() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "GetUserPaymentMethods");
    }

    public static String createPaymentMethod() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "CreatePaymentMethod");
    }

    public static String getPaymentToken() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "GetPaymentToken");
    }

    public static String decreaseOrderItem() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "DecreaseOrderItem");
    }

    public static String increaseOrderItem() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "IncreaseOrderItem");
    }

    public static String applyVouchers() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "ApplyVouchers");
    }

    public static String clearVouchers() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "ClearVouchers");
    }

    public static String clearOrder() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "ClearOrder", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String createPaymentTransaction() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "CreatePaymentTransaction");
    }

    public static String createPaymentTransactionForGPay() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "CreatePaymentTransaction");
    }

    public static String removeUserPaymentMethod() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "RemoveUserPaymentMethod");
    }

    public static String getDeliveryServicePackageDetails() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "GetDeliveryServicePackageDetails");
    }

    public static String setDeliveryOption() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "SetDeliveryOption");
    }

    /* Legalities Endpoint*/
    public static String getLegalitiesText() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_SETTINGS, NO_AKAMAI_EXTENSION + "GetTemplateText");
    }

    public static String getTemplateTexts() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_SETTINGS, NO_AKAMAI_EXTENSION + "GetTemplateTexts");
    }

    /* Login Controller */
    public static String forgotPassword() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "ForgotPassword", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String loginEmail() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "Signin", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String loginFb() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "LoginFacebook", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String loginTicket() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "LoginTicket", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String logout() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "Logout", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String registration() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "Signup", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String saveUserDetails() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "SetUserDetails", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String loadUserDetails() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "GetUserDetails", ApiUrlVersion.emptyVersion.apiVersion());
    }

    /* Summary */
    public static String getSummaryMenu() {
        return getFormattedUrl(ApiService.GENIE, "menu", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getSummaryDashboards() {
        return getFormattedUrl(ApiService.GENIE, "summary/dashboards", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getSummaryFilters() {
        return getFormattedUrl(ApiService.GENIE, "summary/{dashboardName}/filters", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getSummaryMeasures() {
        return getFormattedUrl(ApiService.GENIE, "summary/{dashboardName}/measures", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getSummaryData() {
        return getFormattedUrl(ApiService.GENIE, "summary/{dashboardName}/data", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getSummaryDataFilters() {
        return getFormattedUrl(ApiService.GENIE, "summary/{dashboardName}/data/{period?}{measure}{&filters}", ApiUrlVersion.emptyVersion.apiVersion());
    }

    /* Overview */
    public static String getOverviewMenu() {
        return getFormattedUrl(ApiService.GENIE, "menu", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getOverviewDashboards() {
        return getFormattedUrl(ApiService.GENIE, "revenue/dashboards", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getOverviewFilters() {
        return getFormattedUrl(ApiService.GENIE, "revenue/overview/filters", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getOverviewData() {
        return getFormattedUrl(ApiService.GENIE, "revenue/overview/data", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getOverviewDataFilters() {
        return getFormattedUrl(ApiService.GENIE, "revenue/overview/data?{period}{&filters}", ApiUrlVersion.emptyVersion.apiVersion());
    }

    /* Masterpass */
    public static String masterpassPayment() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "MasterPassPayment");
    }

    public static String masterpassPostTransaction() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "MasterPassPostTransaction");
    }

    /* 3DS */
    public static String getPaymentMethodNonce() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_SETTINGS, NO_AKAMAI_EXTENSION + "GetPaymentMethodNonce");
    }

    /* Ourpay */
    public static String getPaymentPlans() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "GetPaymentPlans");
    }

    public static String getScheduledPlans() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "GetScheduledPayments");
    }

    public static String getPastPayments() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "GetPastPayments");
    }

    public static String getDeliveryService() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "GetDeliveryServicePackageByCustomer");
    }

    public static String processOurpayInstallment() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "ProcessOurpayInstallment");
    }

    public static String createAfterpayOrder() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "CreateAfterpayOrder");
    }

    public static String callAfterPayCreatePayment() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "AfterPayCreatePayment");
    }

    public static String callGetAfterpayData() {
        return getFormattedUrl(ApiService.PRODUCT, ACCOUNT_ID_DELIMETER + "/afterpay?amount={amount}", ApiUrlVersion.v2.apiVersion());
    }

    /*VISA CHECKOUT*/
    public static String visaCheckoutLogin() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "LoginVisa", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String getWishlistIdsOnly() {
        return getFormattedUrl(ApiService.WISHLIST, "ids", ApiUrlVersion.v4.apiVersion());
    }

    public static String getWishlistAll() {
        return getFormattedUrl(ApiService.SHOP, ACCOUNT_ID_DELIMETER + "/wishlist", ApiUrlVersion.v4.apiVersion());
    }

    public static String getWishlistPaginated() {
        return getFormattedUrl(ApiService.SHOP, ACCOUNT_ID_DELIMETER + "/wishlist/search?limit={limit}&offset={offset}", ApiUrlVersion.v5.apiVersion());
    }

    public static String addToWishlist() {
        return getFormattedUrl(ApiService.WISHLIST, "", ApiUrlVersion.v4.apiVersion());
    }

    public static String removeFromWishlist() {
        return getFormattedUrl(ApiService.WISHLIST, "{product_id}", ApiUrlVersion.v4.apiVersion());
    }

    public static String setAttachment() {
        return getFormattedUrl(ApiService.LEGACY, NO_AKAMAI_EXTENSION + "SetAttachment", ApiUrlVersion.emptyVersion.apiVersion());
    }

    public static String setAttachmentForContact() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/threads/number={number}/messages/{id}/attachments", ApiUrlVersion.v1.apiVersion());
    }

    public static String uploadImage() {
        return getFormattedUrl(ApiService.ATTACHMENTS, "attachment/", ApiUrlVersion.v1.apiVersion());
    }

    public static String fileSettings() {
        return getFormattedUrl(ApiService.ATTACHMENTS, "settings", ApiUrlVersion.v1.apiVersion());
    }

    public static String recommendedItems() {
        return getFormattedUrl(ApiService.PRODUCT, ACCOUNT_ID_DELIMETER + "/recommendations/hrnn", ApiUrlVersion.v4.apiVersion());
    }

    public static String callYouMakeAlsoLike() {
        return getFormattedUrl(ApiService.PRODUCT, ACCOUNT_ID_DELIMETER + "/recommendations/sims/{sku_id}", ApiUrlVersion.v4.apiVersion());
    }

    public static String addToRecentlyViewedItems() {
        return getFormattedUrl(ApiService.RECENTLY, "", ApiUrlVersion.v4.apiVersion());
    }

    public static String recentlyViewedItems() {
        return getFormattedUrl(ApiService.SHOP, ACCOUNT_ID_DELIMETER + "/recently", ApiUrlVersion.v5.apiVersion());
    }

    public static String getTicketSatisfaction() {
        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/threads/number={number}:ticket_satisfaction", ApiUrlVersion.v1.apiVersion());
    }

    public static String closeTicketSatisfaction() {

        return getFormattedUrl(ApiService.MYACCOUNT, ACCOUNT_ID_DELIMETER + "/my-account/threads/number={number}:close", ApiUrlVersion.v1.apiVersion());
    }

    public static String getPostcodeDefault() {
        return getFormattedUrl(ApiService.EVENTING, "users/current", ApiUrlVersion.v2.apiVersion());
    }

    public static String getPostcodeShippingPrice() {
        return getFormattedUrl(ApiService.PRODUCT, ACCOUNT_ID_DELIMETER + "/products/delivery?postCode={postcode}&skuId={skuid}&weight={weight}&height={height}&length=&width={width}&price={price}", ApiUrlVersion.v1.apiVersion());
    }

    public static String saveAgeRestrictedConsentData() {
        return getFormattedUrlForCustomLegacyVersion(LEGACY_API_VERSION_FOR_CHECKOUT, NO_AKAMAI_EXTENSION + "SaveAgeRestrictedConsentData");
    }

    private ApiEndPoint() {
        // This class is not publicly instantiable
    }


    public static void main(String[] args) {
        System.out.println(LEGACY_API_VERSION);
    }

}
