package au.com.dealsdirect.ui.controller.login;
/*
 * Created by CodeineBot on 6/15/17.
 */

import au.com.dealsdirect.ui.base.MvpPresenter;

public interface LoginMvpPresenter<V extends LoginMvpView> extends MvpPresenter<V> {

    boolean loginViaEmail(String username, String password);

    boolean loginViaFacebook(String email, String firstName,
                             String lastName, String facebookUserID,
                             String facebookCookieValue);

    boolean logout();

    boolean loginTicket(String ticket, String countryId);
}
