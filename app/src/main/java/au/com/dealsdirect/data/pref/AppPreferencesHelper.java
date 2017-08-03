package au.com.dealsdirect.data.pref;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Build;

import com.mysale.genie.utility.Prefs;

import javax.inject.Inject;
import javax.inject.Singleton;

import au.com.dealsdirect.R;
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


    private static final String DEBUG_MODE = "app_debug_mode";
    private static final String COUNTRY_ID = "server_country_id";
    private static final String LANGUAGE_ID = "server_language_id";
    private static final String LANGUAGE_LIST = "server_language_list";
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
    private static final String SEARCH_MAX_PRICE = "app_search_max_price";
    private static final String ACCESS_ANONYMOUS_ENABLED = "app_anonymous_enabled";
    private static final String FB_SECRET = "fb_secret";
    private static final String PAYMENT_COUNT = "payment_count";

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
        return Prefs.getString(PREF_KEY_USER_AGENT,"");
    }

    @Override
    public void setCountryId(String countryId) {
        Prefs.putString(COUNTRY_ID,countryId);
    }

    @Override
    public String getCountryId() {
        return Prefs.getString(COUNTRY_ID, "");
    }

    @Override
    public void setLanguageId(String languageId) {
        Prefs.putString(LANGUAGE_ID,languageId);
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
        Prefs.putString(SITE_NAME,siteName);
    }

    @Override
    public String getSiteName() {
        return Prefs.getString(SITE_NAME,"");
    }

    @Override
    public void setCurrency(String currency) {
        Prefs.putString(CURRENCY,currency);
    }

    @Override
    public String getCurrency() {
        return Prefs.getString(CURRENCY, "");
    }

    @Override
    public void setCurrencySign(String currencySign) {
        Prefs.putString(CURRENCY_SIGN,currencySign);
    }

    @Override
    public String getCurrencySign() {
        return Prefs.getString(CURRENCY_SIGN, "");
    }

    @Override
    public void setFollowUsFbLink(String followUsFbLink) {
        Prefs.putString(FOLLOW_US_LINK_FB,followUsFbLink);
    }

    @Override
    public String getFollowUsFbLink() {
        String FACEBOOK_URL = Prefs.getString(FOLLOW_US_LINK_FB, "");
        String FACEBOOK_PAGE_ID = "47143367223";      //  http://findmyfbid.com/
        try {
            mContext.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return "fb://page/" + FACEBOOK_PAGE_ID;
        } catch (Exception e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    @Override
    public void setFollowUsTwitterLink(String followUsTwitterLink) {
        Prefs.putString(FOLLOW_US_LINK_TWITTER,followUsTwitterLink);
    }

    @Override
    public String getFollowUsTwitterLink() {
        return Prefs.getString(FOLLOW_US_LINK_TWITTER, "");
    }

    @Override
    public void setImageServerUrl(String imageServerUrl) {
        Prefs.putString(IMAGE_SERVER_URL,imageServerUrl);
    }

    @Override
    public String getImageServerUrl() {
        return Prefs.getString(IMAGE_SERVER_URL,"");
    }

    @Override
    public void setIsPaypalEnabled(boolean val) {
        Prefs.putBoolean(PAYMENT_PAYPAL_ENABLED,val);
    }

    @Override
    public boolean isPaypalEnabled() {
        return Prefs.getBoolean(PAYMENT_PAYPAL_ENABLED, false);
    }

    @Override
    public void setIsMasterpassEnabled(boolean val) {
        Prefs.putBoolean(PAYMENT_MASTERPASS_ENABLED,val);
    }

    @Override
    public boolean isMasterpassEnabled() {
        return Prefs.getBoolean(PAYMENT_MASTERPASS_ENABLED, false);
    }

    @Override
    public void setIsAmexEnabled(boolean val) {
        Prefs.putBoolean(PAYMENT_AMEX_ENABLED,val);
    }

    @Override
    public boolean isAmexEnabled() {
        return Prefs.getBoolean(PAYMENT_AMEX_ENABLED, false);
    }

    @Override
    public void setIsKountEnabled(boolean val) {
        Prefs.putBoolean(PAYMENT_KOUNT_ENABLED,val);
    }

    @Override
    public boolean isKountEnabled() {
        return Prefs.getBoolean(PAYMENT_KOUNT_ENABLED, false);
    }

    @Override
    public void setKountMerchantId(String kountMerchantId) {
        Prefs.putString(PAYMENT_KOUNT_MERCHANT_ID,kountMerchantId);
    }

    @Override
    public String getKountMerchantId() {
        return Prefs.getString(PAYMENT_KOUNT_MERCHANT_ID, "");
    }

    @Override
    public void setSearchMaxPrice(int searchMaxPrice) {
        Prefs.putInt(SEARCH_MAX_PRICE,searchMaxPrice);
    }

    @Override
    public int getSearchMaxPrice() {
        return Prefs.getInt(SEARCH_MAX_PRICE,0);
    }

    @Override
    public void setAccessAnonymousEnabled(boolean accessAnonymousEnabled) {
        Prefs.putBoolean(ACCESS_ANONYMOUS_ENABLED,accessAnonymousEnabled);
    }

    @Override
    public boolean getAccessAnonymousEnabled() {
        return Prefs.getBoolean(ACCESS_ANONYMOUS_ENABLED,false);
    }

    @Override
    public void setFbSecret(String fbSecret) {
        Prefs.putString(FB_SECRET,fbSecret);
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
        Prefs.putInt(PAYMENT_COUNT,count);
    }

    @Override
    public int getPaymentCount() {
        return Prefs.getInt(PAYMENT_COUNT,0);
    }

    @Override
    public void setGCMRegistrationId(String registrationId) {
        Prefs.putString(GNotification.PROPERTY_REG_ID, registrationId);
    }

    @Override
    public String getGCMRegistrationId() {
        return Prefs.getString(GNotification.PROPERTY_REG_ID,"");
    }

    @Override
    public void setGCMAppVersion(int appVersion) {
        Prefs.putInt(GNotification.PROPERTY_APP_VERSION, appVersion);
    }

    @Override
    public int getGCMAppVersion() {
        return Prefs.getInt(GNotification.PROPERTY_APP_VERSION,Integer.MIN_VALUE);
    }

}
