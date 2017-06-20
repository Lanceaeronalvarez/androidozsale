package au.com.dealsdirect.data.auth;

/**
 * Created by smartwave on 20/06/2017.
 */

public interface AuthHelper {

    void acknowledgeAuth(String loginTicket);

    void revokeAuth();

    void setLoginTicket(String loginTicket);

    String getLoginTicket();

    boolean isAuthorized();
}
