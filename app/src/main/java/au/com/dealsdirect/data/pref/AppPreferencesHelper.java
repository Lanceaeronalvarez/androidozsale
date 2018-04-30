package au.com.dealsdirect.data.pref;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.mysale.genie.utility.Prefs;

import java.util.HashSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;
import au.com.dealsdirect.di.ApplicationContext;
import au.com.dealsdirect.di.PreferenceInfo;
import au.com.dealsdirect.service.fcm.GNotification;


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
    private static final String LANGUAGE_ID = "server_language_id";
    private static final String LANGUAGE_LIST = "server_language_list";
    private static final String IS_MULTI_LANGUAGE = "server_multi_language";
    private static final String IS_MULTI_COUNTRY = "server_multi_country";
    private static final String SITE_NAME = "server_site_name";
    private static final String CURRENCY = "server_currency";
    private static final String CURRENCY_SIGN = "server_currency_sign";
    private static final String FOLLOW_US_LINK_FB = "server_follow_us_fb";
    private static final String FOLLOW_US_LINK_TWITTER = "server_follow_us_twitter";
    private static final String IMAGE_SERVER_URL = "server_image_server_url";

    private static final String PAYMENT_PAYPAL_ENABLED = "app_paypal_enabled";
    private static final String PAYMENT_MASTERPASS_ENABLED = "app_masterpass_enabled";
    private static final String PAYMENT_AMEX_ENABLED = "app_amex_enabled";
    private static final String PAYMENT_KOUNT_ENABLED = "app_kount_enabled";
    private static final String PAYMENT_KOUNT_MERCHANT_ID = "app_kount_merchant_id";
    private static final String PAYMENT_MYPAY_ENABLED = "app_mypay_enabled";


    private static final String PUBLIC_PAYMENT_TOKEN = "PUBLIC_PAYMENT_TOKEN";
    private static final String PUBLIC_PAYMENT_TYPE = "PUBLIC_PAYMENT_TYPE";

    /* mypay */
    private static final String PAYMENT_MYPAY_TEMPLATE_TEXTS_KEY = "settings_mypay_template_texts";
    private static final String MYPAY_EXCEED_LIMIT = "_checkoutMyPayPayExceedLimit";
    private static final String MYPAY_INVALID_PAYMENT_METHOD = "_checkoutMyPayPayInvalidPaymentMethod";
    private static final String MYPAY_OUT_OF_RANGE = "_checkoutMyPayPayOutOfRangeMobileApp";
    private static final String MYPAY_OUT_UP_TO_MOBILE_UP = "_checkoutMyPayPayOutUpToMobileApp";
    private static final String MYPAY_UNTRUSTED = "_checkoutMyPayPayUntrusted";
    private static final String MYPAY_DETAILS = "myPayDetailsMobileApp";
    private static final String MYPAY_THANKYOU_TEXT = "_OurPayThankYouTextMobileApp";
    private static final String MYPAY_TC = "_OurPayTC_text";
    private static final String MYPAY_TC_VALIDATION_FAILED = "_OurPayTCValidationFailed";
    private static final String MYPAY_PAYMENT_SCHEDULE = "_PaymentSchedule";

    /*VISA CHECKOUT*/

    private static final String VCO_ENABLED = "VCO_ENABLED";
    private static final String VCO_APIKEY = "VCO_APIKEY";
    private static final String VCO_APIURL = "VCO_APIURL";
    private static final String VCO_PROVIDERTYPE = "VCO_PROVIDERTYPE";
    private static final String SEARCH_MAX_PRICE = "app_search_max_price";
    private static final String ACCESS_ANONYMOUS_ENABLED = "app_anonymous_enabled";
    private static final String FB_SECRET = "fb_secret";
    private static final String PAYMENT_COUNT = "payment_count";

    private static final String EVENT_USER_ID = "event_user_id";
    private static final String COOKIES = "network_cookies";


    private Context mContext;

    @Inject
    public AppPreferencesHelper(@ApplicationContext Context context,
                                @PreferenceInfo String prefFileName) {
        mContext = context;
        new Prefs.Builder()
                .setContext(context)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(prefFileName)
                .setUseDefaultSharedPreference(true)
                .build();

        //Set default settings here
        Prefs.putString(FB_SECRET, context.getResources().getString(R.string.facebook_app_secret));
        Prefs.putString(COUNTRY_ID, context.getResources().getString(R.string.default_country_id));
        Prefs.putString(LANGUAGE_ID, context.getResources().getString(R.string.default_language_id));
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
        return Prefs.getString(COUNTRY_ID, "");
    }

    @Override
    public void setLanguageId(String languageId) {
        Prefs.putString(LANGUAGE_ID, languageId);
    }

    @Override
    public String getLanguageId() {
        return Prefs.getString(LANGUAGE_ID, "");
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
        return Prefs.getString(FB_SECRET, "");
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
    public void setIsMyPayEnabled(boolean isMyPayEnabled) {
        Prefs.putBoolean(PAYMENT_MYPAY_ENABLED, isMyPayEnabled);
    }

    @Override
    public boolean getIsMyPayEnabled() {
        return Prefs.getBoolean(PAYMENT_MYPAY_ENABLED, true);
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
    public void setMyPayTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue value) {
        Log.d("Template", "= " + value.getOurPayTCValidationFailed() + " , " + value.getCheckoutMyPayPayInvalidPaymentMethod() + " , " + value.getMyPayDetailsMobileApp());
        Prefs.putString(MYPAY_EXCEED_LIMIT, value.getCheckoutMyPayPayExceedLimit());
        Prefs.putString(MYPAY_INVALID_PAYMENT_METHOD, value.getCheckoutMyPayPayInvalidPaymentMethod());
        Prefs.putString(MYPAY_OUT_OF_RANGE, value.getCheckoutMyPayPayOutOfRangeMobileApp());
        Prefs.putString(MYPAY_OUT_UP_TO_MOBILE_UP, value.getCheckoutMyPayPayOutUpToMobileApp());
        Prefs.putString(MYPAY_UNTRUSTED, value.getCheckoutMyPayPayUntrusted());
        Prefs.putString(MYPAY_DETAILS, value.getMyPayDetailsMobileApp());
        Prefs.putString(MYPAY_THANKYOU_TEXT, value.getOurPayThankYouTextMobileApp());
        Prefs.putString(MYPAY_TC, value.getOurPayTC_text());
        Prefs.putString(MYPAY_TC_VALIDATION_FAILED, value.getOurPayTCValidationFailed());
        Prefs.putString(MYPAY_PAYMENT_SCHEDULE, value.getPaymentSchedule());
    }

    @Override
    public String getMyPayTemplateTexts(String detailKey) {
        Log.d("Template", Prefs.getString(detailKey, ""));

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
    public void setPublicPaymentType(String publicPaymentType) {
        Prefs.putString(PUBLIC_PAYMENT_TYPE, publicPaymentType);
    }

    @Override
    public String getPublicPaymentType() {
        return Prefs.getString(PUBLIC_PAYMENT_TYPE,"");
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

}
