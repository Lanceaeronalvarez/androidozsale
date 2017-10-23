package au.com.dealsdirect.ui.controller.login;
/*
 * Created by CodeineBot on 6/15/17.
 */

import au.com.dealsdirect.ui.base.AuthenticationMvpPresenter;

public interface LoginMvpPresenter<V extends LoginMvpView> extends AuthenticationMvpPresenter<V> {

    boolean loginViaEmail(String username, String password);

}
