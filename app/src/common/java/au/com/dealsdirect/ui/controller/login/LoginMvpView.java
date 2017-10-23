package au.com.dealsdirect.ui.controller.login;
/*
 * Created by CodeineBot on 6/15/17.
 */

import au.com.dealsdirect.ui.base.AuthenticationMvpView;

public interface LoginMvpView extends AuthenticationMvpView {

    void showLoginSuccessful(String loginTicket);

    void showLoginError(String message);

    void showRegistration();

    void showForgotPassword();
}
