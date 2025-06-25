package au.com.dealsdirect.data.pref;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Build;

import com.mysale.genie.utility.Prefs;
import com.mysale.genie.utility.config.api.GetAppSettingsConsent;

import java.util.HashSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;
import au.com.dealsdirect.data.network.model.vouchers.GetUserVoucherResponse;
import au.com.dealsdirect.di.ApplicationContext;
import au.com.dealsdirect.di.PreferenceInfo;
import au.com.dealsdirect.service.fcm.GNotification;
import au.com.dealsdirect.ui.controller.main.Settings;


@Singleton
public class AppPreferencesHelper implements PreferencesHelper {

    //Elv - Declare preference keys here
    private static final String PREF_KEY_USER_LOGGED_IN_MODE = "PREF_KEY_USER_LOGGED_IN_MODE";
    private static final String PREF_KEY_CURRENT_USER_ID = "PREF_KEY_CURRENT_USER_ID";
    private static final String PREF_KEY_CURRENT_USER_NAME = "PREF_KEY_CURRENT_USER_NAME";
    private static final String PREF_KEY_CURRENT_USER_EMAIL = "PREF_KEY_CURRENT_USER_EMAIL";
    private static final String PREF_KEY_CURRENT_USER_PROFILE_PIC_URL
            = "PREF_KEY_CURRENT_USER_PROFILE_PIC_URL";
    private static final String PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN";
    private static final String PREF_KEY_USER_AGENT = "PREF_KEY_USER_AGENT";
    private static final String IS_INITIAL_LAUNCH = "IS_INITIAL_LAUNCH";


    private static final String DEBUG_MODE = "app_debug_mode";
    private static final String COUNTRY_ID = "server_country_id";
    private static final String COUNTRY_ISO = "server_country_iso";
    private static final String LANGUAGE_ID = "server_language_id";
    private static final String LEGACY_COUNTRY_ID = "mysalecountryid";
    private static final String LEGACY_LANGUAGE_ID = "languageId";
    private static final String LANGUAGE_LIST = "server_language_list";
    private static final String IS_MULTI_LANGUAGE = "server_multi_language";
    private static final String IS_MULTI_COUNTRY = "server_multi_country";
    private static final String SITE_NAME = "server_site_name";
    private static final String CURRENCY = "server_currency";
    private static final String CURRENCY_SIGN = "server_currency_sign";
    private static final String FOLLOW_US_LINK_FB = "server_follow_us_fb";
    private static final String FOLLOW_US_LINK_TWITTER = "server_follow_us_twitter";
    private static final String IMAGE_SERVER_URL = "server_image_server_url";
    private static final String USER_HAS_RATE_APP = "server_user_rate";

    private static final String PAYMENT_PAYPAL_ENABLED = "app_paypal_enabled";
    private static final String PAYMENT_MASTERPASS_ENABLED = "app_masterpass_enabled";
    private static final String PAYMENT_AFTERPAY_ENABLED = "app_afterpay_enabled";
    private static final String PAYMENT_AMEX_ENABLED = "app_amex_enabled";
    private static final String PAYMENT_KOUNT_ENABLED = "app_kount_enabled";
    private static final String PAYMENT_KOUNT_MERCHANT_ID = "app_kount_merchant_id";
    private static final String PAYMENT_MYPAY_ENABLED = "app_mypay_enabled";
    private static final String PAYMENT_PAYPAL_CREDIT_ENABLED = "app_paypal_credit_enabled";

    private static final String PUBLIC_PAYMENT_TOKEN = "PUBLIC_PAYMENT_TOKEN";
    private static final String PUBLIC_PAYMENT_TYPE = "PUBLIC_PAYMENT_TYPE";

    private static final String CURRENT_PAYMENT_TOKEN = "PUBLIC_PAYMENT_TOKEN";
    private static final String CURRENT_PAYMENT_TYPE = "PUBLIC_PAYMENT_TYPE";

    /*VISA CHECKOUT*/

    private static final String VCO_ENABLED = "VCO_ENABLED";
    private static final String VCO_APIKEY = "VCO_APIKEY";
    private static final String VCO_APIURL = "VCO_APIURL";
    private static final String VCO_PROVIDERTYPE = "VCO_PROVIDERTYPE";

    //    DELIVERY OPTIONS
    private static final String KEY_DELIVERYOPTION_EXPRESS_TITLE = "_DeliveryOption_EXPRESS_Title";
    private static final String KEY_DELIVERYOPTION_EXPRESS_DESCRIPTION = "_DeliveryOption_EXPRESS_Description";
    private static final String KEY_DELIVERYOPTION_STANDARD_TITLE = "_DeliveryOption_STANDARD_Title";

    private static final String SEARCH_MAX_PRICE = "app_search_max_price";
    private static final String ACCESS_ANONYMOUS_ENABLED = "app_anonymous_enabled";
    private static final String FB_SECRET = "fb_secret";
    private static final String PAYMENT_COUNT = "payment_count";

    private static final String EVENT_USER_ID = "event_user_id";
    private static final String COOKIES = "network_cookies";

    /* PERSONALISATION */
    private static final String PERSONALISATION_VALIDATION = "PERSONALISATION_VALIDATION";

    /* NOTIFICATIONS */
    private static final String KEY_NOTIFICATIONS_ENABLED = "KEY_NOTIFICATIONS_ENABLED";

    /* GDPR */
    public static final String CONSENT_CONTINUE_TEXT = "_consentContinueText";
    public static final String CONSENT_WITH_REGISTRATION_TERMS_TEXT = "_consentWithTCText";
    public static final String CONSENT_WITH_REGISTRATION_EMAILS_TEXT = "_consentWithEmailsText";
    public static final String CONSENT_WITH_REGISTRATION_TERMS_WARNING = "_consentWithRegistrationTermsWarning";
    public static final String CONSENT_SHORT_TEMPLATE_TEXT = "ConsentShortTextPTNameV1";
    public static final String CONSENT_FULL_TEMPLATE_TEXT = "ConsentFullTextPTNameV1";
    public static final String CONSENT_TERMS_AND_CONDITION = "TermsAndConditions_Text";

    public static final String CONSENT_SHORT_TEXT = "ShortTextPTName";
    public static final String CONSENT_FULL_TEXT = "FullTextPTName";
    public static final String CONSENT_MODE = "Mode";
    public static final String CONSENT_TNC_CHECKED = "RegAgreementTermsAndConditionCheckboxTicked";
    public static final String CONSENT_EMAILS_CHECKED = "RegAgreementReceiveEmailsCheckboxTicked";
    public static final String SHOW_STRICT_CONSENT = "ShowStrictConsent";

    public static final String VOUCHER_NEW = "VoucherNewTemplateText";
    public static final String VOUCHER_ALREADY_SPENT = "VoucherAlreadySpentTemplateText";
    public static final String VOUCHER_EXPIRING_SOON = "VoucherExpiringSoonTemplateText";
    public static final String VOUCHER_EXPIRED = "VoucherExpiredTemplateText";
    public static final String VOUCHER_PENDING = "VoucherPendingTemplateText";

    /*ACCOUNT DATA*/
    public static final String ACCOUNT_DATA_SORTING = "AccountDataSorting";

    /* Fabric App Events */
    private static final String LAST_REDIRECTION = "LAST_REDIRECTION_SCREEN";
    private static final String IS_NEW_USER = "IS_NEW_USER";
    private static final String HAS_ACTIVE_CHECKOUT_SESSION = "HAS_ACTIVE_CHECKOUT_SESSION";
    private static final String CART_HASH_CODE = "CART_HASH_CODE";

    // AddToCart Journey
    private static final String HAS_VIEWED_SALE = "HAS_VIEWED_SALE";
    private static final String HAS_VIEWED_PRODUCT_CATEGORY = "HAS_VIEWED_PRODUCT_CATEGORY";
    private static final String HAS_VIEWED_PRODUCT = "HAS_VIEWED_PRODUCT";
    private static final String HAS_ADDED_TO_CART = "HAS_CLICKED_ADD_TO_CART";
    private static final String HAS_VIEWED_CART = "HAS_CLICKED_VIEW_CART";

    //Aferpay
    private static final String IS_AFTERPAY_ENABLED = "IS_AFTERPAY_ENABLED";
    private static final String AFTERPAY_SCRIPT_URI = "app_afterpay_script_uri";
    private static final String AFTERPAY_LIGHTBOX_IMG_URL = "app_afterpay_lightboximgurl";
    private static final String AFTERPAY_TERMS_LINK = "app_afterpay_terms_link";

    //LPay
    private static final String IS_LPAY_ENABLED = "IS_LPAY_ENABLED";

    //ReCAPTCHA
    private static final String RECAPTCHA_SITE_KEY = "RECAPTCHA_SITE_KEY";

    private static final String GOOGLE_ADS = "GOOGLE_ADS";
    private static final String COLOR_FILTER = "COLOR_FILTER";
    private static final String COLUMN_COUNT = "COLUMN_COUNT";
    private static final String LAST_TIME_STAMP = "LAST_TIME_STAMP";
    private static final String FILE_SIZE_LIMIT = "FILE_SIZE_LIMIT";

    private static final String HAS_WISHLIST_BEEN_ACCESSED = "HAS_WISHLIST_BEEN_ACCESSED";
    private static final String STRIPE_PUBLIC_KEY = "STRIPE_PUBLIC_KEY";
    private static final String STRIPE_ENABLED = "STRIPE_ENABLED";
    private static final String STRIPE_PAYMENT_METHOD_ID = "STRIPE_PAYMENT_METHOD_ID";

    private static final String SHIPPING_HOVER = "SHIPPING_HOVER";
    private static final String SHIPPING_TITLE = "SHIPPING_TITLE";

    private static final String SHIPPING_BY_POSTCODE_ENABLED = "SHIPPING_BY_POSTCODE_ENABLED";

    private static final String PHONE_BANNER_COLUMNS = "PHONE_BANNER_COLUMNS";
    private static final String TABLET_BANNER_COLUMNS = "TABLET_BANNER_COLUMNS";

    private static final String PREFERS_OLD_SHOP_BANNER_DIMENSIONS = "PREFERS_OLD_SHOP_BANNER_DIMENSIONS";

    private static final String DEFAULT_POSTCODE = "DEFAULT_POSTCODE";

    private static final String SUPPLIER_ORIGINAL_PRICE_INFO_SALE_LIST = "SUPPLIER_ORIGINAL_PRICE_INFO_SALE_LIST";

    private static final String SUPPLIER_ORIGINAL_PRICE_INFO_TEMPLATE_TEXT_1 = "SUPPLIER_ORIGINAL_PRICE_INFO_TEMPLATE_TEXT_1";
    private static final String SUPPLIER_ORIGINAL_PRICE_INFO_TEMPLATE_TEXT_2 = "SUPPLIER_ORIGINAL_PRICE_INFO_TEMPLATE_TEXT_2";

    private static final String SUPPLIER_ORIGINAL_PRICE_INFO_ENABLED = "SUPPLIER_ORIGINAL_PRICE_INFO_ENABLED";

    private static final String BUYBOX_TITLE = "BUYBOX_TITLE";
    private static final String BUYBOX_SELLER_TEMPLATE = "BUYBOX_SELLER_TEMPLATE";
    private static final String BUYBOX_BUTTON_TEXT = "BUYBOX_BOTTOM_TEXT";

    private static final String KLARNA_ENABLED = "KLARNA_ENABLED";


    private static final String ZIP_PAY_ENABLED = "ZIP_PAY_ENABLED";

    private static final String PRODUCT_PAGE_PRICE_BLOCK_MODE = "PRODUCT_PAGE_PRICE_BLOCK_MODE";

    private static final String HOURS_LEFT_TO_DISPLAY_TIMER = "HOURS_LEFT_TO_DISPLAY_TIMER";

    private Context mContext;

    @Inject
    public AppPreferencesHelper(@ApplicationContext Context context,
                                @PreferenceInfo String prefFileName) {
        mContext = context;
        new Prefs.Builder()
                .setContext(context)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(prefFileName)
                .build();

        //Set default settings here
        Prefs.putString(COUNTRY_ID, getCountryId());
        Prefs.putString(LANGUAGE_ID, getLanguageId());
        Prefs.putBoolean(SHOW_STRICT_CONSENT, shouldShowStrictConsent());
        Prefs.putBoolean(DEBUG_MODE, context.getResources().getBoolean(R.bool.debug_mode));
        setUserAgent();
    }

    //Elv - Override exposed methods from Preference Helper here

    @Override
    public int getCurrentUserLoggedInMode() {
//        return mPrefs.getInt(PREF_KEY_USER_LOGGED_IN_MODE,
//                DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT.getType());

        return 0;
    }

    @Override
    public void setUserAgent() {
        String appName = mContext.getResources().getString(R.string.app_name);
        String appVersion;
        try {
            String versionNumber = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            int versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
            appVersion = versionNumber + " rv:" + versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

            appVersion = "";
        }

        String deviceName, OSName, OSVersion, locale, interfaceIdiom, countryId;
        deviceName = Build.MANUFACTURER + " " + Build.MODEL;
        OSName = "Android";
        OSVersion = Build.VERSION.RELEASE;
        locale = mContext.getResources().getConfiguration().locale.toString();
        interfaceIdiom = mContext.getResources().getBoolean(R.bool.is_tablet) ? "07" : "06";
        countryId = getCountryId();

        String userAgent = appName + " "
                + appVersion + " ("
                + deviceName + "; "
                + OSName + "; "
                + OSVersion + "; "
                + locale + "; "
                + "APACSALE:"
                + interfaceIdiom + ":"
                + countryId + ")";

        Prefs.putString(PREF_KEY_USER_AGENT, userAgent);
    }

    @Override
    public String getUserAgent() {
        return Prefs.getString(PREF_KEY_USER_AGENT, "");
    }

    @Override
    public void setCountryId(String countryId) {
        Prefs.putString(COUNTRY_ID, countryId);
    }

    @Override
    public String getCountryId() {
        return Prefs.getString(COUNTRY_ID, Settings.getDefaultCountry() != null ?
                Settings.getDefaultCountry().countryId : "");
    }

    @Override
    public void setCountryIso(String countryIso) {
        Prefs.putString(COUNTRY_ISO, countryIso);
    }

    @Override
    public String getCountryIso() {
        return Prefs.getString(COUNTRY_ISO, "");
    }

    @Override
    public void setLanguageId(String languageId) {
        Prefs.putString(LANGUAGE_ID, languageId);
    }

    @Override
    public String getLanguageId() {
        return Prefs.getString(LANGUAGE_ID, Settings.getDefaultCountry() != null ?
                Settings.getDefaultCountry().languageId : "");
    }

    @Override
    public String getLegacyCountryId() {
        return Prefs.getString(LEGACY_COUNTRY_ID, "");
    }

    @Override
    public String getLegacyLanguageId() {
        return Prefs.getString(LEGACY_LANGUAGE_ID, "");
    }

    @Override
    public void setLanguages(String languagesString) {
        Prefs.putString(LANGUAGE_LIST, languagesString);
    }

    @Override
    public String getLanguages() {
        return Prefs.getString(LANGUAGE_LIST, "");
    }

    @Override
    public void setSiteName(String siteName) {
        Prefs.putString(SITE_NAME, siteName);
    }

    @Override
    public String getSiteName() {
        return Prefs.getString(SITE_NAME, "");
    }

    @Override
    public void setCurrency(String currency) {
        Prefs.putString(CURRENCY, currency);
    }

    @Override
    public String getCurrency() {
        return Prefs.getString(CURRENCY, "");
    }

    @Override
    public void setCurrencySign(String currencySign) {
        Prefs.putString(CURRENCY_SIGN, currencySign);
    }

    @Override
    public String getCurrencySign() {
        return Prefs.getString(CURRENCY_SIGN, "");
    }

    @Override
    public void setFollowUsFbLink(String followUsFbLink) {
        Prefs.putString(FOLLOW_US_LINK_FB, followUsFbLink);
    }

    @Override
    public String getFollowUsFbLink() {
        String FACEBOOK_URL = Prefs.getString(FOLLOW_US_LINK_FB, "");
        String FACEBOOK_PAGE_ID = mContext.getString(R.string.fb_page_id);     //  http://findmyfbid.com/
        try {
            mContext.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return "fb://page/" + FACEBOOK_PAGE_ID;
        } catch (Exception e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    @Override
    public void setFollowUsTwitterLink(String followUsTwitterLink) {
        Prefs.putString(FOLLOW_US_LINK_TWITTER, followUsTwitterLink);
    }

    @Override
    public String getFollowUsTwitterLink() {
        return Prefs.getString(FOLLOW_US_LINK_TWITTER, "");
    }

    @Override
    public void setImageServerUrl(String imageServerUrl) {
        Prefs.putString(IMAGE_SERVER_URL, imageServerUrl);
    }

    @Override
    public String getImageServerUrl() {
        return Prefs.getString(IMAGE_SERVER_URL, "");
    }

    @Override
    public void setIsPaypalEnabled(boolean val) {
        Prefs.putBoolean(PAYMENT_PAYPAL_ENABLED, val);
    }

    @Override
    public boolean isPaypalEnabled() {
        return Prefs.getBoolean(PAYMENT_PAYPAL_ENABLED, false);
    }

    @Override
    public void setIsMasterpassEnabled(boolean val) {
        Prefs.putBoolean(PAYMENT_MASTERPASS_ENABLED, val);
    }

    @Override
    public boolean isMasterpassEnabled() {
        return Prefs.getBoolean(PAYMENT_MASTERPASS_ENABLED, false);
    }

    @Override
    public void setIsAfterpayEnabled(boolean val) {
        Prefs.putBoolean(PAYMENT_AFTERPAY_ENABLED, val);
    }

    @Override
    public boolean isAfterpayEnabled() {
        return Prefs.getBoolean(PAYMENT_AFTERPAY_ENABLED, false);
    }

    @Override
    public void setAfterpayScriptUri(String uri) {
        Prefs.putString(AFTERPAY_SCRIPT_URI, uri);
    }

    @Override
    public String getAfterpayScriptUri() {
        return Prefs.getString(AFTERPAY_SCRIPT_URI, "");
    }

    @Override
    public void setAfterpayLightboxImgUrl(String url) {
        Prefs.putString(AFTERPAY_LIGHTBOX_IMG_URL, url);
    }

    @Override
    public String getAfterpayLightboxImageUrl() {
        return Prefs.getString(AFTERPAY_LIGHTBOX_IMG_URL, "");
    }

    @Override
    public void setAfterpayTermsLink(String link) {
        Prefs.putString(AFTERPAY_TERMS_LINK, link);
    }

    @Override
    public String getAfterpayTermsLink() {
        return Prefs.getString(AFTERPAY_TERMS_LINK, "");
    }

    @Override
    public void setLPayEnabled(boolean enabled) {
        Prefs.putBoolean(IS_LPAY_ENABLED, enabled);
    }

    @Override
    public boolean isLPayEnabled() {
        return Prefs.getBoolean(IS_LPAY_ENABLED, false);
    }

    @Override
    public void setIsAmexEnabled(boolean val) {
        Prefs.putBoolean(PAYMENT_AMEX_ENABLED, val);
    }

    @Override
    public boolean isAmexEnabled() {
        return Prefs.getBoolean(PAYMENT_AMEX_ENABLED, false);
    }

    @Override
    public void setIsKountEnabled(boolean val) {
        Prefs.putBoolean(PAYMENT_KOUNT_ENABLED, val);
    }

    @Override
    public boolean isKountEnabled() {
        return Prefs.getBoolean(PAYMENT_KOUNT_ENABLED, false);
    }

    @Override
    public void setKountMerchantId(String kountMerchantId) {
        Prefs.putString(PAYMENT_KOUNT_MERCHANT_ID, kountMerchantId);
    }

    @Override
    public String getKountMerchantId() {
        return Prefs.getString(PAYMENT_KOUNT_MERCHANT_ID, "");
    }

    @Override
    public void setSearchMaxPrice(int searchMaxPrice) {
        Prefs.putInt(SEARCH_MAX_PRICE, searchMaxPrice);
    }

    @Override
    public int getSearchMaxPrice() {
        return Prefs.getInt(SEARCH_MAX_PRICE, 0);
    }

    @Override
    public void setAccessAnonymousEnabled(boolean accessAnonymousEnabled) {
        Prefs.putBoolean(ACCESS_ANONYMOUS_ENABLED, accessAnonymousEnabled);
    }

    @Override
    public boolean getAccessAnonymousEnabled() {
        return Prefs.getBoolean(ACCESS_ANONYMOUS_ENABLED, false);
    }

    @Override
    public void setFbSecret(String fbSecret) {
        Prefs.putString(FB_SECRET, fbSecret);
    }

    @Override
    public String getFbSecret() {
        return Prefs.getString(FB_SECRET, mContext.getResources().getString(R.string.facebook_app_secret));
    }

    @Override
    public boolean isDebugMode() {
        return Prefs.getBoolean(DEBUG_MODE, true);
    }

    @Override
    public void setPaymentCount(int count) {
        Prefs.putInt(PAYMENT_COUNT, count);
    }

    @Override
    public int getPaymentCount() {
        return Prefs.getInt(PAYMENT_COUNT, 0);
    }

    @Override
    public void setGCMRegistrationId(String registrationId) {
        Prefs.putString(GNotification.PROPERTY_REG_ID, registrationId);
    }

    @Override
    public String getGCMRegistrationId() {
        return Prefs.getString(GNotification.PROPERTY_REG_ID, "");
    }

    @Override
    public void setGCMAppVersion(int appVersion) {
        Prefs.putInt(GNotification.PROPERTY_APP_VERSION, appVersion);
    }

    @Override
    public int getGCMAppVersion() {
        return Prefs.getInt(GNotification.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
    }

    @Override
    public void setIsVisaCheckoutEnabled(boolean isVisaCheckoutEnabled) {
        Prefs.putBoolean(VCO_ENABLED, isVisaCheckoutEnabled);
    }

    @Override
    public boolean getIsVisaCheckoutEnabled() {
        return Prefs.getBoolean(VCO_ENABLED, false);
    }

    @Override
    public void setVisaCheckoutApiKey(String visaCheckoutApiKey) {
        Prefs.putString(VCO_APIKEY, visaCheckoutApiKey);
    }

    @Override
    public String getVisaCheckoutApiKey() {
        return Prefs.getString(VCO_APIKEY, "");
    }

    @Override
    public void setVisaCheckoutApiUrl(String visaCheckoutApiUrl) {
        Prefs.putString(VCO_APIURL, visaCheckoutApiUrl);
    }

    @Override
    public String getVisaCheckoutApiUrl() {
        return Prefs.getString(VCO_APIURL, "");
    }

    @Override
    public void setVisaCheckoutProviderType(int visaCheckoutProviderType) {
        Prefs.putInt(VCO_PROVIDERTYPE, visaCheckoutProviderType);
    }

    @Override
    public int getVisaCheckoutProviderType() {
        return Prefs.getInt(VCO_PROVIDERTYPE, -1);
    }

    @Override
    public void setPersonalisationTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue value) {
        Prefs.putString(PERSONALISATION_VALIDATION, value.getPersonalisationValidation());
    }

    @Override
    public String getPersonalisationTemplateTexts() {
        return Prefs.getString(PERSONALISATION_VALIDATION, "");
    }

    @Override
    public void setConsentTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue value) {
        Prefs.putString(CONSENT_CONTINUE_TEXT, value.getConsentContinueText());
        Prefs.putString(CONSENT_FULL_TEMPLATE_TEXT, value.getConsentFullTextPTNameV1());
        Prefs.putString(CONSENT_SHORT_TEMPLATE_TEXT, value.getConsentShortTextPTNameV1());
        Prefs.putString(CONSENT_TERMS_AND_CONDITION, value.getTermsAndConditionsText());
        Prefs.putString(CONSENT_WITH_REGISTRATION_TERMS_TEXT, value.getConsentWithTCText());
        Prefs.putString(CONSENT_WITH_REGISTRATION_EMAILS_TEXT, value.getConsentWithEmailsText());
        Prefs.putString(CONSENT_WITH_REGISTRATION_TERMS_WARNING, value.getConsentWithRegistrationTermsWarning());
    }

    @Override
    public String getConsentTemplateTexts(String key) {
        return Prefs.getString(key, "");
    }

    @Override
    public void setAppSettingsConsent(GetAppSettingsConsent.ResponseValue value) {
        Prefs.putString(CONSENT_SHORT_TEXT, value.getShortTextPTName());
        Prefs.putString(CONSENT_FULL_TEXT, value.getFullTextPTName());
        Prefs.putInt(CONSENT_MODE, value.getMode());
        Prefs.putBoolean(CONSENT_TNC_CHECKED, value.isRegAgreementTermsAndConditionCheckboxTicked());
        Prefs.putBoolean(CONSENT_EMAILS_CHECKED, value.isRegAgreementReceiveEmailsCheckboxTicked());
    }

    @Override
    public String getAppSettingsConsentText(String key) {
        return Prefs.getString(key, "");
    }

    @Override
    public int getAppSettingsConsentMode() {
        return Prefs.getInt(CONSENT_MODE, -1);
    }

    @Override
    public boolean getAppSettingsConsentIsChecked(String key) {
        return Prefs.getBoolean(key, false);
    }

    @Override
    public void setDeliveryOptionsTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue value) {
        Prefs.putString(KEY_DELIVERYOPTION_EXPRESS_TITLE, value.getDeliveryOptionExpressTitle());
        Prefs.putString(KEY_DELIVERYOPTION_EXPRESS_DESCRIPTION, value.getDeliveryOptionExpressDescription());
        Prefs.putString(KEY_DELIVERYOPTION_STANDARD_TITLE, value.getDeliveryOptionStandardTitle());
    }

    @Override
    public String getStoredTemplateTexts(String detailKey) {
        return Prefs.getString(detailKey, "");
    }

    @Override
    public void setIsInitialLaunch(boolean isInitialLaunch) {
        Prefs.putBoolean(IS_INITIAL_LAUNCH, isInitialLaunch);
    }

    @Override
    public boolean getIsInitialLaunch() {
        return Prefs.getBoolean(IS_INITIAL_LAUNCH, true);
    }

    @Override
    public void setIsMultiLanguage(boolean isMultiLanguage) {
        Prefs.putBoolean(IS_MULTI_LANGUAGE, isMultiLanguage);
    }

    //set default multi language to true temporarily
    @Override
    public boolean getIsMultiLanguage() {
        return Prefs.getBoolean(IS_MULTI_LANGUAGE, true);
    }

    @Override
    public void setIsMultiCountry(boolean isMultiCountry) {
        Prefs.putBoolean(IS_MULTI_COUNTRY, isMultiCountry);
    }

    @Override
    public boolean getIsMultiCountry() {
        return Prefs.getBoolean(IS_MULTI_COUNTRY, false);
    }

    @Override
    public void setPublicPaymentToken(String publicPaymentToken) {
        Prefs.putString(PUBLIC_PAYMENT_TOKEN, publicPaymentToken);
    }

    @Override
    public String getPublicPaymentToken() {
        return Prefs.getString(PUBLIC_PAYMENT_TOKEN, "");
    }

    @Override
    public void setCurrentPaymentToken(String paymentToken) {
        Prefs.putString(CURRENT_PAYMENT_TOKEN, paymentToken);
    }

    @Override
    public String getCurrentPaymentToken() {
        return Prefs.getString(CURRENT_PAYMENT_TOKEN, "");
    }

    @Override
    public void setPublicPaymentType(String publicPaymentType) {
        Prefs.putString(PUBLIC_PAYMENT_TYPE, publicPaymentType);
    }

    @Override
    public String getPublicPaymentType() {
        return Prefs.getString(PUBLIC_PAYMENT_TYPE, "");
    }

    @Override
    public void setCurrentPaymentType(String paymentType) {
        Prefs.putString(CURRENT_PAYMENT_TYPE, paymentType);
    }

    @Override
    public String getCurrentPaymentType() {
        return Prefs.getString(CURRENT_PAYMENT_TYPE, "");
    }

    @Override
    public void setIsNotificationsEnabled(boolean isNotificationsEnabled) {
        Prefs.putBoolean(KEY_NOTIFICATIONS_ENABLED, isNotificationsEnabled);
    }

    @Override
    public boolean getIsNotificationsEnabled() {
        return Prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    @Override
    public void setIsPaypalCreditEnabled(boolean isPaypalCreditEnabled) {
        Prefs.putBoolean(PAYMENT_PAYPAL_CREDIT_ENABLED, isPaypalCreditEnabled);
    }

    @Override
    public boolean isPaypalCreditEnabled() {
        return Prefs.getBoolean(PAYMENT_PAYPAL_CREDIT_ENABLED, false);
    }

    @Override
    public void setIsSortingEnabled(boolean isSortingEnabled) {
        Prefs.putBoolean(ACCOUNT_DATA_SORTING, isSortingEnabled);
    }

    @Override
    public boolean getIsSortingEnabled() {
        return Prefs.getBoolean(ACCOUNT_DATA_SORTING, true);
    }

    @Override
    public void setLastRedirection(String lastRedirection) {
        Prefs.putString(LAST_REDIRECTION, lastRedirection);
    }

    @Override
    public String getLastRedirection() {
        return Prefs.getString(LAST_REDIRECTION, "");
    }

    @Override
    public void setIsNewUser(boolean isNewUser) {
        Prefs.putBoolean(IS_NEW_USER, isNewUser);
    }

    @Override
    public boolean getIsNewUser() {
        return Prefs.getBoolean(IS_NEW_USER, false);
    }

    @Override
    public void setHasActiveCheckoutSession(boolean hasActiveCheckoutSession) {
        Prefs.putBoolean(HAS_ACTIVE_CHECKOUT_SESSION, hasActiveCheckoutSession);
    }

    @Override
    public boolean hasActiveCheckoutSession() {
        return Prefs.getBoolean(HAS_ACTIVE_CHECKOUT_SESSION, false);
    }

    @Override
    public void setCartHashCode(int hashCode) {
        Prefs.putInt(CART_HASH_CODE, hashCode);
    }

    @Override
    public int getCartHashCode() {
        return Prefs.getInt(CART_HASH_CODE, 0);
    }

    @Override
    public void resetAddToCartJourneyFlags() {
        setHasViewedSale(false);
        setHasViewedProductCategory(false);
        setHasViewedProduct(false);
        setHasAddedToCart(false);
        setHasViewedCart(false);
    }

    @Override
    public void setHasViewedSale(boolean hasViewedSale) {
        Prefs.putBoolean(HAS_VIEWED_SALE, hasViewedSale);
    }

    @Override
    public boolean hasViewedSale() {
        return Prefs.getBoolean(HAS_VIEWED_SALE, false);
    }

    @Override
    public void setHasViewedProductCategory(boolean hasViewedProductCategory) {
        Prefs.putBoolean(HAS_VIEWED_PRODUCT_CATEGORY, hasViewedProductCategory);
    }

    @Override
    public boolean hasViewedProductCategory() {
        return Prefs.getBoolean(HAS_VIEWED_PRODUCT_CATEGORY, false);
    }

    @Override
    public void setHasViewedProduct(boolean hasViewedProduct) {
        Prefs.putBoolean(HAS_VIEWED_PRODUCT, hasViewedProduct);
    }

    @Override
    public boolean hasViewedProduct() {
        return Prefs.getBoolean(HAS_VIEWED_PRODUCT, false);
    }

    @Override
    public void setHasAddedToCart(boolean hasAddedToCart) {
        Prefs.putBoolean(HAS_ADDED_TO_CART, hasAddedToCart);
    }

    @Override
    public boolean hasAddedToCart() {
        return Prefs.getBoolean(HAS_ADDED_TO_CART, false);
    }

    @Override
    public void setHasViewedCart(boolean hasViewedCart) {
        Prefs.putBoolean(HAS_VIEWED_CART, hasViewedCart);
    }

    @Override
    public boolean hasViewedCart() {
        return Prefs.getBoolean(HAS_VIEWED_CART, false);
    }

    @Override
    public void setUserHasRateApp(boolean userHasRateApp) {
        Prefs.putBoolean(USER_HAS_RATE_APP, userHasRateApp);
    }

    @Override
    public boolean userHasRateApp() {
        return Prefs.getBoolean(USER_HAS_RATE_APP, false);
    }

    @Override
    public void setShouldShowStrictConsent(boolean shouldShowStrictConsent) {
        Prefs.putBoolean(SHOW_STRICT_CONSENT, shouldShowStrictConsent);
    }

    @Override
    public boolean shouldShowStrictConsent() {
        return Prefs.getBoolean(SHOW_STRICT_CONSENT, true);
    }

    public void setEventUserId(String userId) {
        Prefs.putString(EVENT_USER_ID, userId);
    }

    @Override
    public String getEventUserId() {
        return Prefs.getString(EVENT_USER_ID, "");
    }

    @Override
    public HashSet<String> getCookies() {
        return (HashSet<String>) Prefs.getStringSet(COOKIES, new HashSet<>());
    }

    @Override
    public void setShippingByPostcodeEnabled(boolean enabled) {
        Prefs.putBoolean(SHIPPING_BY_POSTCODE_ENABLED, enabled);
    }

    @Override
    public boolean getShippingByPostcodeEnabled() {
        return Prefs.getBoolean(SHIPPING_BY_POSTCODE_ENABLED, false);
    }

    @Override
    public int getMobilePhoneBannerColumns() {
        return Prefs.getInt(PHONE_BANNER_COLUMNS, -1);
    }

    @Override
    public void setMobilePhoneBannerColumns(Integer columns) {
        if (columns == null) {
            Prefs.remove(PHONE_BANNER_COLUMNS);
        } else {
            Prefs.putInt(PHONE_BANNER_COLUMNS, columns);
        }
    }

    @Override
    public int getMobileTabletBannerColumns() {
        return Prefs.getInt(TABLET_BANNER_COLUMNS, -1);
    }

    @Override
    public void setMobileTabletBannerColumns(Integer columns) {
        if (columns == null) {
            Prefs.remove(TABLET_BANNER_COLUMNS);
        } else {
            Prefs.putInt(TABLET_BANNER_COLUMNS, columns);
        }
    }

    @Override
    public void setReCaptchaSiteKey(String key) {
        Prefs.putString(RECAPTCHA_SITE_KEY, key);
    }

    @Override
    public String getReCaptchaSiteKey() {
        return Prefs.getString(RECAPTCHA_SITE_KEY, "");
    }

    @Override
    public void setIsGoogleAdsEnabled(boolean isGoogleAdsEnabled) {
        Prefs.putBoolean(GOOGLE_ADS, isGoogleAdsEnabled);
    }

    @Override
    public boolean isGoogleAdsEnabled() {
        return Prefs.getBoolean(GOOGLE_ADS, true);
    }

    @Override
    public void setIsColorFilterEnabled(boolean isColorFilterEnabled) {
        Prefs.putBoolean(COLOR_FILTER, isColorFilterEnabled);
    }

    @Override
    public boolean isColorFilterEnabled() {
        return Prefs.getBoolean(COLOR_FILTER, false);
    }

    @Override
    public void setLastColumnSelected(int columnCount) {
        Prefs.putInt(COLUMN_COUNT, columnCount);
    }

    @Override
    public int getLastColumnSelected() {
        return Prefs.getInt(COLUMN_COUNT, 3);
    }

    public void setLastTimeStamp(String timeStamp) {
        Prefs.putString(LAST_TIME_STAMP, timeStamp);
    }

    @Override
    public String getLastTimeStamp() {
        return Prefs.getString(LAST_TIME_STAMP, "");
    }

    @Override
    public void setVoucherStatusTemplateText(GetTemplateTextsResponse.GetTemplateTextsValue value) {
        Prefs.putString(VOUCHER_NEW, value.getVoucherNew());
        Prefs.putString(VOUCHER_ALREADY_SPENT, value.getVoucherAlreadySpent());
        Prefs.putString(VOUCHER_EXPIRING_SOON, value.getVoucherExpiringSoon());
        Prefs.putString(VOUCHER_EXPIRED, value.getVoucherExpired());
        Prefs.putString(VOUCHER_PENDING, value.getVoucherPending());
    }

    @Override
    public String getVoucherStatusTemplateText(GetUserVoucherResponse.Status status) {
        switch (status) {
            case NEW:
                return Prefs.getString(VOUCHER_NEW, "");
            case ALREADY_SPENT:
                return Prefs.getString(VOUCHER_ALREADY_SPENT, "");
            case EXPIRING_SOON:
                return Prefs.getString(VOUCHER_EXPIRING_SOON, "");
            case EXPIRED:
                return Prefs.getString(VOUCHER_EXPIRED, "");
            case PENDING:
                return Prefs.getString(VOUCHER_PENDING, "");
        }
        return "";
    }

    @Override
    public String getShippingHover() {
        return Prefs.getString(SHIPPING_HOVER, "");
    }

    @Override
    public void setShippingHover(GetTemplateTextsResponse.GetTemplateTextsValue value) {
        Prefs.putString(SHIPPING_HOVER, value.getShippingRulesHover());
        Prefs.putString(SHIPPING_TITLE, value.getShippingRulesHoverTitle());
    }

    @Override
    public String getShippingTitle() {
        return Prefs.getString(SHIPPING_TITLE, "");
    }

    @Override
    public int getFileSizeLimit() {
        return Prefs.getInt(FILE_SIZE_LIMIT, 1);
    }

    @Override
    public void setFileSizeLimit(int fileSizeLimit) {
        Prefs.putInt(FILE_SIZE_LIMIT, fileSizeLimit);
    }

    @Override
    public boolean hasWishlistBeenAccessed() {
        return Prefs.getBoolean(HAS_WISHLIST_BEEN_ACCESSED, false);
    }

    @Override
    public void setHasWishlistBeenAccessed(boolean isAccessed) {
        Prefs.putBoolean(HAS_WISHLIST_BEEN_ACCESSED, isAccessed);
    }

    @Override
    public String getStripePublicKey() {
        return Prefs.getString(STRIPE_PUBLIC_KEY, "");
    }

    @Override
    public void setStripePublicKey(String stripePublicKey) {
        Prefs.putString(STRIPE_PUBLIC_KEY, stripePublicKey);
    }

    @Override
    public boolean isStripeEnabled() {
        return Prefs.getBoolean(STRIPE_ENABLED, false);
    }

    @Override
    public void setStripeEnabled(boolean stripeEnabled) {
        Prefs.putBoolean(STRIPE_ENABLED, stripeEnabled);
    }

    @Override
    public String getStripePaymentMethodId() {
        return Prefs.getString(STRIPE_PAYMENT_METHOD_ID, "");
    }

    @Override
    public void setStripePaymentMethodId(String paymentMethodId) {
        Prefs.putString(STRIPE_PAYMENT_METHOD_ID, paymentMethodId);
    }

    @Override
    public void setPrefersOldShopBannerDimensions(boolean doesPrefer) {
        Prefs.putBoolean(PREFERS_OLD_SHOP_BANNER_DIMENSIONS, doesPrefer);
    }

    @Override
    public boolean getPrefersOldShopBannersDimensions() {
        return Prefs.getBoolean(PREFERS_OLD_SHOP_BANNER_DIMENSIONS, false);
    }

    @Override
    public void setDefaultPostcode(String postcode) {
        Prefs.putString(DEFAULT_POSTCODE, postcode);
    }

    @Override
    public String getDefaultPostcode() {
        return Prefs.getString(DEFAULT_POSTCODE, "");
    }

    @Override
    public long getSupplierOriginalPriceInfoSaleListTimeAgreed() {
        return Prefs.getLong(SUPPLIER_ORIGINAL_PRICE_INFO_SALE_LIST, -1);
    }

    @Override
    public void setIsSupplierOriginalPriceInfoSaleListTimeAgreed(long timestamp) {
        Prefs.putLong(SUPPLIER_ORIGINAL_PRICE_INFO_SALE_LIST, timestamp);
    }

    @Override
    public void setSupplierOriginalPriceInfoTemplateTextType1(String text) {
        Prefs.putString(SUPPLIER_ORIGINAL_PRICE_INFO_TEMPLATE_TEXT_1, text);
    }

    @Override
    public String getSupplierOriginalPriceInfoTemplateTextType1() {
        return Prefs.getString(SUPPLIER_ORIGINAL_PRICE_INFO_TEMPLATE_TEXT_1, "");
    }

    @Override
    public void setSupplierOriginalPriceInfoTemplateTextType2(String text) {
        Prefs.putString(SUPPLIER_ORIGINAL_PRICE_INFO_TEMPLATE_TEXT_2, text);

    }

    @Override
    public String getSupplierOriginalPriceInfoTemplateTextType2() {
        return Prefs.getString(SUPPLIER_ORIGINAL_PRICE_INFO_TEMPLATE_TEXT_2, "");
    }

    @Override
    public void setSupplierOriginalPriceInfoEnabled(boolean isEnabled) {
        Prefs.putBoolean(SUPPLIER_ORIGINAL_PRICE_INFO_ENABLED, isEnabled);
    }

    @Override
    public boolean isSupplierOriginalPriceInfoEnabled() {
        return Prefs.getBoolean(SUPPLIER_ORIGINAL_PRICE_INFO_ENABLED, false);
    }

    @Override
    public void setBuyBoxTemplateTextTitle(String title) {
        Prefs.putString(BUYBOX_TITLE, title);
    }

    @Override
    public String getBuyBoxTemplateTextTitle() {
        return Prefs.getString(BUYBOX_TITLE, "");
    }

    @Override
    public void setBuyBoxTemplateTextSellerTemplate(String sellerTemplate) {
        Prefs.putString(BUYBOX_SELLER_TEMPLATE, sellerTemplate);
    }

    @Override
    public String getBuyBoxTemplateTextSellerTemplate() {
        return Prefs.getString(BUYBOX_SELLER_TEMPLATE, "");
    }

    @Override
    public void setBuyBoxTemplateTextButtonText(String bottomText) {
        Prefs.putString(BUYBOX_BUTTON_TEXT, bottomText);
    }

    @Override
    public String getBuyBoxTemplateTextBottomText() {
        return Prefs.getString(BUYBOX_BUTTON_TEXT, "");
    }

    @Override
    public void setKlarnaEnabled(boolean enabled) {
        Prefs.putBoolean(KLARNA_ENABLED, enabled);
    }

    @Override
    public boolean isKlarnaEnabled() {
        return Prefs.getBoolean(KLARNA_ENABLED, false);
    }

    @Override
    public void setZipPayEnabled(boolean enabled) {
        Prefs.putBoolean(ZIP_PAY_ENABLED, enabled);
    }

    @Override
    public boolean isZipPayEnabled() {
        return Prefs.getBoolean(ZIP_PAY_ENABLED, false);
    }

    @Override
    public void setProductPagePriceBlockMode(int mode) {
        Prefs.putInt(PRODUCT_PAGE_PRICE_BLOCK_MODE, mode);
    }

    @Override
    public int getProductPagePriceBlockMode() {
        return Prefs.getInt(PRODUCT_PAGE_PRICE_BLOCK_MODE, 1);
    }

    @Override
    public void setHoursLeftToDisplayTimer(int value) {
        Prefs.putInt(HOURS_LEFT_TO_DISPLAY_TIMER, value);
    }

    @Override
    public int getHoursLeftToDisplayTimer() {
        return Prefs.getInt(HOURS_LEFT_TO_DISPLAY_TIMER, 48);
    }
}
