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

    public static final int MAX_SCALED_BITMAP_HEIGHT = 500;
    public static final int MAX_SCALED_BITMAP_WIDTH = 500;
    public static final int IMAGE_SIZE = 100;
    public static final int MAX_IMAGE_COUNT = 3;
    public static final int REQUEST_CODE_PERMISSION = 2000;
    public static final int REQUEST_CODE_FOR_SUCCESS = 1000;

    public static final String VCO = "VCO";
    public static final String REGULAR = "REGULAR";
    public static final String OURPAY = "OURPAY";
    public static final String MASTERPASS = "MASTERPASS";
    public static final String PAYPALCREDIT = "PAYPALCREDIT";
    public static final String PAYPAL = "PAYPAL";
    public static final String AFTERPAY = "AFTERPAY";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String VISA = "VISA";
    public static final String MASTERCARD = "MASTERCARD";
    public static final String STRIPE = "stripe";
    public static final String BRAINTREE = "braintree";
    public static final String USE_STRIPE_SDK = "use_stripe_sdk";
    public static final String BASKET_CHANGED = "basket_changed";
    public static final String AMEX = "amex";
    public static final String AMERICAN_EXPRESS = "American Express";

    public static final int ADD_TO_CART_PHONE = 6;
    public static final int ADD_TO_CART_TABLET = 7;


    private AppConstants() {
        // This utility class is not publicly instantiable
    }
}
