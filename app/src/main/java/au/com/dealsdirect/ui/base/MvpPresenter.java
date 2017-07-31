package au.com.dealsdirect.ui.base;


import com.androidnetworking.error.ANError;

import au.com.dealsdirect.data.network.ApiCallback;
import io.reactivex.Observable;

/**
 * Every presenter in the app must either implement this interface or extend BasePresenter
 * indicating the MvpView type that wants to be attached with.
 */
public interface MvpPresenter<V extends MvpView> {

    void onAttach(V mvpView);

    void onDetach();

    void handleApiError(ANError error);

    boolean isTablet();

    void doApiCallForResponse(Observable observable, ApiCallback callback);

}
