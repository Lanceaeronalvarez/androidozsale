package au.com.dealsdirect.utils;

import android.content.Context;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

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
}
