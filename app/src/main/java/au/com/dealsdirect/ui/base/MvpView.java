package au.com.dealsdirect.ui.base;

import androidx.annotation.StringRes;

import au.com.dealsdirect.utils.LoadingDialogType;

/**
 * Base interface that any class that wants to act as a View in the MVP (Model View Presenter)
 * pattern must implement. Generally this interface will be extended by a more specific interface
 * that then usually will be implemented by an Activity or Fragment.
 */
public interface MvpView {

    boolean isSecurePage();

    void showLoading(LoadingDialogType loadingDialogType);

    void hideLoading();

    void onError(@StringRes int resId);

    void onError(String message);

    boolean isNetworkConnected();

    void hideKeyboard();

    void onRefreshStart();

    void onRefreshEnd();

    void showLoadingDialog(String message, boolean cancelable);

    void hideLoadingDialog();

    void hideNoNetworkLayout();

    void showNoNetworkLayout();

    boolean isViewAttached();

    void showLoadingDelayed(int delay);
}
