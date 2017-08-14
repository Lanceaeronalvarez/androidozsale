package au.com.dealsdirect.utils;
/*
 * Created by CodeineBot on 7/19/17.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.mysale.genie.utility.Prefs;
import com.mysale.genie.utility.RxBus;
import com.mysale.genie.utility.config.model.getappsettingssection.Android;

import java.util.List;

import au.com.dealsdirect.R;


public class IntrospectionUtils {

    public static final String EVENT_VERSION_INTROSPECTION = "VersionIntrospection";

    public static final String EVENT_LAUNCH = "onLaunch";
    public static final String EVENT_LOGIN = "onLogin";
    public static final String EVENT_LOGOUT = "onLogout";
    public static final String EVENT_ADD_TO_CART = "onAddToCart";
    public static final String EVENT_CHECKOUT_SCREEN = "onCheckoutScreen";
    public static final String EVENT_PAY = "onPay";

    public static final String ACTION_SHOW_NONDISMISSABLE_ALERT = "showNonDismissibleAlert";
    public static final String ACTION_SHOW_DISMISSABLE_ALERT = "showDismissibleAlert";
    public static final String ACTION_SHOW_WEBVIEW = "showWebview";

    public static final String VERSION_CODE = "g_version_code";
    public static final String VERSION_NAME = "g_version_name";
    public static final String VERSION_SHOW = "g_version_show";

    public static void verifyVersion(Context context) {
        int savedVersionCode = getSavedVersionCode();
        int currentVersionCode = getVersionCode(context);

        if (savedVersionCode == 0 || savedVersionCode != currentVersionCode) {

            //App is on initial install or updated
            saveVersionToPref(currentVersionCode, getVersionName(context));
        }
    }

    public static void checkVersion(Context context, List<Android> versions) {

        //Clear subscription
        RxBus.instance().unSubscribeAll();
        if (versions == null || versions.isEmpty()) return;

        Version currentVersion = new Version(getVersionName(context));

        Android closestVersion = null;
        for (int i = 0; i < versions.size(); i++) {
            Android android = versions.get(i);

            Version tempVersion = new Version(android.getVersionNo());

            if (currentVersion.compareTo(tempVersion) == 0) {
                closestVersion = android;

                break;
            } else if (currentVersion.compareTo(tempVersion) == -1) {
                closestVersion = android;
            }
        }

        if (closestVersion != null) {
            callVersionPayload(context, closestVersion);
        }
    }

    private static void callVersionPayload(Context context, Android android) {

        RxBus.instance().subscribe(action -> {

            if (action instanceof String) {
                String sAction = (String) action;

                if (sAction.equalsIgnoreCase(android.getEvent())) {

                    if (!Prefs.getBoolean(VERSION_SHOW, false)) {
                        showPayload(context, android);
                        Prefs.putBoolean(VERSION_SHOW, true);
                    }
                }
            }

        });

        //On init call
        if (android.getEvent().equalsIgnoreCase(EVENT_LAUNCH)) {
            RxBus.instance().post(EVENT_LAUNCH);
        }
    }

    private static void showPayload(Context context, Android android) {

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_version_introspection);

        TextView title = (TextView) dialog.findViewById(R.id.dialog_version_title);
        TextView message = (TextView) dialog.findViewById(R.id.dialog_version_message);
        WebView webview = (WebView) dialog.findViewById(R.id.dialog_version_webview);
        Button button = (Button) dialog.findViewById(R.id.dialog_version_button);

        title.setText(android.getPayload().getTitle());
        message.setText(android.getPayload().getMessage());

        if (android.getAction().equalsIgnoreCase(ACTION_SHOW_DISMISSABLE_ALERT)) {
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(view -> dialog.dismiss());
        } else if (android.getAction().equalsIgnoreCase(ACTION_SHOW_NONDISMISSABLE_ALERT)) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        } else if (android.getAction().equalsIgnoreCase(ACTION_SHOW_WEBVIEW)) {
            webview.setVisibility(View.VISIBLE);
            webview.loadUrl(android.getPayload().getUrl());
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
        }

        dialog.show();
    }

    private static int getVersionCode(Context context) {
        int versionCode;
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);

            versionCode = packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            versionCode = 0;
        }

        return versionCode;
    }

    private static String getVersionName(Context context) {
        String versionName;
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);

            versionName = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            versionName = "";
        }

        return versionName;
    }

    private static void saveVersionToPref(int versionCode, String versionName) {
        Prefs.putInt(VERSION_CODE, versionCode);
        Prefs.putString(VERSION_NAME, versionName);
    }

    private static int getSavedVersionCode() {
        return Prefs.getInt(VERSION_CODE, 0);
    }

    private static String getSavedVersionName() {
        return Prefs.getString(VERSION_NAME, "");
    }

    static class Version implements Comparable<Version> {
        private String version;

        public final String get() {
            return this.version;
        }

        public Version(String version) {
            if (version == null)
                throw new IllegalArgumentException("Version can not be null");
            if (!version.matches("[0-9]+(\\.[0-9]+)*"))
                throw new IllegalArgumentException("Invalid version format");
            this.version = version;
        }

        @Override
        public int compareTo(Version that) {
            if (that == null)
                return 1;
            String[] thisParts = this.get().split("\\.");
            String[] thatParts = that.get().split("\\.");
            int length = Math.max(thisParts.length, thatParts.length);
            for (int i = 0; i < length; i++) {
                int thisPart = i < thisParts.length ?
                        Integer.parseInt(thisParts[i]) : 0;
                int thatPart = i < thatParts.length ?
                        Integer.parseInt(thatParts[i]) : 0;
                if (thisPart < thatPart)
                    return -1;
                if (thisPart > thatPart)
                    return 1;
            }
            return 0;
        }

        @Override
        public boolean equals(Object that) {
            if (this == that)
                return true;
            if (that == null)
                return false;
            if (this.getClass() != that.getClass())
                return false;
            return this.compareTo((Version) that) == 0;
        }
    }
}
