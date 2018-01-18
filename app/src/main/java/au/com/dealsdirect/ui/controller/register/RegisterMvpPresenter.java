package au.com.dealsdirect.ui.controller.register;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.app.Activity;

import au.com.dealsdirect.ui.base.AuthenticationMvpPresenter;
import au.com.dealsdirect.ui.base.BaseActivity;

public interface RegisterMvpPresenter<V extends RegisterMvpView> extends AuthenticationMvpPresenter<V> {

    void registerUser(String firstName, String lastName, String email, String password,
                      boolean hasReadTermsAndConditions);

    void facebookRegisterAnalytics(Activity activity);

}
