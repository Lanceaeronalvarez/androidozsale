package au.com.dealsdirect.data.auth;

import android.content.Context;

import com.mysale.genie.utility.Prefs;
import com.mysale.genie.utility.RxBus;

import javax.inject.Inject;

import au.com.dealsdirect.data.AppDataManager;

/**
 * dp Created by Admin on 6/19/17.
 */

public class Auth {

    public static final String EVENT_LOGIN_START = "event_login_start";
    public static final String EVENT_LOGIN_END = "event_login_end";
    public static final String EVENT_LOGOUT = "event_logout";
    public static final String EVENT_PRE_LOGOUT = "event_logout_start";
    public static final String EVENT_NOT_AUTHENTICATED = "event_not_authenticated";

    public static final String LOGIN_TICKET = "auth_login_ticket";
    public static final String IS_LOGGED_IN = "auth_is_logged_in";

    @Inject
    AppDataManager appDataManager;

    public void didLogin(Context context, String ticket) {
        Prefs.putBoolean(IS_LOGGED_IN, true);

        setLoginTicket(ticket);

        appDataManager.callGetAppSettings(context, appDataManager.getCountryId());
        RxBus.instance().post(EVENT_LOGIN_END);
    }

    public static String getLoginTicket() {
        return Prefs.getString(LOGIN_TICKET, "");
    }

    public static void setLoginTicket(String ticket) {
        Prefs.putString(LOGIN_TICKET, ticket);
    }

    public static boolean isLoggedIn() {

        boolean isLoggedIn = Prefs.getBoolean(IS_LOGGED_IN, false);

        return (isLoggedIn && getLoginTicket().length() > 1);
    }

    public static void clearAuth() {
        Prefs.putBoolean(IS_LOGGED_IN, false);
        setLoginTicket("");

        //Clear Cookies
//        GCookieJar.getInstance().clear();

    }
}
