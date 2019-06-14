package au.com.dealsdirect.ui.controller.register;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.content.Context;

import au.com.dealsdirect.ui.base.AuthenticationMvpPresenter;
import au.com.dealsdirect.ui.controller.gdpr.GdprMvpPresenter;

public interface RegisterMvpPresenter<V extends RegisterMvpView> extends AuthenticationMvpPresenter<V> {

    void registerUser(Context context, String firstName, String lastName, String email, String password,
                      boolean tncAccepted, boolean emailsAccepted);

}
