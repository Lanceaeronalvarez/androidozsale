package au.com.dealsdirect.data.pref;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import au.com.dealsdirect.di.ApplicationContext;
import au.com.dealsdirect.di.PreferenceInfo;


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

    private Context mContext;

    @Inject
    public AppPreferencesHelper(@ApplicationContext Context context,
                                @PreferenceInfo String prefFileName) {
        mContext = context;
//        new Prefs.Builder()
//                .setContext(context)
//                .setMode(ContextWrapper.MODE_PRIVATE)
//                .setPrefsName(prefFileName)
//                .setUseDefaultSharedPreference(true)
//                .build();
//
//        //Set default settings here
//        Prefs.putString(FB_SECRET, context.getResources().getString(R.string.facebook_app_secret));
//        Prefs.putString(COUNTRY_ID, context.getResources().getString(R.string.default_country_id));
//        Prefs.putString(LANGUAGE_ID, context.getResources().getString(R.string.default_language_id));
//        Prefs.putBoolean(DEBUG_MODE, context.getResources().getBoolean(R.bool.debug_mode));

    }

    //Elv - Override exposed methods from Preference Helper here

//    @Override
//    public int getCurrentUserLoggedInMode() {
////        return mPrefs.getInt(PREF_KEY_USER_LOGGED_IN_MODE,
////                DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT.getType());
//
//        return 0;
//    }
//
//    @Override
//    public String getCountryId() {
//        return Prefs.getString(COUNTRY_ID, "");
//    }
//
//    @Override
//    public String getLanguageId() {
//        return Prefs.getString(LANGUAGE_ID, "");
//    }
//
//    @Override
//    public List<Language> getLanguages() {
//        List<Language> languageList = new ArrayList<>();
//        return Prefs.getObject(LANGUAGE_LIST, languageList.getClass());
//    }
//
//    @Override
//    public String getCurrency() {
//        return Prefs.getString(CURRENCY, "");
//    }
//
//    @Override
//    public String getCurrencySign() {
//        return Prefs.getString(CURRENCY_SIGN, "");
//    }
//
//    @Override
//    public String getFollowUsTwitterLink() {
//        return Prefs.getString(FOLLOW_US_LINK_TWITTER, "");
//    }
//
//    @Override
//    public String getFollowUsFbLink() {
//        String FACEBOOK_URL = Prefs.getString(FOLLOW_US_LINK_FB, "");
//        String FACEBOOK_PAGE_ID = "47143367223";      //  http://findmyfbid.com/
//        try {
//            mContext.getPackageManager().getPackageInfo("com.facebook.katana", 0);
//            return "fb://page/" + FACEBOOK_PAGE_ID;
//        } catch (Exception e) {
//            return FACEBOOK_URL; //normal web url
//        }
//    }
//
//    @Override
//    public boolean isPaypalEnabled() {
//        return Prefs.getBoolean(PAYMENT_PAYPAL_ENABLED, false);
//    }
//
//    @Override
//    public boolean isAmexEnabled() {
//        return Prefs.getBoolean(PAYMENT_AMEX_ENABLED, false);
//    }
//
//    @Override
//    public boolean isMasterpassEnabled() {
//        return Prefs.getBoolean(PAYMENT_MASTERPASS_ENABLED, false);
//    }
//
//    @Override
//    public boolean isKountEnabled() {
//        return Prefs.getBoolean(PAYMENT_KOUNT_ENABLED, false);
//    }
//
//    @Override
//    public String getKountMerchantId() {
//        return Prefs.getString(PAYMENT_KOUNT_MERCHANT_ID, "");
//    }
//
//    @Override
//    public boolean isDebugMode() {
//        return Prefs.getBoolean(DEBUG_MODE, true);
//    }
//
//    @Override
//    public String getFbSecret() {
//        return Prefs.getString(FB_SECRET, "");
//    }


}
