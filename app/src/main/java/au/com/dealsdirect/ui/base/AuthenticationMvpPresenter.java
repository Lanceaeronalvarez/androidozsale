package au.com.dealsdirect.ui.base;


import android.app.Activity;

import com.androidnetworking.error.ANError;
import com.facebook.CallbackManager;

import au.com.dealsdirect.data.network.ApiCallback;
import au.com.dealsdirect.ui.controller.gdpr.GdprMvpPresenter;
import io.reactivex.Observable;

/**
 * Every presenter in the app must either implement this interface or extend BasePresenter
 * indicating the MvpView type that wants to be attached with.
 */
public interface AuthenticationMvpPresenter<V extends MvpView> extends GdprMvpPresenter<V> {

//    void setUserAsLoggedOut();
//
//    void doApiCallForObjectResponse(Observable observable, ApiCallback callback);
//
//    void doApiCallForListResponse(Observable observable, ApiCallback callback);

    boolean loginViaFacebook(String facebookUserID,
                             String facebookCookieValue,
                             boolean tcAccepted, boolean emailsAccepted,
                             String accessToken);

    void onFacebookLogin(Activity activity, CallbackManager callbackManager, int isRegister,
                         boolean tcAccepted, boolean emailsAccepted);


}
