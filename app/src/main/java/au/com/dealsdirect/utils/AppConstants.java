package au.com.dealsdirect.utils;


import android.annotation.SuppressLint;

public final class AppConstants {

    public static final String STATUS_CODE_SUCCESS = "success";
    public static final String STATUS_CODE_FAILED = "failed";

    public static final int API_STATUS_CODE_LOCAL_ERROR = 0;

    public static final String DB_NAME = "mindorks_mvp.db";
    public static final String PREF_NAME = "mindorks_pref";

    public static enum POP_FLAG{
        ROOT,
        BACK
    }

    public static enum AUTH_FLAG{
        REGISTER,
        LOGIN
    }

    public static final long NULL_INDEX = -1L;

    public static final String SEED_DATABASE_OPTIONS = "seed/options.json";
    public static final String SEED_DATABASE_QUESTIONS = "seed/questions.json";

    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";

    public static final String API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String MP2_DATE_FORMAT = "dd/MM/yy";

    public static final String MP_DATE_FORMAT = "dd/MM/yyyy";

    public static final String DD_DATE_FORMAT = "MMMM dd, yyyy";

    public static final String MP_TIME_FORMAT = "hh:mm a";

    // Banners postfix
    public static final int BANNER_MOBILE_WIDTH = 423;
    public static final int BANNER_MOBILE_HEIGHT = 143;

//    public static final int BANNER_TABLET_WIDTH = 313;
//    public static final int BANNER_TABLET_HEIGHT = 294;

    public static final int BANNER_TABLET_WIDTH = 320;
    public static final int BANNER_TABLET_HEIGHT = 193;

    public static final int BANNER_TABLET_FEATURED_WIDTH = 642;
    public static final int BANNER_TABLEt_FEATURED_HEIGHT = 603;

    @SuppressLint("DefaultLocale")
    public static final String BANNER_SIZE_MOBILE = String.format("_%dx%d", BANNER_MOBILE_WIDTH, BANNER_MOBILE_HEIGHT);
    @SuppressLint("DefaultLocale")
    public static final String BANNER_SIZE_TABLET = String.format("_%dx%d", BANNER_TABLET_WIDTH, BANNER_TABLET_HEIGHT);
    @SuppressLint("DefaultLocale")
    public static final String BANNER_SIZE_TABLET_FEATURE = String.format("_%dx%d", BANNER_TABLET_FEATURED_WIDTH, BANNER_TABLEt_FEATURED_HEIGHT);

    public static final String API_REGISTER = "Registration";
    public static final String API_REGISTER_FACEBOOK = "LoginFacebook";


    private AppConstants() {
        // This utility class is not publicly instantiable
    }
}
