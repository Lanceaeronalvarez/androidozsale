package au.com.dealsdirect.data.auth;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.mysale.genie.utility.Prefs;

import javax.inject.Inject;
import au.com.dealsdirect.utils.CookieUtils;

/**
 * Created by smartwave on 20/06/2017.
 */

public class Auth implements AuthHelper {

    public static AuthHandler sAuthHandler = null;

    private final String IS_LOGGED_IN = "KEY_IS_LOGGED_IN";
    private final String LOGIN_TICKET = "KEY_LOGIN_TICKET";

    @Inject
    public Auth(){

    }

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

    public void invokeLogin(Router router, AuthHandler handler) {
//        router.pushController(RouterTransaction.with(LoginController.newInstance())
//                .pushChangeHandler(new VerticalChangeHandler())
//                .popChangeHandler(new VerticalChangeHandler()));

        Auth.sAuthHandler = handler;
    }

    public void onAuthHandlerSuccess(){
        if (sAuthHandler!=null){
            sAuthHandler.success();
        }
    }
}
