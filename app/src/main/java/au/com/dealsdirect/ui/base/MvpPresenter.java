package au.com.dealsdirect.ui.base;


import com.androidnetworking.error.ANError;

import au.com.dealsdirect.data.network.ApiCallback;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Every presenter in the app must either implement this interface or extend BasePresenter
 * indicating the MvpView type that wants to be attached with.
 */
public interface MvpPresenter<V extends MvpView> {

    void onAttach(V mvpView);

    void onDetach();

    void handleApiError(ANError error);

    void setLastCartRedirection(String lastRedirection);

    boolean hasActiveCheckoutSession();

    void setActiveCheckoutSessionFalse();

    boolean isTablet();

    boolean isGdprDisabled();

    void setIsNewUser(boolean isNewUser);

    boolean getIsNewUser();

    Disposable doApiCallForResponse(Observable observable, ApiCallback callback);

}
