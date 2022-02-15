package au.com.dealsdirect.utils.legacycookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import au.com.dealsdirect.utils.AppLogger;
import okhttp3.Cookie;

/**
 * Created by MTC on 4/10/19.
 */

public class LegacyCookie {

    static ConcurrentHashMap<String, SerializableCookie> cookies =
            new ConcurrentHashMap<String, SerializableCookie>();

    public static boolean hasConsentSaved = false;
    private static SharedPreferences cookiePrefs;

    public static void getLegacyCookie(Context context) {
        cookiePrefs = context.getSharedPreferences("CookiePrefsFile", 0);

        // Load any previously stored cookies into the store
        String storedCookieNames = cookiePrefs.getString("names", null);
        if (storedCookieNames != null) {
            String[] cookieNames = TextUtils.split(storedCookieNames, ",");
            for (String name : cookieNames) {
                String encodedCookie = cookiePrefs.getString("cookie_" + name, null);
                if (encodedCookie != null) {
                    SerializableCookie decodedCookie = decodeCookie(encodedCookie);
                    if (decodedCookie != null) {
                        cookies.put(name, decodedCookie);
                    }
                }
            }
        }
    }

    private static SerializableCookie decodeCookie(String cookieString) {
        byte[] bytes = hexStringToByteArray(cookieString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        SerializableCookie cookie = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cookie = (SerializableCookie) objectInputStream.readObject();
        } catch (IOException e) {
            AppLogger.d("cookie", "IOException in decodeCookie", e);
        } catch (ClassNotFoundException e) {
            AppLogger.d("cookie", "ClassNotFoundException in decodeCookie", e);
        }
        return cookie;
    }

    public static void checkForLegacyConsentCookie(Context context) {
        List<Cookie> listCookie = new ArrayList<>();

        int consentMode = 1;
        boolean hasConsentCookie = false;
        String csCookieValue = "";
        int k0 = 0;

        int prevMode = -1;

        for (SerializableCookie cookie : cookies.values()) {

            hasConsentCookie = cookie.getCookie().getName().contains("cs") &&
                    cookie.getCookie().getValue().equals(Integer.toString(consentMode)) ||
                    cookie.getCookie().getName().equals("cs");

            if (hasConsentCookie) {
                csCookieValue = cookie.getCookie().getValue();

                String[] parts = csCookieValue.split("&");

                for (String part : parts) {
                    int chartAt = part.indexOf("=");
                    String finalValue = part.substring(chartAt + 1);
                    if (part.contains("k0")) {
                        k0 = Integer.parseInt(finalValue);
                    }
                }

                break;
            }

        }

        /* cs is from cookie to check if consent data has been saved
         * sample format csCookieValue=k0=1&k1=PrivacyPolicy_Text&k2=12/04/2019 8:50:04 AM
         * k0 = 0(did not agree) or 1(agree)
         * k1 = privacy policy text
         * k2 = dateTime */

        if (hasConsentCookie && k0 == 1) {
            hasConsentSaved = true;
        } else {
            hasConsentSaved = false;
        }

        if (cookiePrefs != null || cookies.size() != 0) {
            clear();
        }

    }

    protected static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    public static boolean hasConsentSaved() {
        return hasConsentSaved;
    }

    public static void setHasConsentSaved(boolean hasConsentSaved) {
        LegacyCookie.hasConsentSaved = hasConsentSaved;
    }

    private static void clear() {

        // Clear cookies from persistent store
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();

        for (SerializableCookie cookie : cookies.values()) {
            String name = cookie.getCookie().getName();
            String domain = cookie.getCookie().getDomain();
            prefsWriter.remove("cookie_" + name);
        }

        prefsWriter.remove("names");
        prefsWriter.commit();

        cookies.clear();
    }
}
