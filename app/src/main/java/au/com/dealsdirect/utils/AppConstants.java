package au.com.dealsdirect.utils;


import android.annotation.SuppressLint;

import com.google.common.io.Resources;

import au.com.dealsdirect.R;

public final class AppConstants {

    public static final String STATUS_CODE_SUCCESS = "success";
    public static final String STATUS_CODE_FAILED = "failed";

    public static final int API_STATUS_CODE_LOCAL_ERROR = 0;

    public static final String DB_NAME = "mindorks_mvp.db";
    public static final String PREF_NAME = "MyPrefsFile";

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

    public static final String MP_DATE_TIME_FORMAT = "dd/MM/yyyy hh:mm a";

    public static final String API_REGISTER = "Registration";
    public static final String API_REGISTER_FACEBOOK = "LoginFacebook";

    public static final String PARAM_SKUID = "skuid";


    private AppConstants() {
        // This utility class is not publicly instantiable
    }
}
