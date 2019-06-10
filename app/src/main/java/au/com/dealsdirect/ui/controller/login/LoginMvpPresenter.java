package au.com.dealsdirect.ui.controller.login;
/*
 * Created by CodeineBot on 6/15/17.
 */

import android.content.Context;

import au.com.dealsdirect.ui.base.AuthenticationMvpPresenter;

public interface LoginMvpPresenter<V extends LoginMvpView> extends AuthenticationMvpPresenter<V> {

    boolean loginViaEmail(Context context, String username, String password);
}
