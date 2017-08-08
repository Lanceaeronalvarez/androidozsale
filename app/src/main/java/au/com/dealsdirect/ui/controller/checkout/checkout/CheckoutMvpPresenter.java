package au.com.dealsdirect.ui.controller.checkout.checkout;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface CheckoutMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void start();

    void fetchCartDetails();

    void fetchUserPaymentMethods();

    void fetchAdjustItemQuantity(String url, String itemID);

    boolean isCartAlreadyLoadedOnce();

    void resetIsCartAlreadyLoaded();

    boolean checkIsLoggedIn();

    void generateOurpay();
}
