
package au.com.dealsdirect.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.dealsdirect.BuildConfig;
import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.custom.DelayedProgressDialog;

public final class CommonUtils {

    private static final String TAG = "CommonUtils";
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    private static final String KEY_ID = "_id";

    protected static final String PREFS_NAME = "Sale_Items_" + BuildConfig.FLAVOR;

    private CommonUtils() {
        // This utility class is not publicly instantiable
    }

    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static ProgressDialog showLoadingDialogOurpay(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        progressDialog.setContentView(R.layout.ourpay_loading_indicator_layout);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static ProgressDialog showLoadingDialogGPay(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        progressDialog.setContentView(R.layout.gpay_loading_indicator_layout);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static ProgressDialog showLoadingDialogAfterpay(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        progressDialog.setContentView(R.layout.afterpay_loading_indicator_layout);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static ProgressDialog showLoadingDialogLPay(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        progressDialog.setContentView(R.layout.lpay_loading_indicator_layout);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static ProgressDialog showLoadingDialogOpenpay(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        progressDialog.setContentView(R.layout.openpay_loading_indicator_layout);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static Dialog showLoadingDialog(Context context, String message, boolean cancelable) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_dialog_modal);

        ((TextView) dialog.findViewById(R.id.progress_dialog_modal_text)).setText(message);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);

        return dialog;
    }

    public static ProgressDialog showLoadingDialogDelayed(Context context, int delay) {
        DelayedProgressDialog progressDialog = new DelayedProgressDialog(context);
        progressDialog.show(delay);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        progressDialog.setContentView(R.layout.delayed_loading_indicator_layout);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    @SuppressLint("all")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static boolean isEmailValid(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String loadJSONFromAsset(Context context, String jsonFileName)
            throws IOException {

        AssetManager manager = context.getAssets();
        InputStream is = manager.open(jsonFileName);

        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        return new String(buffer, "UTF-8");
    }

    public static String getTimeStamp() {
        return new SimpleDateFormat(AppConstants.TIMESTAMP_FORMAT, Locale.US).format(new Date());
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void showAdmob(Context context, View view, String unitId) {
        AdView adView = new AdView(context);
        adView.setAdSize(AdSize.SMART_BANNER);

        if (BuildConfig.DEBUG || BuildConfig.IS_TEST) {
            adView.setAdUnitId(context.getResources().getString(R.string.admob_test_unit_id));
        } else {
            adView.setAdUnitId(unitId);
        }

        ((RelativeLayout) view).addView(adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public static void showAdmob(Activity activity, View view, String unitId, int width) {
        AdView adView = new AdView(activity);
        AdSize adSize = new AdSize(width, 90);
        adView.setAdSize(adSize);

        if (BuildConfig.DEBUG || BuildConfig.IS_TEST) {
            adView.setAdUnitId(activity.getResources().getString(R.string.admob_test_unit_id));
        } else {
            adView.setAdUnitId(unitId);
        }

        ((RelativeLayout) view).addView(adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    public static void saveSaleItem(Context context, String itemId, String seoIdentifier, String saleId) {
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(itemId, seoIdentifier);
        editor.putString(itemId + KEY_ID, saleId);
        editor.apply();
    }

    public static void clearSaleItem(Context context) {
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.clear();
        editor.apply();
    }

    public static String loadSaleItem(Context context, String itemName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(itemName, "");
    }

    public static String loadSaleId(Context context, String itemID) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(itemID + KEY_ID, "");
    }

    public static void shakeView(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.shake));
    }

    public static void fadeInView(View view, AnimatorListenerAdapter listener) {
        view.setAlpha(0f);
        view.animate()
                .alpha(1f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        view.setAlpha(1f);
                        if (listener != null) {
                            listener.onAnimationCancel(animation);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (listener != null) {
                            listener.onAnimationEnd(animation);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        super.onAnimationRepeat(animation);
                        if (listener != null) {
                            listener.onAnimationRepeat(animation);
                        }
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        if (listener != null) {
                            listener.onAnimationStart(animation);
                        }
                    }

                    @Override
                    public void onAnimationPause(Animator animation) {
                        super.onAnimationPause(animation);
                        if (listener != null) {
                            listener.onAnimationPause(animation);
                        }
                    }

                    @Override
                    public void onAnimationResume(Animator animation) {
                        super.onAnimationResume(animation);
                        if (listener != null) {
                            listener.onAnimationResume(animation);
                        }
                    }
                });
    }

    public static void fadeOutView(View view, AnimatorListenerAdapter listener) {
        view.setAlpha(1f);
        view.animate()
                .alpha(0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        view.setAlpha(0f);
                        if (listener != null) {
                            listener.onAnimationCancel(animation);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (listener != null) {
                            listener.onAnimationEnd(animation);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        super.onAnimationRepeat(animation);
                        if (listener != null) {
                            listener.onAnimationRepeat(animation);
                        }
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        if (listener != null) {
                            listener.onAnimationStart(animation);
                        }
                    }

                    @Override
                    public void onAnimationPause(Animator animation) {
                        super.onAnimationPause(animation);
                        if (listener != null) {
                            listener.onAnimationPause(animation);
                        }
                    }

                    @Override
                    public void onAnimationResume(Animator animation) {
                        super.onAnimationResume(animation);
                        if (listener != null) {
                            listener.onAnimationResume(animation);
                        }
                    }
                });
    }

    private final static Pattern htmlRegexPattern = Pattern.compile("<(br|basefont|hr|input|source|frame|param|area|meta|!--|col|link|option|base|img|wbr|!DOCTYPE).*?>|<(a|abbr|acronym|address|applet|article|aside|audio|b|bdi|bdo|big|blockquote|body|button|canvas|caption|center|cite|code|colgroup|command|datalist|dd|del|details|dfn|dialog|dir|div|dl|dt|em|embed|fieldset|figcaption|figure|font|footer|form|frameset|head|header|hgroup|h1|h2|h3|h4|h5|h6|html|i|iframe|ins|kbd|keygen|label|legend|li|map|mark|menu|meter|nav|noframes|noscript|object|ol|optgroup|output|p|pre|progress|q|rp|rt|ruby|s|samp|script|section|select|small|span|strike|strong|style|sub|summary|sup|table|tbody|td|textarea|tfoot|th|thead|time|title|tr|track|tt|u|ul|var|video).*?<\\/\\2>");

    public static boolean checkIfStringHasHtmlElements(String source) {
        return htmlRegexPattern.matcher(source).find();
    }

    public static Activity getActivityFromView(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public static boolean isActivityOfViewDestroyed(View view) {
        final Activity activity = getActivityFromView(view);
        return activity == null || activity.isDestroyed();
    }
}
