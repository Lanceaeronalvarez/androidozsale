package au.com.dealsdirect.utils;

import android.content.Context;

import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.com.dealsdirect.ui.controller.main.Settings;
import okhttp3.Cookie;

/**
 * Created by smartwave on 20/06/2017.
 */

public class CookieUtils {

    private static PersistentCookieJar sCookieJar;

    public static synchronized void initCookieJar(Context context) {
        if (sCookieJar == null) {
            sCookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        }
    }

    public static synchronized PersistentCookieJar getInstance() {
        return sCookieJar;
    }

    public static void addCookie(Map<String, String> cookieValues){
        String url = Settings.getSelectedCountry().genieRoot.toString();
        url = url.replace("https://www.", "");
        url = url.substring(0, url.length() - 1);
        List<Cookie> validCookies = new ArrayList<>();
        for (Map.Entry<String, String> entry : cookieValues.entrySet()) {
            Cookie cookie = new Cookie.Builder()
                    .domain(url)
                    .path("/")
                    .name(entry.getKey())
                    .value(entry.getValue())
                    .secure()
                    .build();
            validCookies.add(cookie);
        }
        getInstance().getCookieCache().addAll(validCookies);
    }
}
