package au.com.dealsdirect.ui.base;


import android.app.Activity;

import com.androidnetworking.error.ANError;
import com.facebook.CallbackManager;

import au.com.dealsdirect.data.network.ApiCallback;
import io.reactivex.Observable;

/**
 * Every presenter in the app must either implement this interface or extend BasePresenter
 * indicating the MvpView type that wants to be attached with.
 */
public interface AuthenticationMvpPresenter<V extends AuthenticationMvpView> {

    void onAttach(V mvpView);

    void onDetach();

    void handleApiError(ANError error);

    boolean isTablet();

    void setUserAsLoggedOut();

    void doApiCallForObjectResponse(Observable observable, ApiCallback callback);

    void doApiCallForListResponse(Observable observable, ApiCallback callback);

    boolean loginViaFacebook(String email, String firstName,
                             String lastName, String facebookUserID,
                             String facebookCookieValue);

    void onFacebookLogin(Activity activity, CallbackManager callbackManager);


}
