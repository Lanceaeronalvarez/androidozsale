package au.com.dealsdirect.ui.controller.login;
/*
 * Created by CodeineBot on 6/15/17.
 */

import au.com.dealsdirect.ui.base.MvpView;

public interface LoginMvpView extends MvpView {

    void showLoginSuccessful(String loginTicket);

    void showLoginError(String message);

    void showRegistration();

    void showForgotPassword();
}
