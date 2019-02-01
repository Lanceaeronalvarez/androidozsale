package au.com.dealsdirect.data.network;


import java.util.HashMap;
import java.util.Set;

import au.com.dealsdirect.BuildConfig;
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
    public static final double LEGACY_API_VERSION = 3.26;

    enum ApiService {
        legacy,
        genie,
        shop,
        sale,
        product,
        productDetails,
        setting,
        eventing
    }

    private static String getFormattedUrl(ApiService service, String url) {

        HashMap<String, String> replaceStrings = new HashMap<>();
        replaceStrings.put(ACCOUNT_ID_DELIMETER, Settings.getSelectedCountry().accountId);

        Set<String> keySet = replaceStrings.keySet();
        for (String key : keySet) {
            String value = replaceStrings.get(key);
            url = url.replaceFirst(key, value);
        }

        String microServiceUrl = "";
        switch (service) {
            case shop: microServiceUrl = "api/shop/shop/v1/accounts/";break;
            case sale: microServiceUrl = "api/sale/sale/v3/accounts/";break;
            case product: microServiceUrl = "api/shop/product/v2/accounts/";break;
            case productDetails: microServiceUrl = "api/shop/product/v2/accounts/";break;
            case setting: microServiceUrl = "api/shop/settings/v1/";break;
            case eventing: microServiceUrl = "api/shop/eventing/v1/";break;
            case legacy: microServiceUrl = (BuildConfig.IS_TEST ? "Public/V" : "papi/public/v") + LEGACY_API_VERSION + "/";break;
            case genie: microServiceUrl = HANDLER_PREFIX;break;
        }

        Settings.Country selectedCountry = Settings.getSelectedCountry();
        String root = service == ApiService.legacy ? selectedCountry.legacyRoot : selectedCountry.genieRoot;
        return root + microServiceUrl + url;
    }

    public static String getCategoryTree() {
        return getFormattedUrl(ApiService.shop,ACCOUNT_ID_DELIMETER + "/categorytree/");
    }

    public static String getSales() {
        return getFormattedUrl(ApiService.sale,ACCOUNT_ID_DELIMETER + "/banners/grouped/");
    }

    public static String getSorting(){
        return getFormattedUrl(ApiService.shop,ACCOUNT_ID_DELIMETER + "/sorting/");
    }

    public static String getProducts(){
        return getFormattedUrl(ApiService.shop,ACCOUNT_ID_DELIMETER + "/products");
    }

    public static String getProductDetails(){
        return getFormattedUrl(ApiService.productDetails,ACCOUNT_ID_DELIMETER + "/sales/{sale_id}/products/{seo_identifier}");
    }

    public static String getProductDetailsFromCategories(){
        return getFormattedUrl(ApiService.productDetails,ACCOUNT_ID_DELIMETER + "/products/{seo_identifier}");
    }

    public static String getProductDetailDynamicDiscount() {
        return getFormattedUrl(ApiService.productDetails, ACCOUNT_ID_DELIMETER + "/price/label");
    }

    public static String getOurpayData(){
        return getFormattedUrl(ApiService.productDetails,ACCOUNT_ID_DELIMETER + "/ourpaydata");
    }

    public static String getAddToCart(){
        return getFormattedUrl(ApiService.product,ACCOUNT_ID_DELIMETER + "/basket/items");
    }

    public static String getBasketQuantity(){
        return getFormattedUrl(ApiService.productDetails,ACCOUNT_ID_DELIMETER + "/basket/items/quantity");
    }

    public static String getSearchEvent(){
        return getFormattedUrl(ApiService.eventing,"events");
    }

    public static String getEventUser(){
        return getFormattedUrl(ApiService.eventing,"users/current");
    }

    /* Deep Link Data */
    public static String deepLink(){
        return getFormattedUrl(ApiService.setting,"deeplinkdata");
    }

    /* Account Data*/
    public static String accountData(){
        return getFormattedUrl(ApiService.setting,"settings/accountdata/" + ACCOUNT_ID_DELIMETER);
    }

    /*SMS VERIFICATION*/
    public static String getSmsVerificationNormalizePhone(){
        return getFormattedUrl(ApiService.legacy,NO_AKAMAI_EXTENSION + "NormalizePhone");
    }

    public static String getSmsVerificationCodeSend(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "VerificationCodeSend");
    }

    public static String getSmsVerificationCodeConfirm(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "VerificationCodeConfirm");
    }

    /*GCM CALLS*/
    public static String gcmRegisterDevice(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "RegisterDevice");
    }

    public static String gcmNotificationEvent(){
        return getFormattedUrl(ApiService.legacy, AKAMAI_EXTENSION + "NotificationEvent");
    }

    public static String gcmRegisterSubscriber(){
        return getFormattedUrl(ApiService.legacy, AKAMAI_EXTENSION + "RegisterSubscriber");
    }

    /*CONFIG CALLS*/
    public static String getServerSettings(){
        return getFormattedUrl(ApiService.legacy, AKAMAI_EXTENSION + "GetServerSettings");
    }

    public static String getPublicAppSettings(){
        return getFormattedUrl(ApiService.legacy, AKAMAI_EXTENSION+ "GetPublicAppSettings");
    }

    public static String getAppSettings(){
        return getFormattedUrl(ApiService.legacy, AKAMAI_EXTENSION + "GetAppSettings");
    }

    public static String getAppSettingsSection(){
        return getFormattedUrl(ApiService.legacy, AKAMAI_EXTENSION + "GetAppSettingsSection");
    }

    public static String getPublicAppSettingsSection(){
        return getFormattedUrl(ApiService.legacy, AKAMAI_EXTENSION + "GetPublicAppSettingsSection");
    }

    public static String getConsentData(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetConsentData");
    }

    public static String saveConsentData(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "SaveConsentData");
    }

    public static String saveReceiveSales(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "SaveReceiveSales");
    }

    public static String getPublicPaymentToken(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetPublicPaymentToken");
    }

    public static String getUserLanguages(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetUserLanguages");
    }

    public static String setUserLanguages(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "SetUserLanguage");
    }

    /* Shops Controller */
    public static String getShopBanners(){
        return getFormattedUrl(ApiService.genie, "GetPublicSalesBanners");
    }

    public static String getSalesCategories(){
        return getFormattedUrl(ApiService.genie, "GetSaleCategories");
    }

    /* Sale Items Controller */
    public static String getPublicSaleItems(){
        return getFormattedUrl(ApiService.genie, "GetPublicSaleItems");
    }

    /* Categories Controller */
    public static String getShopCategories(){
        return getFormattedUrl(ApiService.genie, "GetPublicSalesCategories");
    }

    /* Sales Controller */
    public static String getSaleItems(){
        return getFormattedUrl(ApiService.genie, "GetSaleItems");
    }

    public static String getSaleDetails(){
        return getFormattedUrl(ApiService.genie, "GetSaleDetails");
    }

    public static String getSaleCategories(){
        return getFormattedUrl(ApiService.genie, "GetSaleCategories");
    }

    /* Sale Detail Controller */
    public static String getSalesItemSaleDetails(){
        return getFormattedUrl(ApiService.genie, "GetPublicSaleDetails");
    }

    public static String getSalesItemDetails(){
        return getFormattedUrl(ApiService.genie, "GetPublicItemDetails");
    }

    /* Voucher Controller */
    public static String addVoucherByKey(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "AddVoucherByKey");
    }

    public static String addAndApplyVoucher(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "AddAndApplyVoucherByKey");
    }

    public static String getUserVouchers(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetUserVouchers");
    }

    public static String getVouchers(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetVouchers");
    }

    /* Invite Controller */
    public static String setInvite(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "SetInviteLink");
    }

    public static String getInvite(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetInviteLink");
    }

    /* Contact Controller */
    public static String answerContact(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "AnswerContact");
    }

    public static String createContact(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "CreateContact");
    }

    public static String getContactInvoices(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetContactInvoices");
    }

    public static String getContact(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetContact");
    }

    public static String getContacts(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetContacts");
    }

    public static String getContactSubjects(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetContactSubjects");
    }

    /* Return Controller */
    public static String getReturns(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetReturns");
    }

    public static String getReturnOrders(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetReturnOrders");
    }

    public static String getReturnOrderDetail(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetReturnOrderDetail");
    }

    public static String getReturnDetails(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetReturnDetails");
    }

    public static String createReturn(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "CreateReturn");
    }

    /* Address Controller */
    public static String deleteUserDeliveryAddress(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "DeleteUserDeliveryAddress");
    }

    public static String getUserAddresses(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetUserAddresses");
    }

    public static String setUserDeliveryAddress(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "SetUserDeliveryAddress");
    }

    public static String applyDeliveryAddress(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "ApplyDeliveryAddress");
    }

    /* Orders Controller*/
    public static String getPaymentsList(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetPaymentsList");
    }

    public static String getOrderPaymentDetails(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetOrderPaymentDetails");
    }

    /* Checkout Endpoints*/
    public static String getCurrentOrder(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetCurrentOrder");
    }

    public static String getUserPaymentMethods(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetUserPaymentMethods");
    }

    public static String createPaymentMethod(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "CreatePaymentMethod");
    }

    public static String getPaymentToken(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetPaymentToken");
    }

    public static String decreaseOrderItem(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "DecreaseOrderItem");
    }

    public static String increaseOrderItem(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "IncreaseOrderItem");
    }

    public static String applyVouchers(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "ApplyVouchers");
    }

    public static String clearVouchers(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "ClearVouchers");
    }

    public static String clearOrder(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "ClearOrder");
    }

    public static String createPaymentTransaction(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "CreatePaymentTransaction");
    }

    public static String removeUserPaymentMethod(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "RemoveUserPaymentMethod");
    }

    public static String getDeliveryServicePackageDetails(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetDeliveryServicePackageDetails");
    }

    public static String setDeliveryOption(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "SetDeliveryOption");
    }

    /* Legalities Endpoint*/
    public static String getLegalitiesText(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetTemplateText");
    }

    public static String getTemplateTexts(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetTemplateTexts");
    }

    /* Login Controller */
    public static String forgotPassword(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "ForgotPassword");
    }

    public static String loginEmail(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "Login");
    }

    public static String loginFb(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "LoginFacebook");
    }

    public static String loginTicket(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "LoginTicket");
    }

    public static String logout(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "Logout");
    }

    public static String registration(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "Registration");
    }

    public static String saveUserDetails(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "SetUserDetails");
    }

    public static String loadUserDetails(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetUserDetails");
    }

    /* Summary */
    public static String getSummaryMenu(){
        return getFormattedUrl(ApiService.genie, "menu");
    }

    public static String getSummaryDashboards(){
        return getFormattedUrl(ApiService.genie, "summary/dashboards");
    }

    public static String getSummaryFilters(){
        return getFormattedUrl(ApiService.genie, "summary/{dashboardName}/filters");
    }

    public static String getSummaryMeasures(){
        return getFormattedUrl(ApiService.genie, "summary/{dashboardName}/measures");
    }

    public static String getSummaryData(){
        return getFormattedUrl(ApiService.genie, "summary/{dashboardName}/data");
    }

    public static String getSummaryDataFilters(){
        return getFormattedUrl(ApiService.genie, "summary/{dashboardName}/data/{period?}{measure}{&filters}");
    }

    /* Overview */
    public static String getOverviewMenu(){
        return getFormattedUrl(ApiService.genie, "menu");
    }

    public static String getOverviewDashboards(){
        return getFormattedUrl(ApiService.genie, "revenue/dashboards");
    }

    public static String getOverviewFilters(){
        return getFormattedUrl(ApiService.genie, "revenue/overview/filters");
    }

    public static String getOverviewData(){
        return getFormattedUrl(ApiService.genie, "revenue/overview/data");
    }

    public static String getOverviewDataFilters(){
        return getFormattedUrl(ApiService.genie, "revenue/overview/data?{period}{&filters}");
    }

    /* Masterpass */
    public static String masterpassPayment(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "MasterPassPayment");
    }

    public static String masterpassPostTransaction(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "MasterPassPostTransaction");
    }

    /* 3DS */
    public static String getPaymentMethodNonce(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetPaymentMethodNonce");
    }

    /* Ourpay */
    public static String getPaymentPlans(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetPaymentPlans");
    }

    public static String getScheduledPlans(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetScheduledPayments");
    }

    public static String getPastPayments(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetPastPayments");
    }

    public static String getDeliveryService(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "GetDeliveryServicePackageByCustomer");
    }

    /*VISA CHECKOUT*/
    public static String visaCheckoutLogin(){
        return getFormattedUrl(ApiService.legacy, NO_AKAMAI_EXTENSION + "LoginVisa");
    }

    private ApiEndPoint() {
         // This class is not publicly instantiable
    }

}
