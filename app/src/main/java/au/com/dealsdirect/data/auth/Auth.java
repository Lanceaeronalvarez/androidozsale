package au.com.dealsdirect.data.auth;

import com.mysale.genie.utility.Prefs;

import javax.inject.Inject;

import au.com.dealsdirect.data.pref.PreferencesHelper;
import au.com.dealsdirect.utils.CookieUtils;
import okhttp3.Cookie;

/**
 * Created by smartwave on 20/06/2017.
 */

public class Auth implements AuthHelper {

    private final String IS_LOGGED_IN = "KEY_IS_LOGGED_IN";
    private final String LOGIN_TICKET = "KEY_LOGIN_TICKET";

    @Override
    public void acknowledgeAuth(String loginTicket) {
        Prefs.putBoolean(IS_LOGGED_IN, true);
        setLoginTicket(loginTicket);
    }

    @Override
    public void revokeAuth() {
        Prefs.putBoolean(IS_LOGGED_IN, false);
        setLoginTicket("");
        CookieUtils.getInstance().clear();
    }

    @Override
    public void setLoginTicket(String loginTicket) {
        Prefs.putString(LOGIN_TICKET, loginTicket);
    }

    @Override
    public String getLoginTicket() {
        return Prefs.getString(LOGIN_TICKET, "");
    }

    @Override
    public boolean isAuthorized() {
        return (Prefs.getBoolean(IS_LOGGED_IN,false) && getLoginTicket().length() > 1);
    }
}
